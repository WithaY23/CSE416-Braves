package edu.stonybrook.cse416.braves.server.model.enums;

import java.util.Locale;

public enum PartyKey {
    DEM("DEM"), REP("REP");

    private final String key;

    PartyKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static PartyKey fromString(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("party is required");
        }
        String normalized = input.trim().toUpperCase(Locale.US);
        for (PartyKey pk : values()) {
            if (pk.key.equals(normalized)) {
                return pk;
            }
        }
        throw new IllegalArgumentException("party must be DEM or REP, got: " + input);
    }
}
