package meta1;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface ServerInt extends Remote {

    public String print_on_barrel(String s) throws RemoteException;

    public Map<String, ArrayList<String>> print_ADM(String s) throws RemoteException;

    public ArrayList<Url> getUrls(String s) throws RemoteException;

    public ArrayList<Url> getHrefs(String s) throws RemoteException ;
    public String getName() throws RemoteException;

    public void processString(String str) throws RemoteException;

    public void sortHashMap() throws RemoteException;

    public HashMap<String, Integer> getTop10() throws RemoteException;

    public void printHashMap(HashMap<String, Integer> map) throws RemoteException;

}
