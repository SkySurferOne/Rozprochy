package edu.sr.client;

import edu.sr.message.Message;
import edu.sr.message.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ClientOut implements Runnable {
    private final ObjectOutputStream outputStream;
    private final DatagramSocket datagramSocket;
    private final int clientId;
    private final String nick;
    private final String hostName;
    private final int portNumber;

    public ClientOut(ObjectOutputStream outputStream, DatagramSocket datagramSocket,
                     int clientId, String nick, String hostName, int portNumber) {
        this.outputStream = outputStream;
        this.datagramSocket = datagramSocket;
        this.clientId = clientId;
        this.nick = nick;
        this.hostName = hostName;
        this.portNumber = portNumber;
    }

    @Override
    public void run() {
        System.out.println("Welcome in the chat. You can write a messages now.");

        try (Scanner input = new Scanner(System.in)) {
            while (true) {
                String text = input.nextLine();
                Message message = null;

                if (text.equals("M") || text.equals("U")) {
                    sendUDPData(input);
                    message = new Message(clientId, nick, text, MessageType.COMMAND);
                } else {
                    message = new Message(clientId, nick, text, MessageType.NORMAL);
                }
                outputStream.writeObject(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

    }

    private void sendUDPData(Scanner input) {
        byte[] buffer = getMultimediaData(input).getBytes();

        try {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
                    InetAddress.getByName(hostName), portNumber);
            datagramSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getMultimediaData(Scanner input) {
        StringBuilder builder = new StringBuilder(input.nextLine() + "\n");

        while (true) {
            String line = input.nextLine();
            builder.append(line);
            if (line.equals("")) {
                break;
            } else {
                builder.append("\n");
            }
        }

        return builder.toString();
    }

}
