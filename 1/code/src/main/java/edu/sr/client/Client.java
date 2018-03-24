package edu.sr.client;

import edu.sr.message.MessageType;
import edu.sr.message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.*;

public class Client implements Runnable {
    private final String hostName;
    private final int portNumber;
    private final String nick;
    private Socket socket = null;
    private DatagramSocket datagramSocket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private int id;
    private ThreadPoolExecutor executor;

    public Client(String hostName, int portNumber, String nick) throws IOException {
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.nick = nick;
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
        try {
            this.socket = new Socket(hostName, portNumber);
            this.outputStream = new ObjectOutputStream(this.socket.getOutputStream());
            this.inputStream = new ObjectInputStream(this.socket.getInputStream());
            this.datagramSocket = new DatagramSocket();
        } catch (IOException e) {
            if (this.socket != null){
                this.socket.close();
            }
            e.printStackTrace();
            throw new IOException();
        }
    }

    @Override
    public void run() {
        // writing in different thread
        try {
            // Register on server and obtain id
            handShake();

            // create thread for sending messages
            ClientOut clientOut = new ClientOut(outputStream, datagramSocket, id, nick,
                    hostName, portNumber);

            // create thread for receiving messages
            ClientIn clientIn = new ClientIn(inputStream);

            // create thread for receiving udp messages
            ClientUDPIn clientUDPIn = new ClientUDPIn(datagramSocket);

            executor.execute(clientOut);
            executor.execute(clientIn);
            executor.execute(clientUDPIn);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (TooManyClientsException e) {
            e.printStackTrace();
            try {
                disconnect();
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                System.exit(0);
            }
        }
    }

    public void handShake() throws IOException, ClassNotFoundException, TooManyClientsException {
        Message registerMsg = new Message(nick, "Register client", MessageType.NORMAL);
        sendMessage(registerMsg);

        Message response = receiveMessage();
        id = response.getId();
        if (response.getType() == MessageType.ERROR) {
            System.out.println("[ERROR] "+response.getText());
            throw new TooManyClientsException();
        }

        System.out.println("Client gets id: " + id);

        sendDummyUDPData();
        Message registerUDPMsg = new Message(nick, "R", MessageType.COMMAND);
        sendMessage(registerUDPMsg);
    }

    private void sendDummyUDPData() {
        byte[] buffer = "UDP handshake".getBytes();

        try {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
                    InetAddress.getByName(hostName), portNumber);
            datagramSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) {
        try {
            outputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Message receiveMessage()  throws IOException, ClassNotFoundException {
        Message message = null;
        message = (Message) inputStream.readObject();
        return message;
    }

    public void disconnect() throws IOException {
        if (socket != null){
            socket.close();
        }
    }
}
