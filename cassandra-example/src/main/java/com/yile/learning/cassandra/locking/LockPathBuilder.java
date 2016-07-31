package com.yile.learning.cassandra.locking;

import java.util.UUID;

public class LockPathBuilder {
    private static final String SLASH = "/";

    public static String buildPath(UUID applicationId, String... path) {
        StringBuilder builder = new StringBuilder();
        builder.append(SLASH);
        builder.append(applicationId.toString());

        for (String element : path) {
            builder.append(SLASH);
            builder.append(element);
        }
        return builder.toString();
    }

    public static String buildPath(String binaryValue, String... path) {
        StringBuilder builder = new StringBuilder();
        for (String element : path) {
            builder.append(SLASH);
            builder.append(element);
        }
        builder.append(SLASH);
        builder.append(binaryValue);
        builder.deleteCharAt(0);
        return builder.toString();
    }
}
