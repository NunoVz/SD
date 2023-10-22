package com.example.servingwebcontent;

import meta1.SearchInt;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

@Component
public class RMIClient implements WEBint, Serializable {
    private SearchInt searchModule;
    private ArrayList<String> nomes = new ArrayList<>();


    private  SimpMessagingTemplate messagingTemplate;

    public void setMessagingTemplate(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public SearchInt getSearchModule() {
        return searchModule;
    }

    public void setSearchModule(SearchInt searchModule) {
        this.searchModule = searchModule;
    }

    public boolean subscribeWEB(String name) throws RemoteException {
        System.out.println("Subscribing " + name);
        System.out.print("> ");
        if (nomes.contains(name)) {
            return false;
        } else {
            nomes.add(name);
            return true;
        }
    }

    /*
    public void push() throws IOException {
        Thread pushThread = new Thread(() -> {
            try {
                Map<String, ArrayList<String>> lastFailoverMap = null;

                while (true) {
                    Map<String, ArrayList<String>> failoverMap = searchModule.getFailoverMap();
                    HashMap<String, Integer> stats = searchModule.getTop10();
                    if (!failoverMap.equals(lastFailoverMap)) {
                        lastFailoverMap = failoverMap;
                        if (messagingTemplate != null) {
                            messagingTemplate.convertAndSend("/topic/messages", new Message(failoverMap.toString(), "RMI Client"));
                        }
                    }
                    if (messagingTemplate != null) {
                        messagingTemplate.convertAndSend("/topic/messages", new Message(stats.toString(), "RMI Client"));
                    }

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        pushThread.start();
    }
    */



}
