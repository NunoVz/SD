package com.example.servingwebcontent;
import org.springframework.stereotype.Service;

@Service
public class RMIclientService {
    private RMIClient rmiClient;

    public void setRmiClient(RMIClient rmiClient) {
        this.rmiClient = rmiClient;
    }

    public RMIClient getRmiClient() {
        return rmiClient;
    }


    // Add any other methods or functionality related to the RMI client
}