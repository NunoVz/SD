package meta1;

import com.example.servingwebcontent.RMIClient;
import com.example.servingwebcontent.WEBint;

import java.io.*;
import java.net.*;
import java.net.UnknownHostException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.*;


public class SearchModule extends UnicastRemoteObject implements SearchInt {

    static ArrayList<ClientInt> client = new ArrayList<>();
    static ArrayList<String> nomes = new ArrayList<>();

    static WEBint clientWeb;

    static ArrayList<String> nomesBarrel = new ArrayList<>();
    static ArrayList<ServerInt> clientBarrel = new ArrayList<>();
    static ArrayList<String> clientBarrelNames1 = new ArrayList<>();

    static ArrayList<ServerInt> clientBarrel2 = new ArrayList<>();
    static ArrayList<String> clientBarrelNames2 = new ArrayList<>();



    private static int serversocket = 7030;


    private static Map<String, ArrayList<String>> failoverMap;

    private static HeartbeatMonitor monitor;

    private static CopyOnWriteArrayList<String> mens = new CopyOnWriteArrayList<>();



    public SearchModule() throws RemoteException {
        super();
    }

    public static void checkBarrels(String name) throws RemoteException {
        if (clientBarrelNames1.contains(name)) {
            int index = clientBarrelNames1.indexOf(name);
            clientBarrelNames1.remove(index);
            clientBarrel.remove(index);
        } else if (clientBarrelNames2.contains(name)) {
            int index = clientBarrelNames2.indexOf(name);
            clientBarrelNames2.remove(index);
            clientBarrel2.remove(index);
        }
        System.out.println("I removed "+name+" \n");


    }

    public Map<String, ArrayList<String>> getFailoverMap() throws IOException {
        Map<String, ArrayList<String>> modifiedFailoverMap = new HashMap<>();

        for (Map.Entry<String, ArrayList<String>> entry : failoverMap.entrySet()) {
            String key = entry.getKey();
            ArrayList<String> names = entry.getValue();
            ArrayList<String> modifiedNames = new ArrayList<>();


            if (clientBarrelNames1.contains(key)) {

                modifiedNames.add(names + " REPARTICÃO A-M");
            } else if (clientBarrelNames2.contains(key)) {

                modifiedNames.add(names + " REPARTICÃO N-Z");
            } else {
                modifiedNames.add(String.valueOf(names));
            }


            modifiedFailoverMap.put(key, modifiedNames);
        }
        return modifiedFailoverMap;
    }



    public HashMap<String, Integer> getTop10() throws RemoteException{
        if(clientBarrel.size()!=0){
            return clientBarrel.get(0).getTop10();
        }else{
            return null;
        }
    }

    public static ArrayList<Url> sortDescending(ArrayList<Url> numbers) {
        ArrayList<Url> sortedNumbers = new ArrayList<>();

        Collections.sort(numbers, new Comparator<Url>() {

            public int compare(Url o1, Url o2) {
                // compare two instance of `Score` and return `int` as result.
                return ((Integer)o2.getScore()).compareTo((Integer) o1.getScore());
            }
        });
        sortedNumbers.addAll(numbers);
        return numbers;
    }
    Random random = new Random();
    public void print_on_server(String name, String s, boolean flag) throws IOException {


        System.out.println("> " + s);

        for (int i = 0; i < client.size(); i++) {
            if(nomes.get(i).equalsIgnoreCase(name)){
                if(s.length()<5){
                    client.get(i).print_on_client("O comando recebido é demasiado pequeno!"+s+"\n");
                }
                else{
                    switch (s.substring(0, 5)) {
                        case "URL: ":
                            sendURL(s.substring(5));
                            client.get(i).print_on_client(clientBarrel.get(0).print_on_barrel(s.substring(5)));
                            break;
                        case "SRC: ":
                            String search= s.substring(5).toLowerCase();

                            for (int j = 0; j < clientBarrel.size(); j++) {
                                clientBarrel.get(j).processString(search);
                            }


                            String[] keyValue = search.split(" ");
                            int count=keyValue.length;
                            boolean stop=true;
                            int start=0;
                            while(stop){

                                ArrayList<ArrayList<Url>> result = new ArrayList<>();
                                for (String keys : keyValue) {
                                    ArrayList<Url> temp;
                                    char firstLetter = keys.charAt(0);
                                    if (firstLetter >= 'a' && firstLetter <= 'm') {
                                        int randomIndex = random.nextInt(clientBarrel.size());
                                        System.out.println(randomIndex);
                                        System.out.println("Size:"+clientBarrel.size());

                                        temp =  clientBarrel.get(randomIndex).getUrls(keys);
                                        if (temp ==null){
                                            client.get(i).print_on_client("No Links contain the words the User searched for");
                                            break;
                                        }
                                    } else {
                                        int randomIndex = random.nextInt(clientBarrel2.size());
                                        System.out.println("Size:"+clientBarrel.size());
                                        System.out.println(randomIndex);
                                        temp =  clientBarrel2.get(randomIndex).getUrls(keys);
                                        if (temp ==null){
                                            client.get(i).print_on_client("No Links contain the words the User searched for");
                                            break;
                                        }

                                    }
                                    result.add(temp);


                                }



                                ArrayList<ArrayList<String>> hrefs = new ArrayList<>();
                                for (ArrayList<Url> urls : result) {
                                    ArrayList<String> temp = new ArrayList<>();

                                    for (Url url : urls) {
                                        temp.add(url.getHref());
                                    }
                                    hrefs.add(temp);

                                }

                                ArrayList<String> intersection;

                                if (!result.isEmpty() ) {
                                    intersection = new ArrayList<>(hrefs.get(0));
                                    for (int j = 1; j < result.size(); j++) {
                                        intersection.retainAll(hrefs.get(j));
                                    }


                                }else{
                                    client.get(i).print_on_client("No Links contain the words the User searched for");
                                    break;
                                }


                                if(count>1){
                                    ArrayList<Url> finalresult = new ArrayList<Url>();
                                    ArrayList<Url> iterate = new ArrayList<Url>(result.get(0));
                                    for (Url t: iterate){
                                        if(intersection.contains(t.getHref()))
                                            finalresult.add(t);
                                    }
                                    int toIndex = Math.min(start + 10, finalresult.size());
                                    System.out.println("Start "+start+" Index"+toIndex+" Size"+finalresult.size());
                                    List<Url> slicedList = finalresult.subList(start, toIndex);
                                    ArrayList<Url> slicedArrayList = new ArrayList<Url>(slicedList);






                                    client.get(i).print_SRC(slicedArrayList);
                                    if(slicedArrayList.size()< 9 || start+1 >= result.get(0).size())
                                        break;
                                }else{
                                    int toIndex = Math.min(start + 10, result.get(0).size());
                                    List<Url> slicedList = result.get(0).subList(start, toIndex);
                                    ArrayList<Url> slicedArrayList = new ArrayList<Url>(slicedList);
                                    client.get(i).print_SRC(slicedArrayList);

                                    if(slicedArrayList.size()< 9 || start+1 >= result.get(0).size())
                                        break;
                                }
                                stop= client.get(i).ShowMore();
                                start+=10;
                            }
                            break;

                        case "SRR: ":
                            String searchR= s.substring(5).toLowerCase();

                            for (int j = 0; j < clientBarrel.size(); j++) {
                                clientBarrel.get(j).processString(searchR);
                            }

                            String[] keyValueR = searchR.split(" ");
                            int countR=keyValueR.length;

                            boolean stop1=true;
                            int start1=0;
                            while(stop1){

                                ArrayList<ArrayList<Url>> resultR = new ArrayList<>();
                                for (String keys : keyValueR) {
                                    ArrayList<Url> temp;
                                    char firstLetter = keys.charAt(0);
                                    if (firstLetter >= 'a' && firstLetter <= 'm') {
                                        int randomIndex = random.nextInt(clientBarrel.size());
                                        System.out.println(randomIndex);


                                        temp =  clientBarrel.get(randomIndex).getUrls(keys);
                                        if (temp ==null){
                                            client.get(i).print_on_client("No Links contain the words the User searched for");
                                            break;
                                        }
                                    } else {
                                        int randomIndex = random.nextInt(clientBarrel2.size());
                                        System.out.println(randomIndex);


                                        temp =  clientBarrel2.get(randomIndex).getUrls(keys);
                                        if (temp ==null){
                                            client.get(i).print_on_client("No Links contain the words the User searched for");
                                            break;
                                        }

                                    }
                                    resultR.add(temp);


                                }



                                ArrayList<ArrayList<String>> hrefsR = new ArrayList<>();
                                for (ArrayList<Url> urls : resultR) {
                                    ArrayList<String> temp = new ArrayList<>();

                                    for (Url url : urls) {
                                        temp.add(url.getHref());
                                    }
                                    hrefsR.add(temp);

                                }

                                ArrayList<String> intersectionR;

                                if (!resultR.isEmpty() ) {
                                    intersectionR = new ArrayList<>(hrefsR.get(0));
                                    for (int j = 1; j < resultR.size(); j++) {
                                        intersectionR.retainAll(hrefsR.get(j));
                                    }


                                }else{
                                    client.get(i).print_on_client("No Links contain the words the User searched for");
                                    break;
                                }


                                if(countR > 1) {
                                    ArrayList<Url> finalresultR = new ArrayList<Url>();
                                    ArrayList<Url> iterateR = new ArrayList<Url>(resultR.get(0));
                                    //iterar por todos
                                    for (Url t : iterateR) {
                                        if (intersectionR.contains(t.getHref()))
                                            finalresultR.add(t);
                                    }

                                    finalresultR = sortDescending(finalresultR);
                                    System.out.println(finalresultR);

                                    for (int j = 0; j < finalresultR.size(); j++) {
                                        System.out.println(finalresultR.get(j).getScore());
                                    }

                                    int toIndex = Math.min(start1 + 10, finalresultR.size());
                                    System.out.println("Start "+start1+" Index"+toIndex+" Size"+finalresultR.size());
                                    List<Url> slicedList = finalresultR.subList(start1, toIndex);
                                    ArrayList<Url> slicedArrayList = new ArrayList<Url>(slicedList);



                                    if(slicedArrayList.size()< 9 || start1+1 >= resultR.get(0).size())
                                        break;

                                    client.get(i).print_SRR(slicedArrayList);
                                } else {
                                    int toIndex = Math.min(start1 + 10, resultR.get(0).size());
                                    List<Url> slicedList = resultR.get(0).subList(start1, toIndex);
                                    ArrayList<Url> slicedArrayList = new ArrayList<Url>(slicedList);
                                    client.get(i).print_SRR(slicedArrayList);

                                    if(slicedArrayList.size()< 9 || start1+1 >= resultR.get(0).size())
                                        break;
                                }
                                stop1= client.get(i).ShowMore();
                                start1+=10;

                            }



                            break;
                        case "ADM: ":
                            System.out.println("--> Lista de Barrels/Downloaders ativos:");
                            client.get(i).print_ADM(failoverMap);

                            break;
                        case "LIG: ":

                            client.get(i).print_LIG(clientBarrel.get(0).getHrefs(s.substring(5)));
                            break;
                        default:
                            client.get(i).print_on_client("Input inválido\n");
                            break;
                    }

                }

            }
        }


    }

    public boolean indexURL(String s) throws IOException{
        return sendURL(s);
    }



    public ArrayList<Url> SRCresult(String search) throws IOException{

        ArrayList<Url> output = new ArrayList<Url>();
        for (int j = 0; j < clientBarrel.size(); j++) {
            clientBarrel.get(j).processString(search);
        }
        String[] keyValue = search.split(" ");

        int count = keyValue.length;
        boolean stop = true;
        int start = 0;
        while (stop) {
            ArrayList<ArrayList<Url>> result = new ArrayList<>();
            for (String keys : keyValue) {
                ArrayList<Url> temp;
                char firstLetter = keys.charAt(0);
                if (firstLetter >= 'a' && firstLetter <= 'm') {
                    int randomIndex = random.nextInt(clientBarrel.size());


                    temp = clientBarrel.get(randomIndex).getUrls(keys);
                    if (temp == null) {
                        break;
                    }
                } else {
                    int randomIndex = random.nextInt(clientBarrel2.size());
                    temp = clientBarrel2.get(randomIndex).getUrls(keys);
                    if (temp == null) {
                        break;
                    }

                }
                result.add(temp);


            }

            ArrayList<ArrayList<String>> hrefs = new ArrayList<>();
            for (ArrayList<Url> urls : result) {
                ArrayList<String> temp = new ArrayList<>();

                for (Url url : urls) {
                    temp.add(url.getHref());
                }
                hrefs.add(temp);

            }

            ArrayList<String> intersection;

            if (!result.isEmpty()) {
                intersection = new ArrayList<>(hrefs.get(0));
                for (int j = 1; j < result.size(); j++) {
                    intersection.retainAll(hrefs.get(j));
                }


            } else {
                break;
            }


            if (count > 1) {
                ArrayList<Url> finalresult = new ArrayList<Url>();
                ArrayList<Url> iterate = new ArrayList<Url>(result.get(0));
                for (Url t : iterate) {
                    if (intersection.contains(t.getHref()))
                        finalresult.add(t);
                }
                int toIndex = Math.min(start + 10, finalresult.size());
                List<Url> slicedList = finalresult.subList(start, toIndex);
                ArrayList<Url> slicedArrayList = new ArrayList<Url>(slicedList);
                return slicedArrayList;
                //if (slicedArrayList.size() < 9 || start + 1 >= result.get(0).size())
                //  break;
            } else {
                int toIndex = Math.min(start + 10, result.get(0).size());
                List<Url> slicedList = result.get(0).subList(start, toIndex);
                ArrayList<Url> slicedArrayList = new ArrayList<Url>(slicedList);
                return slicedArrayList;

                // if (slicedArrayList.size() < 9 || start + 1 >= result.get(0).size())
                //   break;
            }
            //start += 10;
        }
        return output;

    }

    public ArrayList<Url> LIGresult(String search) throws IOException{
        return clientBarrel.get(0).getHrefs(search);
    }
    public String teste() throws IOException{
        return "Ola";
    }
    public ArrayList<Url> SRRresult(String searchR) throws IOException{
        for (int j = 0; j < clientBarrel.size(); j++) {
            clientBarrel.get(j).processString(searchR);
        }
        ArrayList<Url> output = new ArrayList<Url>();
        String[] keyValueR = searchR.split(" ");
        int countR = keyValueR.length;

        boolean stop1 = true;
        int start1 = 0;
        while (stop1) {
            ArrayList<ArrayList<Url>> resultR = new ArrayList<>();
            for (String keys : keyValueR) {
                ArrayList<Url> temp;
                char firstLetter = keys.charAt(0);
                if (firstLetter >= 'a' && firstLetter <= 'm') {
                    int randomIndex = random.nextInt(clientBarrel.size());

                    temp = clientBarrel.get(randomIndex).getUrls(keys);
                    if (temp == null) {
                        break;
                    }
                } else {
                    int randomIndex = random.nextInt(clientBarrel2.size());

                    temp = clientBarrel2.get(randomIndex).getUrls(keys);
                    if (temp == null) {
                        break;
                    }

                }
                resultR.add(temp);


            }

            ArrayList<ArrayList<String>> hrefsR = new ArrayList<>();
            for (ArrayList<Url> urls : resultR) {
                ArrayList<String> temp = new ArrayList<>();

                for (Url url : urls) {
                    temp.add(url.getHref());
                }
                hrefsR.add(temp);

            }

            ArrayList<String> intersectionR;

            if (!resultR.isEmpty()) {
                intersectionR = new ArrayList<>(hrefsR.get(0));
                for (int j = 1; j < resultR.size(); j++) {
                    intersectionR.retainAll(hrefsR.get(j));
                }


            } else {
                break;
            }


            if (countR > 1) {
                ArrayList<Url> finalresultR = new ArrayList<Url>();
                ArrayList<Url> iterateR = new ArrayList<Url>(resultR.get(0));
                for (Url t : iterateR) {
                    if (intersectionR.contains(t.getHref()))
                        finalresultR.add(t);
                }

                finalresultR = sortDescending(finalresultR);

                int toIndex = Math.min(start1 + 10, finalresultR.size());
                List<Url> slicedList = finalresultR.subList(start1, toIndex);
                ArrayList<Url> slicedArrayList = new ArrayList<Url>(slicedList);

                return slicedArrayList;
            } else {
                int toIndex = Math.min(start1 + 10, resultR.get(0).size());
                List<Url> slicedList = resultR.get(0).subList(start1, toIndex);
                ArrayList<Url> slicedArrayList = new ArrayList<Url>(slicedList);
                return slicedArrayList;


            }

        }
        return output;
    }
    public void subscribe(String name, ClientInt c, ServerInt barrel, boolean flag, int reparticao) throws RemoteException {
        if(flag){

            System.out.println("Subscribing " + name);
            System.out.print("> ");
            if(reparticao==1){
                clientBarrel.add(barrel);
                clientBarrelNames1.add(name);


            }
            else{
                clientBarrel2.add(barrel);
                clientBarrelNames2.add(name);
            }

            nomesBarrel.add(name);
        }
        else{
            System.out.println("Subscribing " + name);
            System.out.print("> ");
            client.add(c);
            nomes.add(name);
            c.print_on_client("Se desejar indexar um URL escreva URL: .\\n\"+\n" +
                    "\"Se desejar procurar por palavras escreva SRC: .\\n\"+\n" +
                    "\"Se desejar procurar por palavras e Ordenar por relevancia escreva SRR: .\\n\"+\n" +
                    "\"Se desejar consultar paginas de uma ligacão escreva LIG: .\"+\n" +
                    "\"Se desejar aceder a pagina de Administracão escreva ADM: .\"");
        }
    }


    public void subscribeWEB(String name, WEBint c) throws RemoteException {
        System.out.println("Subscribing " + name);
        System.out.print("> ");
        clientWeb = c;





    }
    public static boolean sendURL(String answer){

        System.out.println("OLAA");
        try (Socket s = new Socket("0.0.0.0", serversocket)) {
            System.out.println("SOCKET=" + s);
            DataOutputStream out = new DataOutputStream(s.getOutputStream());

            out.writeUTF(answer);
            return true;


        } catch (UnknownHostException e) {
            System.out.println("Sock:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO:" + e.getMessage());
        }
        return false;

    }

    public static void main(String args[]) throws IOException {
        String a;


        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        try {
            //User user = new User();
            SearchModule h = new SearchModule();

            Registry r = LocateRegistry.createRegistry(7500);
            r.rebind("XPTO", h);


            System.out.println("Hello Server ready.");

            try (DatagramSocket socket = new DatagramSocket(9000)) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                monitor = new HeartbeatMonitor();

                while (true) {
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();
                    //System.out.printf("R: %s\n", message);
                    monitor.processHeartbeat(message);
                    failoverMap = monitor.getFailoverMap();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception re) {
            System.out.println("Exception in HelloImpl.main: " + re);
        }
    }
}


class HeartbeatMonitor {
    private static final int MAX_FAILOVER = 3;
    private static final long HEARTBEAT_INTERVAL = 2000; // 1 second
    private static final long MAX_HEARTBEAT_AGE = 3; // 6 seconds
    private Map<String, Long> heartbeatMap;
    private Map<String, ArrayList<String>> failoverMap;

    static ArrayList<ServerInt> clientBarrel = new ArrayList<>();
    static ArrayList<ServerInt> clientBarrel2 = new ArrayList<>();

    public HeartbeatMonitor() {
        heartbeatMap = new HashMap<>();
        failoverMap = new HashMap<>();
        startHeartbeatChecker();
    }

    public void processHeartbeat(String message) {
        String[] parts = message.split("\\|");
        String name = parts[0];
        long time = Long.parseLong(parts[1]);
        ArrayList<String> temp = new ArrayList<>();

        String adress = parts[2];
        String port = parts[3];
        temp.add(adress);
        temp.add(port);

        heartbeatMap.put(name, time); // update the heartbeat time for the given name

        if (!failoverMap.containsKey(name)) {
            failoverMap.put(name,temp);
        }



    }

    private void startHeartbeatChecker() {
        Thread thread = new Thread(() -> {
            while (true) {
                long currentTime = System.currentTimeMillis()/1000;
                List<String> expiredNames = new ArrayList<>();

                for (Map.Entry<String, Long> entry : heartbeatMap.entrySet()) {
                    String name = entry.getKey();
                    long heartbeatTime = entry.getValue();

                    if (currentTime - heartbeatTime > MAX_HEARTBEAT_AGE) {
                        expiredNames.add(name);
                    }
                }

                for (String name : expiredNames) {
                    heartbeatMap.remove(name);
                    failoverMap.remove(name);
                    try {
                        SearchModule.checkBarrels(name);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }

                }

                try {
                    Thread.sleep(HEARTBEAT_INTERVAL);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public Map<String, ArrayList<String>> getFailoverMap() {
        return failoverMap;
    }
}

