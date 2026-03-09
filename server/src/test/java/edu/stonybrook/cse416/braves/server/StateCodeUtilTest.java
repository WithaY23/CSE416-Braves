package edu.stonybrook.cse416.braves.server;

import edu.stonybrook.cse416.braves.server.util.StateCodeUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StateCodeUtilTest {

    @Test
    void normalizeAliases() {
        assertEquals("OR", StateCodeUtil.normalizeOrThrow("oregon"));
        assertEquals("OR", StateCodeUtil.normalizeOrThrow("OR"));
        assertEquals("SC", StateCodeUtil.normalizeOrThrow("South Carolina"));
        assertEquals("SC", StateCodeUtil.normalizeOrThrow("sc"));
    }

    @Test
    void rejectsUnsupportedState() {
        assertThrows(IllegalArgumentException.class, () -> StateCodeUtil.normalizeOrThrow("TX"));
    }
}
