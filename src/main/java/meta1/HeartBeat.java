package meta1;

import java.net.*;

public class HeartBeat extends Thread {
    private final int port;
    private final InetAddress serverAddress;
    private final String name;

    private final String adressSend;
    private final String portSend;



    public HeartBeat( InetAddress serverAddress,int port, String name,String A,String P) {
        this.port = port;
        this.serverAddress = serverAddress;
        this.name=name;
        this.adressSend=A;
        this.portSend=P;

    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket()) {

            while (true) {
                long currentTimeSeconds = System.currentTimeMillis() / 1000;
                byte[] buffer = (name+"|"+currentTimeSeconds+"|"+adressSend+"|"+portSend).getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, port);
                socket.send(packet);
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
