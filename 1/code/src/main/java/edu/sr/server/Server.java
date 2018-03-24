package edu.sr.server;

import edu.sr.message.MessageType;
import edu.sr.message.Message;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

public class Server implements Runnable {
    private final int portNumber;
    private ServerSocket serverSocket = null;
    private DatagramSocket datagramSocket = null;
    private int clientsNumber = 0;
    private int nextId = 0;
    private Set<ServerClient> registeredClients = new HashSet<>();
    private final int MAX_CLIENTS;
    private ThreadPoolExecutor executor;
    private final int MAX_THREAD_NUM = 4;

    public Server(int portNumber, int maxClientsNumber) throws IOException {
        this.portNumber = portNumber;
        this.MAX_CLIENTS = maxClientsNumber;
        this.serverSocket = new ServerSocket(portNumber);
        this.datagramSocket = new DatagramSocket(portNumber);
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(this.MAX_CLIENTS);
    }

    @Override
    /**
     * Handle incoming clients
     */
    public void run() {
        System.out.println("Server is running on port "+portNumber+" (MAX_CLIENTS="+MAX_CLIENTS+")");
        while (true) {
            Socket clientSocket = null;

            try {
                clientSocket = serverSocket.accept();
                ServerClient serverClient = null;
                if (clientsNumber + 1 > MAX_CLIENTS) {
                    serverClient = new ServerClient(clientSocket, -1, this);
                    serverClient.rejectConnection("To many clients");
                } else {
                    clientsNumber++;
                    int clientId = nextId++;
                    serverClient = new ServerClient(clientSocket, clientId, this);
                    serverClient.handShake();
                    registeredClients.add(serverClient);
                    executor.execute(serverClient);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteClient(ServerClient serverClient) {
        Message message = new Message(serverClient.getId(), serverClient.getNick(), "<quits from chat>", MessageType.NORMAL);
        sendTCP(message);
        registeredClients.remove(serverClient);
        clientsNumber--;
    }

    public synchronized void sendTCP(Message message) {
        // Create specific ForkJoinPool in order not to block other streams
        ForkJoinPool forkJoinPool = new ForkJoinPool(MAX_THREAD_NUM);
        System.out.println("[chat] "+message.toString());
        forkJoinPool.execute(() ->
                registeredClients.parallelStream()
                    .forEach(client ->
                        client.sendMessage(message)
                    ));
    }

    public void sendUDP(byte[] bytes, Message message) throws ExecutionException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool(MAX_THREAD_NUM);

        System.out.println("[chat] "+message.toString());
        System.out.println("[chat:udp]\n" + new String(bytes).replace("\0", "") + "[\\chat:udp]");

        forkJoinPool.execute(() -> registeredClients.parallelStream().forEach(
                client -> client.sendUDP(bytes, message.getId())
        ));
    }

    public DatagramPacket readUDPMessage() {
        int bufferSize = 10240;
        byte[] buffer = new byte[bufferSize];
        DatagramPacket packet = null;

        try {
            packet = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(packet);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return packet;
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    public void disconnect() throws IOException {
        if (serverSocket != null) {
            // close clients and end client threads
            serverSocket.close();
            datagramSocket.close();
            executor.shutdown();
        }
    }

}
