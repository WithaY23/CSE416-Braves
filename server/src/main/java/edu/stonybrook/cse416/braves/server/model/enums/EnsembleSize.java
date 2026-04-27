package edu.stonybrook.cse416.braves.server.model.enums;

public enum EnsembleSize {
    TEST("test"), FINAL("final");

    private final String key;

    EnsembleSize(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
