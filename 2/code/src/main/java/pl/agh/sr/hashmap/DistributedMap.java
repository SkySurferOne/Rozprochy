package pl.agh.sr.hashmap;

import com.google.protobuf.InvalidProtocolBufferException;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.*;
import org.jgroups.stack.Protocol;
import org.jgroups.stack.ProtocolStack;
import pl.agh.sr.hashmap.adapters.DistributedMapReceiver;
import pl.agh.sr.protos.HashMapOperationProtos.HashMapOperation;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DistributedMap implements SimpleStringMap {
    private Map<String, String> hashMap = new ConcurrentHashMap<>();
    private JChannel channel;
    private String clusterName;
    private InetAddress address;

    public DistributedMap(String clusterName, String address) throws UnknownHostException {
        this.channel = new JChannel(false);
        this.address = InetAddress.getByName(address);
        this.clusterName = clusterName;

        joinChannel();
    }

    public DistributedMap(String clusterName) {
        this.channel = new JChannel(false);
        this.clusterName = clusterName;

        joinChannel();
    }

    private void joinChannel() {
        System.setProperty("java.net.preferIPv4Stack", "true");

        ProtocolStack protocolStack = new ProtocolStack();
        channel.setProtocolStack(protocolStack);
        channel.setReceiver(new DistributedMapReceiver(hashMap));

        try {
            extendProtocolStack(protocolStack, address);
            channel.connect(clusterName);
            channel.getState(null, 10000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void extendProtocolStack(ProtocolStack protocolStack, InetAddress address) throws Exception {
        Protocol udp = new UDP();
        if (address != null) {
            udp.setValue("mcast_group_addr", address);
        }

        protocolStack.addProtocol(udp)
                .addProtocol(new PING())
                .addProtocol(new MERGE3())
                .addProtocol(new FD_SOCK())
                .addProtocol(new FD_ALL()
                        .setValue("timeout", 12000)
                        .setValue("interval", 3000))
                .addProtocol(new VERIFY_SUSPECT())
                .addProtocol(new BARRIER())
                .addProtocol(new NAKACK2())
                .addProtocol(new UNICAST3())
                .addProtocol(new STABLE())
                .addProtocol(new GMS())
                .addProtocol(new UFC())
                .addProtocol(new MFC())
                .addProtocol(new FRAG2())
                .addProtocol(new SEQUENCER())
                .addProtocol(new STATE_TRANSFER())
                .addProtocol(new FLUSH())
                .init();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        channel.close();
    }

    private void send(byte[] message) {
        Message msg = new Message(null, null, message);

        try {
            channel.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
            channel.close();
        }
    }

    @Override
    public boolean containsKey(String key) {
        return hashMap.containsKey(key);
    }

    @Override
    public String get(String key) {
        return hashMap.get(key);
    }

    @Override
    public String put(String key, String value) {
        HashMapOperation hashMapOperation = HashMapOperation.newBuilder()
                .setType(HashMapOperation.OperationType.PUT)
                .setKey(key)
                .setValue(value)
                .build();
        byte[] message = hashMapOperation.toByteArray();
        send(message);

        String previousValue = hashMap.put(key, value);
        return previousValue;
    }

    @Override
    public String remove(String key) {
        HashMapOperation hashMapOperation = HashMapOperation.newBuilder()
                .setType(HashMapOperation.OperationType.REMOVE)
                .setKey(key)
                .build();
        byte[] message = hashMapOperation.toByteArray();
        send(message);

        String previousValue = hashMap.remove(key);
        return previousValue;
    }

    public Set<Map.Entry<String, String>> entrySet() {
        return hashMap.entrySet();
    }
}
