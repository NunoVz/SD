package meta1;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.*;
import java.net.UnknownHostException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.*;
import org.jsoup.Connection.Response;

import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Downloader extends UnicastRemoteObject implements DownloaderRMI {

    //https://gist.github.com/alopes/5358189

    Downloader() throws RemoteException {
        super();
    }

    public void print_on_client(String s) throws RemoteException {
        System.out.println("> " + s);
    }

    public static void main(String args[]) throws RemoteException, InterruptedException {
        int NUM_THREADS = 5;
        ExecutorService executorService;
        executorService = Executors.newFixedThreadPool(NUM_THREADS);
        for (int i = 1; i <= NUM_THREADS; i++) {
            executorService.submit(new DownloaderThread("Down" + i));
        }


    }

}

class DownloaderThread implements Runnable {
    static  String[] stopwords = {"/","-",">","<","|","de", "a", "o", "que", "e", "do", "da", "em", "um", "para", "é", "com", "não", "uma", "os", "no", "se", "na", "por", "mais", "as", "dos", "como", "mas", "foi", "ao", "ele", "das", "tem", "à", "seu", "sua", "ou", "ser", "quando", "muito", "há", "nos", "já", "está", "eu", "também", "só", "pelo", "pela", "até", "isso", "ela", "entre", "era", "depois", "sem", "mesmo", "aos", "ter", "seus", "quem", "nas", "me", "esse", "eles", "estão", "você", "tinha", "foram", "essa", "num", "nem", "suas", "meu", "às", "minha", "têm", "numa", "pelos", "elas", "havia", "seja", "qual", "será", "nós", "tenho", "lhe", "deles", "essas", "esses", "pelas", "este", "fosse", "dele", "tu", "te", "vocês", "vos", "lhes", "meus", "minhas", "teu", "tua", "teus", "tuas", "nosso", "nossa", "nossos", "nossas", "dela", "delas", "esta", "estes", "estas", "aquele", "aquela", "aqueles", "aquelas", "isto", "aquilo", "estou", "está", "estamos", "estão", "estive", "esteve", "estivemos", "estiveram", "estava", "estávamos", "estavam", "estivera", "estivéramos", "esteja", "estejamos", "estejam", "estivesse", "estivéssemos", "estivessem", "estiver", "estivermos", "estiverem", "hei", "há", "havemos", "hão", "houve", "houvemos", "houveram", "houvera", "houvéramos", "haja", "hajamos", "hajam", "houvesse", "houvéssemos", "houvessem", "houver", "houvermos", "houverem", "houverei", "houverá", "houveremos", "houverão", "houveria", "houveríamos", "houveriam", "sou", "somos", "são", "era", "éramos", "eram", "fui", "foi", "fomos", "foram", "fora", "fôramos", "seja", "sejamos", "sejam", "fosse", "fôssemos", "fossem", "for", "formos", "forem", "serei", "será", "seremos", "serão", "seria", "seríamos", "seriam", "tenho", "tem", "temos", "tém", "tinha", "tínhamos", "tinham", "tive", "teve", "tivemos", "tiveram", "tivera", "tivéramos", "tenha", "tenhamos", "tenham", "tivesse", "tivéssemos", "tivessem", "tiver", "tivermos", "tiverem", "terei", "terá", "teremos", "terão", "teria", "teríamos", "teriam"};
    private static final int BUFFER_SIZE = 100000;
    private static final String SEPARATOR = " ; ";
    private static final String LINE_SEPARATOR = "\n";
    private static final String KEY_VALUE_SEPARATOR = " | ";
    private static final String LIST_SIZE_SUFFIX = "_count";
    private static final String LIST_ITEM_SEPARATOR = "_";
    DownloaderThread(String name) {
        new Thread(this,name).start();

    }
    public void send(Map<String, String> data,MulticastSocket socket,InetAddress group,int port) throws Exception {
        StringBuilder message = new StringBuilder();
        for (String key : data.keySet()) {
            message.append(key).append(KEY_VALUE_SEPARATOR).append(data.get(key)).append(SEPARATOR);
        }
        message.append(LINE_SEPARATOR);
        byte[] buffer = message.toString().getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
        socket.send(packet);
    }
    public static Map<String, String> createList(String prefix, String[] items) {
        Map<String, String> data = new HashMap<>();
        data.put(prefix + LIST_SIZE_SUFFIX, Integer.toString(items.length));
        for (int i = 0; i < items.length; i++) {
            data.put(prefix + LIST_ITEM_SEPARATOR + i + "_name", items[i]);
        }
        return data;
    }

    public void run() {

        InetAddress group = null;
        InetAddress group2 = null;
        try {
            group = InetAddress.getByName("224.0.0.1");
            group2 = InetAddress.getByName("224.0.0.2");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        MulticastSocket socket = null;
        MulticastSocket socket2 = null;
        try {
            socket = new MulticastSocket(5000);
            socket2 = new MulticastSocket(5001);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            socket.joinGroup(group);
            socket2.joinGroup(group2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HeartBeat heartBeatThread = null;
        try {
            InetAddress serverAddress = InetAddress.getByName("127.0.0.1");

            heartBeatThread = new HeartBeat((serverAddress), 9000,("["+Thread.currentThread().getName()+"]"),(group.toString()+"|"+group2.toString()),("5000|5001"));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        heartBeatThread.start();





        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        String linkA = null;
        UrlInterface h= null;
        try {

            Registry r = LocateRegistry.getRegistry(7600);
            h = (UrlInterface) r.lookup("URL");

            Downloader c = new Downloader();
            h.subscribe(Thread.currentThread().getName(), (DownloaderRMI) c);
            System.out.println("Client sent subscription to server");



        } catch (Exception e) {
            System.out.println("Exception in main: " + e);
        }


        while (true){
            ArrayList<String> words = new ArrayList<>();
            ArrayList<String> words2 = new ArrayList<>();

            try {
                linkA= h.info();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            Url url =new Url(linkA);
            Url SecondUrl =new Url(linkA);
            try {
                Response response = Jsoup.connect(url.getHref()).execute();

                int statusCode = response.statusCode();
                if (statusCode == 200 ) {
                    Document doc = Jsoup.connect(url.getHref()).get();

                    url.setTitle(doc.title());
                    SecondUrl.setTitle(doc.title());


                    StringTokenizer tokens = new StringTokenizer(doc.text());
                    int countTokens = 0;
                    String[] quote = new String[10];
                    int cont= 0;
                    while (tokens.hasMoreElements() && countTokens++ < 100){
                        String temp= tokens.nextToken().toLowerCase();
                        //Completar com os restantes
                        String[] charsToRemove = { ".", ":", ",",";","(",")", "{","·" };
                        for (String c : charsToRemove) {
                            temp = temp.replace(c, "");
                        }
                        if((!Arrays.asList(stopwords).contains(temp)) && (!words.contains(temp))){
                            char firstLetter = temp.charAt(0);
                            if (firstLetter >= 'a' && firstLetter <= 'm') {
                                words.add(temp);
                            } else {
                                words2.add(temp);
                            }

                        }
                        if(cont<=9){
                            quote[cont]=temp;
                        }
                        cont++;

                    }

                    url.setQ(String.join(" ", quote));
                    url.setWords(words);

                    SecondUrl.setQ(String.join(" ", quote));
                    SecondUrl.setWords(words2);

                    System.out.println(url.getQ());

                    Elements links = doc.select("a[href]");
                    for (Element link : links){
                        url.AppendHref(link.attr("abs:href"));
                        url.setScore(url.getScore()+1);
                        SecondUrl.AppendHref(link.attr("abs:href"));
                        SecondUrl.setScore(url.getScore()+1);
                    }
                    h.giveInfo(url.getHrefs());
                    System.out.print("["+Thread.currentThread().getName()+"] ");
                    System.out.println(url.getHref());
                } else {
                    System.out.println("Connection is not valid. Status code: " + statusCode);
                }

                try {
                    Map<String, String> data = new HashMap<>();


                    //A-M
                    data.put("href", url.getHref());
                    data.put("title", url.getTitle());
                    data.put("quote",  url.getQ());
                    data.put("score", Integer.toString(url.getScore()));
                    data.put("hrefs", url.getHrefs().toString().replaceAll("[\\[\\]\\s]", ""));
                    data.put("words", url.getWords().toString().replaceAll("[\\[\\]\\s]", ""));
                    send(data,socket,group,5000);
                    data = new HashMap<>();

                    //N-Z
                    data.put("href", SecondUrl.getHref());
                    data.put("title", SecondUrl.getTitle());
                    data.put("quote",  SecondUrl.getQ());
                    data.put("score", Integer.toString(SecondUrl.getScore()));
                    data.put("hrefs", SecondUrl.getHrefs().toString().replaceAll("[\\[\\]\\s]", ""));
                    data.put("words", SecondUrl.getWords().toString().replaceAll("[\\[\\]\\s]", ""));
                    send(data,socket,group2,5001);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }



            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

    }
}
