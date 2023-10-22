package meta1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.*;

public class Url implements Serializable {

    ArrayList<String> words = new ArrayList<>();
    int score=0;
    ArrayList<String> hrefArray = new ArrayList<>();



    String quote;

    String title;



    private String href;


    public Url(String href) {

        this.href= href;
    }
    public String getQ() {
        return quote;
    }
    public void setQ(String quote) {
        this.quote  = quote;
    }
    public void setTitle(String title) {
        this.title  = title;
    }

    public String getTitle() {
        return title;
    }

    public String getHref() {
        return href;
    }
    public void setHref(String href) {
        this.href= href;
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public void setWords(ArrayList<String> words) {
        this.words = words;
    }
    public void AppendWords(String words) {
        (this.words).add(words);
    }

    public void AppendHref(String href) {
        (this.hrefArray).add(href);
    }
    public ArrayList<String> getHrefs() {
        return hrefArray;
    }




    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }




}
