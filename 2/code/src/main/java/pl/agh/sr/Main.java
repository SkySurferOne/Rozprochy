package pl.agh.sr;

import pl.agh.sr.client.WarehouseClient;

import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) {
        String address = null;
        if (args.length >= 1) {
            address = args[0];
        }

        try {
            WarehouseClient warehouseClient;
            if (address == null) {
                warehouseClient = new WarehouseClient();
            } else {
                warehouseClient = new WarehouseClient(address);
            }
            Thread thread = new Thread(warehouseClient);
            thread.start();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
