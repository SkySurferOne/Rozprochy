package edu.sr.server;

import edu.sr.message.Message;
import edu.sr.message.MessageType;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

public class ServerClient implements Runnable {
    private final Socket clientSocket;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;
    private final int id;
    private InetAddress address = null;
    private Integer clientPort = null;
    private Server server;
    private String nick = "anonymous";

    public ServerClient(Socket clientSocket, int id, Server server) throws IOException {
        this.clientSocket = clientSocket;
        this.inputStream = new ObjectInputStream(this.clientSocket.getInputStream());
        this.outputStream = new ObjectOutputStream(this.clientSocket.getOutputStream());
        this.id = id;
        this.server = server;
    }

    public void handShake() {
        try {
            Message message = (Message) inputStream.readObject();
            nick = message.getNick();
            System.out.println("Register client "+message.getNick() + " with id "+ id);

            Message response = new Message(id, message.getNick(), "Client registered", MessageType.NORMAL);
            outputStream.writeObject(response);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void rejectConnection(String rejectionReason) {
        try {
            Message message = (Message) inputStream.readObject();
            System.out.println("Reject connection for "+message.getNick());

            Message response = new Message(id, message.getNick(), rejectionReason, MessageType.ERROR);
            outputStream.writeObject(response);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Waiting for messages from client "+id);

        try {
            while (true) {
                Message message = (Message) inputStream.readObject();
                if (message.getType() == MessageType.COMMAND) {
                    DatagramPacket datagramPacket = server.readUDPMessage();
                    byte[] bytes = datagramPacket.getData();

                    if (message.getText().equals("U")) {
                        server.sendUDP(bytes, message);

                    } else if (message.getText().equals("M")) {
                        System.out.println("multicast");
                        // server.sendMulticast(bytes, message);

                    } else if (message.getText().equals("R")) {
                        System.out.println("Register UDP");
                        address = datagramPacket.getAddress();
                        clientPort = datagramPacket.getPort();
                    }
                } else {
                    server.sendTCP(message);
                }
            }
        } catch (EOFException e) {
            System.out.println("Client " + id + " closed connection");
            server.deleteClient(this);

            try {
                inputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }  catch (IOException | ClassNotFoundException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) {
        // Cannot send message to yourself
        if (message.getId() == id) {
            return;
        }

        try {
            outputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendUDP(byte[] bytes, int clientId) {
        if (this.id != clientId) {
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length,
                    address, clientPort);

            try {
                server.getDatagramSocket().send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int getId() {
        return id;
    }

    public String getNick() {
        return nick;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getClientPort() {
        return clientPort;
    }

}
