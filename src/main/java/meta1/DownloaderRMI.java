package meta1;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DownloaderRMI extends Remote {
    public void print_on_client(String s) throws RemoteException;

    }
