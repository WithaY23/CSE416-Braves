package edu.stonybrook.cse416.braves.server;

import edu.stonybrook.cse416.braves.server.repository.*;
import edu.stonybrook.cse416.braves.server.service.BackendDataService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class BackendDataServiceValidationTest {

    private BackendDataService service() {
        return new BackendDataService(
                mock(StateRepository.class),
                mock(DistrictMapRepository.class),
                mock(StateSummaryRepository.class),
                mock(DistrictTableRepository.class),
                mock(HeatmapBinRepository.class),
                mock(GinglesResultRepository.class),
                mock(EiSupportResultRepository.class),
                mock(EnsembleSplitRepository.class),
                mock(BoxWhiskerResultRepository.class)
        );
    }

    @Test
    void summaryIsSkeletonForNow() {
        assertThrows(UnsupportedOperationException.class, () -> service().getStateSummary("OR"));
    }

    @Test
    void heatmapIsSkeletonForNow() {
        assertThrows(UnsupportedOperationException.class, () -> service().getHeatmap("OR", "black"));
    }
}
