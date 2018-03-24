package pl.agh.sr.client;

import java.util.HashMap;
import java.util.Map;

public enum WarehouseOperation {
    PUT("put"),
    GET("get"),
    LIST("list"),
    REMOVE("remove"),
    ADD("add"),
    SUBTRACT("sub"),
    EXIT("exit");

    private final String name;
    private static Map<String, WarehouseOperation> enumMap = new HashMap<>();

    static {
        enumMap.put("put", WarehouseOperation.PUT);
        enumMap.put("get", WarehouseOperation.GET);
        enumMap.put("list", WarehouseOperation.LIST);
        enumMap.put("remove", WarehouseOperation.REMOVE);
        enumMap.put("add", WarehouseOperation.ADD);
        enumMap.put("sub", WarehouseOperation.SUBTRACT);
        enumMap.put("exit", WarehouseOperation.EXIT);
    }

    private WarehouseOperation(String name) {
        this.name = name;
    }

    public boolean equalsName(String otherName) {
        return equals(otherName.toLowerCase());
    }

    public static WarehouseOperation getEnum(String value) {
        return enumMap.get(value.toLowerCase());
    }

    @Override
    public String toString() {
        return this.name;
    }
}
