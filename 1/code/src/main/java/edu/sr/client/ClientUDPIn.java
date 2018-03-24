package edu.sr.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ClientUDPIn implements Runnable {
    private final DatagramSocket datagramSocket;
    private final static int bufferSize = 10480;

    public ClientUDPIn(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] buffer = new byte[bufferSize];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(packet);
                String data = new String(packet.getData()).replace("\0", "");
                System.out.print("[udp]\n"+data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
