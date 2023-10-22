package meta1;

import java.io.*;
import java.net.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import com.example.servingwebcontent.RMIClient;

public class Client extends UnicastRemoteObject implements ClientInt {

    public Client() throws RemoteException {
        super();
    }

    public void print_on_client(String s) throws RemoteException {
        System.out.println("> " + s);

    }

    public void print_Search(String s) throws RemoteException {
        System.out.println("> " + s);
    }

    public void print_ADM(Map<String, ArrayList<String>> s) throws RemoteException {
         System.out.println(s);
    }

    public void print_SRC(ArrayList<Url> s) throws RemoteException {
        if(s== null )
            System.out.println("Não ha resultados tente mais tarde");
        else{
            System.out.println(s);
            for(Url i:s){
                System.out.println("--> Título: " + i.getTitle());
                System.out.println("--> Citação: " + i.getQ());
                System.out.println("--> URL: " + i.getHref());
            }
        }

    }

    public void print_SRR(ArrayList<Url> s) throws RemoteException {
        if(s== null )
            System.out.println("Não ha resultados tente mais tratado");
        else{
            System.out.println(s);
            for(Url i:s){
                System.out.println(i.getHref()+" --- score:  "+ i.getScore());
            }
        }

    }

    public void print_LIG(ArrayList<Url> s) throws RemoteException {
        if(s== null )
            System.out.println("Não ha resultados tente mais tratado");
        else{
            System.out.println(s);
            for(Url i:s){
                System.out.println("-> URLs: " + i.getHref());
            }
        }

    }
    public boolean ShowMore() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        String a;
        System.out.println("Se quiser ver os proximos resultados digite + se quiser sair digite - ");

        System.out.print("> ");
        a = reader.readLine();
        return a.equals("+");

    }

    public static void main(String args[]) {
        String a;

        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        try {
            //User user = new User();

            String nome = args[0];


            SearchInt h = (SearchInt) LocateRegistry.getRegistry(7500).lookup("XPTO"); //r.lookup("XPTO");
            Client c = new Client();
            h.subscribe(nome, (ClientInt) c,null, false,0);
            System.out.println("Client sent subscription to server");
            while (true) {
                System.out.print("> ");
                a = reader.readLine();
                h.print_on_server(nome,a,false);
            }

        } catch (Exception e) {
            System.out.println("Exception in main: " + e);
        }

    }
}
