package meta1;

import com.example.servingwebcontent.RMIClient;
import com.example.servingwebcontent.WEBint;

import java.io.IOException;
import java.rmi.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface SearchInt extends Remote {

    public void print_on_server(String name, String s, boolean flag) throws IOException;

    public void subscribe(String name, ClientInt c, ServerInt barrel, boolean flag, int reparticao) throws RemoteException;
    public void subscribeWEB(String name, WEBint c) throws RemoteException, InterruptedException;

    public ArrayList<Url> SRCresult(String search) throws IOException;

    public ArrayList<Url> SRRresult(String searchR) throws IOException;

    public ArrayList<Url> LIGresult(String searchR) throws IOException;

    public  Map<String, ArrayList<String>> getFailoverMap() throws IOException;
    public boolean indexURL(String s) throws IOException;

    public HashMap<String, Integer> getTop10() throws RemoteException;

    //public static boolean checkStatus();
}
