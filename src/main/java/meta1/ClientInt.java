package meta1;

import com.example.servingwebcontent.RMIClient;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

public interface ClientInt extends Remote {

    public void print_on_client(String s) throws RemoteException;


    public void print_ADM(Map<String, ArrayList<String>> s) throws RemoteException;

    public void print_SRC(ArrayList<Url> s) throws RemoteException;

    public void print_SRR(ArrayList<Url> s) throws RemoteException;

    public void print_LIG(ArrayList<Url> s) throws RemoteException;
    public boolean ShowMore() throws IOException;

}
