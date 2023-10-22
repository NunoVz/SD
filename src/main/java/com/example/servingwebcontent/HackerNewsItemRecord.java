package com.example.servingwebcontent;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) // This indicates that any properties not bound in this type should be
// ignored.
public record HackerNewsItemRecord(
        String id, // The item's unique id.
        Boolean deleted, // true if the item is deleted.
        String type, // The type of item. One of "job", "story", "comment", "poll", or "pollopt".
        String by, // The username of the item's author.
        Long time, // Creation date of the item, in Unix Time.
        String text, // The comment, story or poll text. HTML.
        Boolean dead, // true if the item is dead.
        String parent, // The comment's parent: either another comment or the relevant story.
        Integer poll, // The pollopt's associated poll.
        List kids, // The ids of the item's comments, in ranked display order.
        String url, // The URL of the story.
        Integer score, // The story's score, or the votes for a pollopt.
        String title, // The title of the story, poll or job. HTML.
        List parts, // A list of related pollopts, in display order.
        Integer descendants, // In the case of stories or polls, the total comment count.
        List<Integer> submitted

) {
    public String getId() {
        return id;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public String getType() {
        return type;
    }

    public String getBy() {
        return by;
    }

    public Long getTime() {
        return time;
    }

    public String getText() {
        return text;
    }

    public Boolean getDead() {
        return dead;
    }

    public String getParent() {
        return parent;
    }

    public Integer getPoll() {
        return poll;
    }

    public List getKids() {
        return kids;
    }

    public String getUrl() {
        return url;
    }

    public Integer getScore() {
        return score;
    }

    public String getTitle() {
        return title;
    }

    public List getParts() {
        return parts;
    }

    public Integer getDescendants() {
        return descendants;
    }
    public List<Integer> getSubmitted() {
        return submitted;
    }

}
