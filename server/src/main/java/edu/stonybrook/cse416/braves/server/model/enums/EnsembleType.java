package edu.stonybrook.cse416.braves.server.model.enums;

public enum EnsembleType {
    RACE_BLIND("race_blind"), VRA_CONSTRAINED("vra_constrained");

    private final String key;

    EnsembleType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
