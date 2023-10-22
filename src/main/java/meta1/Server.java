package meta1;

import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.io.*;
import java.util.*;


import java.io.*;
import java.net.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import static java.lang.Thread.sleep;

public class Server extends UnicastRemoteObject implements ServerInt {
    SearchInt h;
    private static final int BUFFER_SIZE = 100000;
    private static final String SEPARATOR = " ; ";
    private static final String LINE_SEPARATOR = "\n";
    private static final String KEY_VALUE_SEPARATOR = " | ";
    private static final String LIST_SIZE_SUFFIX = "_count";
    private static final String LIST_ITEM_SEPARATOR = ",";
    private Map<String, ArrayList<String>> failoverMap;

    private HashMap<String, Integer> search = new HashMap<>();
    static String filename;
    static String filenameUrls;


    static String nome;

    static boolean Flag= true;


    Server() throws RemoteException {
        super();
    }

    public String print_on_barrel(String s) throws RemoteException {

        switch (s) {
            case "URL":
                break;
            case "SRC":
                break;
            case "SRR":
                break;
            case "ADM":
                return failoverMap.toString();
            case "LIG":
                break;
            default:
                break;
        }

        return "Wassup";
    }


    public void processString(String str) throws RemoteException{
        if (search.containsKey(str)) {
            int count = search.get(str);
            search.put(str, count + 1);
        } else {
            search.put(str, 1);
        }

        sortHashMap();

        System.out.println("\n------TOP 10------\n");
        for (Map.Entry<String, Integer> entry : search.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public void sortHashMap() {
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(search.entrySet());
        entryList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        search = sortedMap;
    }


    public void printHashMap(HashMap<String, Integer> map) throws RemoteException {
        map = search;
        System.out.println("------TOP 10------\n");
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }


    public Map<String, ArrayList<String>> print_ADM(String s) throws RemoteException {
        System.out.println(failoverMap);
        return failoverMap;

    }

    public HashMap<String, Integer> getTop10() throws RemoteException {
        if (search.size() <= 10) {
            return search;
        }

        HashMap<String, Integer> top10 = new HashMap<>();
        int count = 0;

        for (Map.Entry<String, Integer> entry : search.entrySet()) {
            top10.put(entry.getKey(), entry.getValue());
            count++;

            if (count == 10) {
                break;
            }
        }

        return top10;
    }
    public ArrayList<Url> getUrls(String s) throws RemoteException {

        return ObjectFile(filename,s,null,true);

    }

    public ArrayList<Url> getHrefs(String s) throws RemoteException {

        return ObjectFile2(filenameUrls,s,null,true);

    }

    public String getName() throws RemoteException {

        return nome;

    }

    public static Url receive(MulticastSocket socket) throws Exception {

        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

        String message = new String(packet.getData(), 0, packet.getLength());
        Url url =new Url("RandomStartingUrl" );
        String[] pairs = message.split(SEPARATOR);
        for (String pair : pairs) {
            if(pair.length()>1){
                String[] keyValue = pair.split(KEY_VALUE_SEPARATOR);
                switch (keyValue[0]){
                    case "href":
                        url.setHref(keyValue[2]);
                        break;

                    case "title":
                        System.out.println();
                        url.setTitle(keyValue[2]);
                        break;
                    case "quote":
                        StringBuilder quoteBuilder = new StringBuilder();
                        for (int i = 2; i < keyValue.length; i++) {
                            quoteBuilder.append(keyValue[i]);
                            if (i < keyValue.length - 1) {
                                quoteBuilder.append(" ");
                            }
                        }
                        String quote = quoteBuilder.toString();
                        url.setQ(quote);
                        break;
                    case "score":
                        url.setScore(Integer.parseInt(keyValue[2]));
                        break;
                    case "hrefs":
                        keyValue = pair.split(LIST_ITEM_SEPARATOR);
                        for (int i = 1; i < keyValue.length; i++) {
                            String item = keyValue[i];
                            if (!(url.getHrefs().contains(item)))
                                url.AppendHref(item);
                        }
                        break;
                    case "words":
                        keyValue = pair.split(LIST_ITEM_SEPARATOR);
                        for (int i = 1; i < keyValue.length; i++) {
                            String item = keyValue[i];
                            url.AppendWords(item);
                        }
                        break;


                }




            }
        }


        return url;
    }

    public static void createObjectFile(String filename) {
        Map<String, List<Url>> map = new HashMap<String, List<Url>>();
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(map);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void createObjectFile2(String filename) {
        ArrayList<Url> map = new ArrayList<>();

        try {
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(map);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static synchronized  ArrayList<Url> ObjectFile(String filename, String keyToAdd, Url newUrl, boolean flag) {
        try {

            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Map<String, ArrayList<Url>> map = (Map<String, ArrayList<Url>>) ois.readObject();
            ois.close();
            fis.close();
            if (flag){
                return map.get(keyToAdd);
            }
            else{
                if (map.containsKey(keyToAdd)) {
                    ArrayList<Url> urls = map.get(keyToAdd);
                    urls.add(newUrl);
                } else {
                    ArrayList<Url> urls = new ArrayList<Url>();
                    urls.add(newUrl);
                    map.put(keyToAdd, urls);
                }

                FileOutputStream fos = new FileOutputStream(filename);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(map);
                oos.close();
                fos.close();
                return  null;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return  null;

    }
    public static synchronized  ArrayList<Url> ObjectFile2(String filename, String key, Url newUrl, boolean flag) {
        try {

            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<Url> map = (ArrayList<Url>) ois.readObject();
            ois.close();
            fis.close();
            ArrayList<Url> results = new ArrayList<>();


            if (flag){
                for(Url i:map){
                    if(i.getHref().contains(key))
                        results.add(i);

                }
                return results;

            }
            else{
                map.add(newUrl);

                FileOutputStream fos = new FileOutputStream(filename);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(map);
                oos.close();
                fos.close();
                return  null;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return  null;

    }
    public static void main(String args[]) {
        String a;

        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        filename = args[0];
        filenameUrls = args[1];
        createObjectFile(filename);
        createObjectFile2(filenameUrls);

        try {
            //User user = new User();

            nome = args[2];


            SearchInt h = (SearchInt) LocateRegistry.getRegistry(7500).lookup("XPTO"); //r.lookup("XPTO");
            Server c = new Server();
            h.subscribe(nome, null,(ServerInt) c, true ,Integer.parseInt( args[3]));
            System.out.println("Client sent subscription to server");
            MulticastSocket socket;
            InetAddress group;
            if(Objects.equals(args[3], "1")){
                //A-M
                group = InetAddress.getByName("224.0.0.1");
                socket = new MulticastSocket(5000);
                socket.joinGroup(group);
            }

            else{
                //N-Z

                group = InetAddress.getByName("224.0.0.2");
                socket = new MulticastSocket(5001);
                socket.joinGroup(group);

            }



            InetAddress serverAddress = InetAddress.getByName("127.0.0.1");

            HeartBeat heartBeatThread = new HeartBeat(serverAddress, 9000,nome, group.toString() , Integer.toString(socket.getPort()));
            heartBeatThread.start();

            // Recebe as mensagens do grupo multicast e imprime no console
            while (true) {
                byte[] buffer = new byte[100000];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                Url data = receive(socket);


                ArrayList<String> words = data.getWords();
                System.out.println(words);
                ObjectFile2(filenameUrls,null,data,false);
                for (String word : words) {
                    ObjectFile(filename, word, data,false);

                }

                sleep(2000);



                //readObjectFile(filename);


            }


        } catch (Exception e) {
            System.out.println("Exception in main: " + e);
        }

    }
}
