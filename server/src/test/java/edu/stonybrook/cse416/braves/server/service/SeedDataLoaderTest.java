package edu.stonybrook.cse416.braves.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stonybrook.cse416.braves.server.model.InterestingPlanDocument;
import edu.stonybrook.cse416.braves.server.model.StateSummaryDocument;
import edu.stonybrook.cse416.braves.server.repository.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SeedDataLoaderTest {

    @Test
    void seedStateSummariesPersistsBackendDrivenOrAndScSummaryFields() {
        StateSummaryRepository stateSummaryRepository = mock(StateSummaryRepository.class);

        SeedDataLoader loader = new SeedDataLoader(
                new ObjectMapper(),
                mock(GeometryAssetService.class),
                mock(StateRepository.class),
                stateSummaryRepository,
                mock(EnsembleSummaryRepository.class),
                mock(DistrictTableRepository.class),
                mock(HeatmapBinRepository.class),
                mock(GinglesResultRepository.class),
                mock(GinglesTableRepository.class),
                mock(EiSupportResultRepository.class),
                mock(EiPrecinctBarCiRepository.class),
                mock(EiKdeRepository.class),
                mock(EnsembleSplitRepository.class),
                mock(BoxWhiskerResultRepository.class),
                mock(InterestingPlanRepository.class),
                mock(VraImpactThresholdTableRepository.class),
                mock(MinorityEffectivenessBoxWhiskerRepository.class),
                mock(MinorityEffectivenessHistogramRepository.class),
                mock(RunManifestRepository.class),
                mock(IngestManifestRepository.class),
                mock(CacheManager.class)
        );

        Path tempRoot;
        try {
            tempRoot = Files.createTempDirectory("seed-summary-test");
            Path outputDir = tempRoot.resolve("preprocessing/output");
            Files.createDirectories(outputDir);
            Files.writeString(outputDir.resolve("OR_state_summary.json"), """
                    {"population":"3,370,625","WhitePopulation":"2,526,251","BlackPopulation":"60,012","AsianPopulation":"194,538","HispanicPopulation":"389,384","voterDistributionDem":"1,240,600 (55.27%)","voterDistributionRep":"919,480 (40.97%)","partyControl":"Democratic","democratReps":"Suzanne Bonamici, Maxine Dexter, Val Hoyle, Janelle Bynum, Andrea Salinas","republicanReps":"Cliff Bentz"}
                    """);
            Files.writeString(outputDir.resolve("SC_state_summary.json"), """
                    {"population":"4,014,460","WhitePopulation":"2,603,975","BlackPopulation":"964,667","AsianPopulation":"90,466","HispanicPopulation":"231,124","voterDistributionDem":"1,028,452 (40.36%)","voterDistributionRep":"1,483,747 (58.23%)","partyControl":"Republican","democratReps":"James Clyburn","republicanReps":"Nancy Mace, Joe Wilson, Sheri Biggs, William Timmons, Ralph Norman, Russell Fry"}
                    """);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }

        ReflectionTestUtils.invokeMethod(loader, "seedStateSummaries", tempRoot);

        verify(stateSummaryRepository).deleteAll();

        ArgumentCaptor<StateSummaryDocument> captor = ArgumentCaptor.forClass(StateSummaryDocument.class);
        verify(stateSummaryRepository, times(2)).save(captor.capture());

        List<StateSummaryDocument> savedDocs = captor.getAllValues();
        StateSummaryDocument orDoc = savedDocs.stream()
                .filter(doc -> "OR".equals(doc.getStateId()))
                .findFirst()
                .orElseThrow();
        StateSummaryDocument scDoc = savedDocs.stream()
                .filter(doc -> "SC".equals(doc.getStateId()))
                .findFirst()
                .orElseThrow();

        assertSummaryFields(orDoc.getPayload(), Map.of(
                "population", "3,370,625",
                "WhitePopulation", "2,526,251",
                "BlackPopulation", "60,012",
                "AsianPopulation", "194,538",
                "HispanicPopulation", "389,384",
                "voterDistributionDem", "1,240,600 (55.27%)",
                "voterDistributionRep", "919,480 (40.97%)",
                "partyControl", "Democratic",
                "democratReps", "Suzanne Bonamici, Maxine Dexter, Val Hoyle, Janelle Bynum, Andrea Salinas",
                "republicanReps", "Cliff Bentz"
        ));

        assertSummaryFields(scDoc.getPayload(), Map.of(
                "population", "4,014,460",
                "WhitePopulation", "2,603,975",
                "BlackPopulation", "964,667",
                "AsianPopulation", "90,466",
                "HispanicPopulation", "231,124",
                "voterDistributionDem", "1,028,452 (40.36%)",
                "voterDistributionRep", "1,483,747 (58.23%)",
                "partyControl", "Republican",
                "democratReps", "James Clyburn",
                "republicanReps", "Nancy Mace, Joe Wilson, Sheri Biggs, William Timmons, Ralph Norman, Russell Fry"
        ));
    }

    private void assertSummaryFields(Map<String, Object> payload, Map<String, String> expectedFields) {
        expectedFields.forEach((key, value) -> assertEquals(value, payload.get(key), "Unexpected value for " + key));
    }

    @Test
    void seedInterestingPlansPersistsOnlyVraAndSeedsEffectiveDistrictIds(@TempDir Path tempDir) throws IOException {
        InterestingPlanRepository interestingPlanRepository = mock(InterestingPlanRepository.class);
        CacheManager cacheManager = mock(CacheManager.class);
        Cache interestingListCache = mock(Cache.class);
        Cache interestingPlanCache = mock(Cache.class);
        when(cacheManager.getCache("interestingPlanList")).thenReturn(interestingListCache);
        when(cacheManager.getCache("interestingPlan")).thenReturn(interestingPlanCache);

        SeedDataLoader loader = new SeedDataLoader(
                new ObjectMapper(),
                mock(GeometryAssetService.class),
                mock(StateRepository.class),
                mock(StateSummaryRepository.class),
                mock(EnsembleSummaryRepository.class),
                mock(DistrictTableRepository.class),
                mock(HeatmapBinRepository.class),
                mock(GinglesResultRepository.class),
                mock(GinglesTableRepository.class),
                mock(EiSupportResultRepository.class),
                mock(EiPrecinctBarCiRepository.class),
                mock(EiKdeRepository.class),
                mock(EnsembleSplitRepository.class),
                mock(BoxWhiskerResultRepository.class),
                interestingPlanRepository,
                mock(VraImpactThresholdTableRepository.class),
                mock(MinorityEffectivenessBoxWhiskerRepository.class),
                mock(MinorityEffectivenessHistogramRepository.class),
                mock(RunManifestRepository.class),
                mock(IngestManifestRepository.class),
                cacheManager
        );

        Path aydenRoot = tempDir.resolve("preprocessing/output/Ayden");
        Path orDir = aydenRoot.resolve("OR_Interesting_Plans");
        Path scDir = aydenRoot.resolve("SC_Interesting_Plans");
        Files.createDirectories(orDir);
        Files.createDirectories(scDir);

        String topology = """
                {"type":"Topology","objects":{"data":{"type":"GeometryCollection","geometries":[{"type":"Polygon","properties":{"district_id":1},"arcs":[]}]}}, "arcs":[]}
                """;
        Files.writeString(orDir.resolve("vra_cracking_districts_min_hispanic.topojson"), topology);
        Files.writeString(orDir.resolve("vra_custom_no_mapping.topojson"), topology);
        Files.writeString(orDir.resolve("race_blind_cracking_districts_min_hispanic.topojson"), topology);
        Files.writeString(scDir.resolve("vra_cracking_districts_min_black.topojson"), topology);
        Files.writeString(scDir.resolve("race_blind_cracking_districts_min_black.topojson"), topology);

        Files.writeString(aydenRoot.resolve("effective_districts.json"), """
                {
                  "OR": {
                    "vra_cracking_districts_min_hispanic": [4]
                  },
                  "SC": {
                    "vra_cracking_districts_min_black": [3, 4]
                  }
                }
                """);

        ReflectionTestUtils.invokeMethod(loader, "seedInterestingPlans", tempDir);

        verify(interestingPlanRepository).deleteAll();
        ArgumentCaptor<InterestingPlanDocument> captor = ArgumentCaptor.forClass(InterestingPlanDocument.class);
        verify(interestingPlanRepository, times(3)).save(captor.capture());
        verify(interestingPlanRepository, never()).save(org.mockito.ArgumentMatchers.argThat(
                doc -> String.valueOf(doc.getPayload().get("planId")).startsWith("race_blind_")
        ));

        List<InterestingPlanDocument> docs = captor.getAllValues();
        InterestingPlanDocument orMapped = docs.stream()
                .filter(doc -> "vra_cracking_districts_min_hispanic".equals(doc.getPlanId()))
                .findFirst()
                .orElseThrow();
        InterestingPlanDocument orUnmapped = docs.stream()
                .filter(doc -> "vra_custom_no_mapping".equals(doc.getPlanId()))
                .findFirst()
                .orElseThrow();

        assertEquals(List.of(4), orMapped.getPayload().get("effectiveDistrictIds"));
        assertEquals(List.of(), orUnmapped.getPayload().get("effectiveDistrictIds"));
        assertEquals("Cracking districts min hispanic", orMapped.getPayload().get("planName"));
        assertTrue(String.valueOf(orMapped.getPayload().get("ensembleType")).contains("vra"));
    }
}
