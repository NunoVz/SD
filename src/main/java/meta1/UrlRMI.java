package meta1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class UrlRMI implements Runnable {
    UrlManager q;

    UrlRMI(UrlManager q) {
        this.q = q;
        new Thread(this, "URLRmi").start();

    }

    public void run() {

        CopyOnWriteArrayList<String> mens = new CopyOnWriteArrayList<>();
        int serverPort = 7030;
        int numero = 0;
        try (ServerSocket listenSocket = new ServerSocket(serverPort)) {
            System.out.println("A escuta no porto 6000");
            System.out.println("LISTEN SOCKET=" + listenSocket);
            while (true) {

                Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
                System.out.println("CLIENT_SOCKET (created at accept())=" + clientSocket);
                numero++;
                new Connection(clientSocket, numero, mens,q);
            }
        } catch (IOException e) {
            System.out.println("Listen:" + e.getMessage());
        }
    }
}
