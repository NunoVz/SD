package meta1;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface UrlInterface extends Remote {

    public void subscribe(String name, DownloaderRMI c) throws RemoteException;
    public String info()throws RemoteException;
    public void  giveInfo(ArrayList<String> urls)throws RemoteException ;



}
