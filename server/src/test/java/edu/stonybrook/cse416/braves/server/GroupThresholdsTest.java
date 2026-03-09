package edu.stonybrook.cse416.braves.server;

import edu.stonybrook.cse416.braves.server.util.GroupThresholds;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GroupThresholdsTest {

    @Test
    void thresholdAppliedByState() {
        assertTrue(GroupThresholds.isFeasible("OR", "latino"));
        assertTrue(GroupThresholds.isFeasible("SC", "black"));
        assertFalse(GroupThresholds.isFeasible("OR", "black"));
        assertFalse(GroupThresholds.isFeasible("SC", "asian"));
    }
}
