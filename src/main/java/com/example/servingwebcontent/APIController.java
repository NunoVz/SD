package com.example.servingwebcontent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class APIController {

    private final RestTemplate restTemplate;
    private final RMIclientService rmiClientService;

    @Autowired
    public APIController(RestTemplate restTemplate, RMIclientService rmiClientService) {
        this.restTemplate = restTemplate;
        this.rmiClientService = rmiClientService;

    }

    @GetMapping("/get-top-stories/{text}")
    public List<String> hackerNewsTopStories(@PathVariable String text) throws IOException {
        List<String> matchingUrls = new ArrayList<>();
        RMIClient rmiClient = rmiClientService.getRmiClient();


        ResponseEntity<Integer[]> response = restTemplate.getForEntity("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty", Integer[].class);
        Integer[] topStoryIds = response.getBody();
        String[] searchWords = text.split("\\s+");
        System.out.println("Corri HackerTopStories");

        for (Integer storyId : topStoryIds) {
            ResponseEntity<HackerNewsItemRecord> storyResponse = restTemplate.getForEntity(
                    "https://hacker-news.firebaseio.com/v0/item/" + storyId + ".json?print=pretty",
                    HackerNewsItemRecord.class
            );
            HackerNewsItemRecord story = storyResponse.getBody();

            if (story != null && "story".equals(story.getType()) && story.getText() != null && story.getUrl() != null) {
                String storyText = story.getText().toLowerCase();
                for (String word : searchWords) {
                    if (storyText.contains(word.toLowerCase())) {
                        matchingUrls.add(story.getUrl());
                        rmiClient.getSearchModule().indexURL(story.getUrl());

                        break;
                    }
                }

            }
        }

        return matchingUrls;
    }

    @GetMapping("/index-stories/{text}")
    public List<String> indexStories(@PathVariable String text) throws IOException {
        RMIClient rmiClient = rmiClientService.getRmiClient();

        // Make a request to fetch the user details
        String userEndpoint = "https://hacker-news.firebaseio.com/v0/user/" + text + ".json?print=pretty";
        System.out.println(userEndpoint);
        RestTemplate restTemplate = new RestTemplate();
        System.out.println("Corri IndexStories");

        ResponseEntity<HackerNewsItemRecord> userResponse = restTemplate.getForEntity(userEndpoint, HackerNewsItemRecord.class);
        if (userResponse.getStatusCode().is2xxSuccessful()) {
            HackerNewsItemRecord user = userResponse.getBody();

            if (user != null && user.getSubmitted() != null && !user.getSubmitted().isEmpty()) {
                List<String> indexedUrls = new ArrayList<>();
                System.out.println(user.getId());
                for (Integer itemId : user.getSubmitted()) {
                    String itemEndpoint = "https://hacker-news.firebaseio.com/v0/item/" + itemId + ".json?print=pretty";
                    ResponseEntity<HackerNewsItemRecord> itemResponse = restTemplate.getForEntity(itemEndpoint, HackerNewsItemRecord.class);

                    if (itemResponse.getStatusCode().is2xxSuccessful()) {
                        HackerNewsItemRecord item = itemResponse.getBody();

                        if (item != null && "story".equals(item.getType()) && item.getUrl() != null) {
                            indexedUrls.add(item.getUrl());
                            rmiClient.getSearchModule().indexURL(item.getUrl());
                        }
                    }
                }

                return indexedUrls;
            }
        }



        return Collections.emptyList();
    }
}
