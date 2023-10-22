package meta1;

import org.jsoup.nodes.Element;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;

import java.net.*;
import java.io.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.concurrent.ConcurrentLinkedQueue;


 public class UrlManager  extends UnicastRemoteObject implements UrlInterface {
     static ArrayList<DownloaderRMI> client = new ArrayList<>();
     static ArrayList<String> nomes = new ArrayList<>();

     private ConcurrentLinkedQueue<String> Urls = new ConcurrentLinkedQueue<>();
     private ConcurrentLinkedQueue<String> VistedUrls = new ConcurrentLinkedQueue<>();


     public UrlManager() throws RemoteException {
         super();
     }
     boolean valueSet = false;

     public synchronized String get() {
         while (Urls.isEmpty()) {
             try {
                 wait();
             } catch (InterruptedException e) {
                 System.out.println("InterruptedException caught");
             }
         }
         String temp = Urls.poll();
         System.out.println("Got: " + temp);
         notifyAll();
         return temp;
     }
     public synchronized void put(ArrayList<String> nomes) {

         for (String i : nomes) {
             if(!(VistedUrls.contains(i)) && i.startsWith("https://")){
                 Urls.offer(i);
                 VistedUrls.offer(i);
                 System.out.println("Put: " + i);
             }
         }
         notifyAll();
     }
     public String info(){

         String result= get();

         return result;
     }

     public void  giveInfo(ArrayList<String> urls){
         put(urls);
     }

     public void subscribe(String name, DownloaderRMI c) throws RemoteException {
         System.out.println("Subscribing " + name);
         System.out.print("> ");
         client.add(c);
         nomes.add(name);
         //client = c;
     }
        private static int serverPort = 7030;

        public static void printada(){
            System.out.println("sdagvsajadskldas");
        }
        public static void main(String args[]) throws RemoteException {
            UrlManager q = new UrlManager();
            new UrlRMI(q);
            String a;




            InputStreamReader input = new InputStreamReader(System.in);
            BufferedReader reader = new BufferedReader(input);

            try {

                Registry r = LocateRegistry.createRegistry(7600);
                r.rebind("URL", q);


                System.out.println("Hello Server ready.");
                while (true) {
                    System.out.print("> ");
                    a = reader.readLine();

                }
            } catch (Exception re) {
                System.out.println("Exception in HelloImpl.main: " + re);
            }

        }


 }

class Connection extends Thread {
        DataInputStream in;
        DataOutputStream out;
        Socket clientSocket;
        int thread_number;
        UrlManager q;

        CopyOnWriteArrayList<String> mensagens;

        public Connection (Socket aClientSocket, int numero, CopyOnWriteArrayList<String> mens,UrlManager q) {
            this.q=q;
            this.mensagens = mens;
            thread_number = numero;
            try{
                clientSocket = aClientSocket;
                in = new DataInputStream(clientSocket.getInputStream());
                out = new DataOutputStream(clientSocket.getOutputStream());
                this.start();
            }catch(IOException e){System.out.println("Connection:" + e.getMessage());}
        }
        //=============================
        public void run(){
            String resposta;
            int n = 0;
            try {
                while(true){
                    ArrayList<String> temp = new ArrayList<>();

                    //an echo server
                    String data = in.readUTF();
                    this.mensagens.add(data);
                    System.out.println("T[" + thread_number + "] Recebeu: "+data);
                    temp.add(data);
                    q.put(temp);

                    String packmensagens = "";


                    //resposta=data.toUpperCase();
                    //out.writeUTF(resposta);
                }
            } catch(EOFException e) {
                System.out.println("EOF:" + e);
            } catch(IOException e) {
                System.out.println("IO:" + e);
            }
        }
    }


