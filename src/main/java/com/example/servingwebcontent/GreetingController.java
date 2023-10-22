package com.example.servingwebcontent;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;

import meta1.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;

@Controller
@Import(RestTemplateConfig.class)
@RequestMapping("/")
public class GreetingController {

    private static RMIClient rmiClient;
    private final SimpMessagingTemplate messagingTemplate;
    private final RMIclientService rmiClientService;


    @Autowired
    public GreetingController(RMIclientService rmiClientService,SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.rmiClientService = rmiClientService;

    }
    @Autowired
    private HttpSession httpSession;


    @PostConstruct
    public void initialize() throws IOException, NotBoundException, InterruptedException {
        SearchInt searchModule = (SearchInt) LocateRegistry.getRegistry(7500).lookup("XPTO");
        rmiClient = new RMIClient();
        rmiClient.setSearchModule(searchModule);
        rmiClient.getSearchModule().subscribeWEB("WEBserver", rmiClient);
        rmiClient.setMessagingTemplate(messagingTemplate);
        rmiClientService.setRmiClient(rmiClient);

    }
    Map<String, ArrayList<String>> lastFailoverMap = null;

    @Scheduled(fixedRate = 5000)
    public  void push() throws IOException {

        Map<String, ArrayList<String>> failoverMap = rmiClient.getSearchModule().getFailoverMap();
        HashMap<String, Integer> stats = rmiClient.getSearchModule().getTop10();
        if (!failoverMap.equals(lastFailoverMap)) {
            lastFailoverMap = failoverMap;
            if (messagingTemplate != null) {
                messagingTemplate.convertAndSend("/topic/messages", new Message(failoverMap.toString(), "RMI Client"));
            }
        }
        if (messagingTemplate != null) {

            if (stats!= null){
                messagingTemplate.convertAndSend("/topic/messages", new Message(stats.toString(), "RMI Client"));

            }
            messagingTemplate.convertAndSend("/topic/messages", new Message(failoverMap.toString(), "RMI Client"));

        }



    }

    @GetMapping("/connect")
    public String showConnectForm(Model model) {
        model.addAttribute("username", new UsernameForm());
        return "connect";
    }

    @PostMapping("/connect")
    public String connect(@ModelAttribute("username") UsernameForm usernameForm, Model model) throws IOException {
        String username = usernameForm.getUsername();
        boolean subscriptionSuccess = rmiClient.subscribeWEB(username);
        if (subscriptionSuccess) {
            model.addAttribute("connected", true);
            model.addAttribute("username", username);
            httpSession.setAttribute("username", username);
        } else {
            model.addAttribute("connected", false);
        }
        return "connected";
    }
    @GetMapping("/IND")
    public String individualPage(@RequestParam("url") String url, Model model) throws IOException {
        System.out.println(url);

        boolean addSuccess = rmiClient.getSearchModule().indexURL(url);
        if (addSuccess) {
            model.addAttribute("added", true);
        } else {
            model.addAttribute("added", false);
        }

        return "urladd";
    }

    @GetMapping("/submit-text/1/{text}")
    public String button1Page(@PathVariable("text") String text, Model model) throws IOException {
        ArrayList<Url> result = rmiClient.getSearchModule().SRCresult(text);

        model.addAttribute("result", result);

        return "button1";
    }

    @GetMapping("/submit-text/2/{text}")
    public String button2Page(@PathVariable("text") String text, Model model) throws IOException {
        ArrayList<Url> result = rmiClient.getSearchModule().SRRresult(text);

        model.addAttribute("result", result);

        return "button2";
    }

    @GetMapping("/LIG")
    public String ligPage(@RequestParam("url") String url, Model model) throws IOException {

        ArrayList<Url> result = rmiClient.getSearchModule().LIGresult(url);
        System.out.println(result);

        model.addAttribute("result", result);


        return "lightml";
    }
    @GetMapping("/ADM")
    public String ligPage( Model model) throws IOException {

        model.addAttribute("result", rmiClient.getSearchModule().getFailoverMap());


        return "admin";
    }
    private static final Logger logger = LoggerFactory.getLogger(GreetingController.class);

    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public Message onMessage(Message message) throws InterruptedException {
        //message.content()...
        //message.setToReverse
        // verificar se é null if message.username=null
        System.out.println("Message received " + message);
        Thread.sleep(1000); // simulated delay // DEVOLVER erro 500 error do lado do server
        return new Message(HtmlUtils.htmlEscape(message.content()), HtmlUtils.htmlEscape(message.username()));
    }


}
