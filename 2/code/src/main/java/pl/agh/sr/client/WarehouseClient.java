package pl.agh.sr.client;

import org.jgroups.JChannel;
import pl.agh.sr.hashmap.DistributedMap;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

public class WarehouseClient implements Runnable {
    private String address;
    private final String clusterName = "warehouse";
    private DistributedMap distributedMap;
    private boolean isScanning = true;

    public WarehouseClient(String address) throws UnknownHostException {
        this.address = address;
        this.distributedMap = new DistributedMap(clusterName, address);
    }

    public WarehouseClient() {
       this.distributedMap = new DistributedMap(clusterName);
    }

    @Override
    public void run() {
        System.out.println("Welcome in the warehouse client.");

        try (Scanner input = new Scanner(System.in)) {
            while (isScanning) {
                String text = input.nextLine();
                String[] command = Arrays.stream(text.trim().split("\\s+"))
                        .filter(str -> !str.equals(""))
                        .toArray(size -> new String[size]);
                try {
                    evaluateCommand(command);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void evaluateCommand(String[] command) {
        if (indexExists(command, 0)) {
            WarehouseOperation warehouseOperation = WarehouseOperation.getEnum(command[0]);
            String message = "";

            if (warehouseOperation == null) {
                message = "Undefined command";
            } else {
                switch (warehouseOperation) {
                    case GET:
                        if (!indexExists(command, 1)) {
                            message = "First parameter is missing";
                        } else {
                            String value = distributedMap.get(command[1]);
                            message = value != null ? value : "This product does not exist";
                        }
                        break;
                    case PUT:
                        if (!indexExists(command, 1)) {
                            message = "First parameter is missing";
                        } else if (!indexExists(command, 2)) {
                            message = "Second parameter is missing";
                        } else {
                            if (!isInteger(command[2])) {
                                message = "second parameter should be integer";
                            } else {
                                String prev = distributedMap.put(command[1], command[2]);
                                message = command[1] + " -> " + command[2];
                                message += prev == null ? " - has been added" : " - updated";
                            }
                        }
                        break;
                    case LIST:
                        System.out.println("Products listing with amount");
                        int i = 1;
                        for (Map.Entry<String, String> entry : distributedMap.entrySet()) {
                            System.out.println((i++) + ": " + entry.getKey() + " - " + entry.getValue());
                        }
                        break;
                    case REMOVE:
                        if (!indexExists(command, 1)) {
                            message = "First parameter is missing";
                        } else {
                            String prevKey = distributedMap.remove(command[1]);
                            message = prevKey != null ? command[1] + " has been deleted" : "This product does not exist";
                        }
                        break;
                    case ADD:
                        if (!indexExists(command, 1)) {
                            message = "First parameter is missing";
                        } else if (!indexExists(command, 2)) {
                            message = "Second parameter is missing";
                        } else {
                            String key = command[1];
                            if (!isInteger(command[2])) {
                                message = "Second parameter should be integer";
                                break;
                            }
                            int addValue = Integer.valueOf(command[2]);
                            boolean exists = distributedMap.containsKey(key);

                            if (!exists) {
                                message = command[1]+" does not exits.";
                            } else {
                                int value = Integer.valueOf(distributedMap.get(key));
                                int evaluated = value + addValue;
                                distributedMap.put(key, String.valueOf(value + addValue));
                                message = key + " -> "+evaluated;
                            }
                        }
                        break;
                    case SUBTRACT:
                        if (!indexExists(command, 1)) {
                            message = "First parameter is missing";
                        } else if (!indexExists(command, 2)) {
                            message = "Second parameter is missing";
                        } else {
                            String key = command[1];
                            if (!isInteger(command[2])) {
                                message = "second parameter should be integer";
                                break;
                            }
                            int subValue = Integer.valueOf(command[2]);
                            boolean exists = distributedMap.containsKey(key);

                            if (!exists) {
                                message = command[1]+" does not exits";
                            } else {
                                int value = Integer.valueOf(distributedMap.get(key));
                                if (value - subValue < 0) {
                                    message = "There is no enough of the product";
                                    break;
                                }
                                int evaluated = value - subValue;
                                distributedMap.put(key, String.valueOf(evaluated));
                                message = key + " -> "+evaluated;
                            }
                        }
                        break;
                    case EXIT:
                        exit();
                        break;
                    default:
                        message = "Undefined command";
                }
            }
            System.out.println(message);
        }
    }

    private void exit() {
        isScanning = false;
        distributedMap = null;
        System.runFinalization();
        System.exit(0);
    }

    private boolean indexExists(final String[] array, final int index) {
        return index >= 0 && index < array.length;
    }

    private boolean isInteger(String s) {
        if (s == null || s.equals("") || s.charAt(0) == '0') {
            return false;
        } else {
            boolean isInteger = true;

            for (int i = 0; i < s.length() && isInteger; i++) {
                char c = s.charAt(i);
                isInteger = isInteger & ((c >= '0' && c <= '9'));
            }
            return isInteger;
        }
    }
}
