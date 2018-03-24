package pl.agh.sr.hashmap.adapters;

import com.google.protobuf.InvalidProtocolBufferException;
import org.jgroups.*;
import pl.agh.sr.protos.HashMapOperationProtos.HashMapState;
import pl.agh.sr.protos.HashMapOperationProtos.HashMapOperation;

import javax.swing.text.html.parser.Entity;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class DistributedMapReceiver extends ReceiverAdapter {
    private Map<String, String> hashMap;
    private final JChannel channel;

    public DistributedMapReceiver(JChannel channel, Map<String, String> hashMap) {
        this.hashMap = hashMap;
        this.channel = channel;
    }

    @Override
    public void receive(Message msg) {
        try {
            HashMapOperation message = HashMapOperation.parseFrom(msg.getBuffer());

            HashMapOperation.OperationType type = message.getType();
            String key = message.getKey();
            String value = message.getValue();
            performAction(type, key, value);

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    public void performAction(HashMapOperation.OperationType type, String key, String value) {
        switch (type) {
            case PUT:
                hashMap.put(key, value);
                break;
            case REMOVE:
                hashMap.remove(key);
                break;
        }
    }

    @Override
    public void getState(OutputStream output) throws Exception {
        synchronized (hashMap) {
            HashMapState.Builder stateBuilder = HashMapState.newBuilder();

            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                stateBuilder.addEntriesBuilder()
                        .setKey(entry.getKey())
                        .setValue(entry.getValue());
            }

            HashMapState state = stateBuilder.build();
            state.writeTo(output);
        }
    }

    @Override
    public void setState(InputStream input) throws Exception {
        synchronized (hashMap) {
            HashMapState state = HashMapState.parseFrom(input);
            hashMap.clear();

            for (HashMapState.Entry entry : state.getEntriesList()) {
                String key = entry.getKey();
                String value = entry.getValue();

                hashMap.put(key, value);
            }
        }
    }

    @Override
    public void viewAccepted(View newView) {
        handleView(channel, newView);
    }

    private void handleView(JChannel channel, View newView) {
        if(newView instanceof MergeView) {
            ViewHandler handler = new ViewHandler(channel, (MergeView) newView);
            handler.start();
        }
    }
}
