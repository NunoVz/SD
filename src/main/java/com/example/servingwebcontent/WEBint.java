package com.example.servingwebcontent;


import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

public interface WEBint extends Remote {

    public boolean subscribeWEB(String name) throws RemoteException;



}
