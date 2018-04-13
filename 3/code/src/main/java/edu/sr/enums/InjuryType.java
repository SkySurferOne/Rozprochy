package edu.sr.enums;

import java.util.HashMap;

public enum InjuryType {
    HIP("hip"),
    KNEE("knee"),
    ELBOW("elbow");

    private final String typeName;
    private static HashMap<String, InjuryType> hashMap = new HashMap<>();

    static {
        hashMap.put("hip", HIP);
        hashMap.put("knee", KNEE);
        hashMap.put("elbow", ELBOW);
    }

    InjuryType(String typeName) {
        this.typeName = typeName;
    }

    public static InjuryType getEnumByTypeName(String typeName) {
        return hashMap.get(typeName.toLowerCase());
    }

    public static boolean isTypeNameValid(String typeName) {
        return getEnumByTypeName(typeName) != null;
    }

    @Override
    public String toString() {
        return this.typeName;
    }
}
