package edu.stonybrook.cse416.braves.server.service;

import edu.stonybrook.cse416.braves.server.dto.StateOptionResponse;
import edu.stonybrook.cse416.braves.server.model.*;
import edu.stonybrook.cse416.braves.server.repository.*;
import edu.stonybrook.cse416.braves.server.util.GroupThresholds;
import edu.stonybrook.cse416.braves.server.util.PopulationMeasure;
import edu.stonybrook.cse416.braves.server.util.StateCodeUtil;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BackendDataService {
    private final StateRepository stateRepository;
    private final DistrictMapRepository districtMapRepository;
    private final StateSummaryRepository stateSummaryRepository;
    private final DistrictTableRepository districtTableRepository;
    private final HeatmapBinRepository heatmapBinRepository;
    private final GinglesResultRepository ginglesResultRepository;
    private final EiSupportResultRepository eiSupportResultRepository;
    private final EnsembleSplitRepository ensembleSplitRepository;
    private final BoxWhiskerResultRepository boxWhiskerResultRepository;

    public BackendDataService(
            StateRepository stateRepository,
            DistrictMapRepository districtMapRepository,
            StateSummaryRepository stateSummaryRepository,
            DistrictTableRepository districtTableRepository,
            HeatmapBinRepository heatmapBinRepository,
            GinglesResultRepository ginglesResultRepository,
            EiSupportResultRepository eiSupportResultRepository,
            EnsembleSplitRepository ensembleSplitRepository,
            BoxWhiskerResultRepository boxWhiskerResultRepository
    ) {
        this.stateRepository = stateRepository;
        this.districtMapRepository = districtMapRepository;
        this.stateSummaryRepository = stateSummaryRepository;
        this.districtTableRepository = districtTableRepository;
        this.heatmapBinRepository = heatmapBinRepository;
        this.ginglesResultRepository = ginglesResultRepository;
        this.eiSupportResultRepository = eiSupportResultRepository;
        this.ensembleSplitRepository = ensembleSplitRepository;
        this.boxWhiskerResultRepository = boxWhiskerResultRepository;
    }

    public List<StateOptionResponse> getStates() {
        return stateRepository.findAllByOrderByStateIdAsc().stream()
                .map(doc -> new StateOptionResponse(
                        doc.getStateId(),
                        String.valueOf(doc.getPayload().get("stateName")),
                        Integer.parseInt(String.valueOf(doc.getPayload().get("totalDistricts")))
                ))
                .toList();
    }

    public Map<String, Object> getDistrictMap(String stateIdInput) {
        String stateId = StateCodeUtil.normalizeOrThrow(stateIdInput);
        DistrictMapDocument doc = districtMapRepository.findByStateId(stateId)
                .orElseThrow(() -> new NoSuchElementException("District map not found for stateId=" + stateId));
        return withPopulationMeasure(doc.getPayload(), null);
    }

    public Map<String, Object> getStateSummary(String stateIdInput) {
        String stateId = StateCodeUtil.normalizeOrThrow(stateIdInput);
        StateSummaryDocument doc = stateSummaryRepository.findByStateId(stateId)
                .orElseThrow(() -> new NoSuchElementException("State summary not found for stateId=" + stateId));
        return withPopulationMeasure(doc.getPayload(), PopulationMeasure.TOTAL);
    }

    public Map<String, Object> getHeatmap(String stateIdInput, String group) {
        String stateId = StateCodeUtil.normalizeOrThrow(stateIdInput);
        String normalizedGroup = normalizeGroup(group);
        requireFeasibleGroup(stateId, normalizedGroup);
        HeatmapBinDocument doc = heatmapBinRepository.findByStateIdAndGroupKey(stateId, normalizedGroup)
                .orElseThrow(() -> new NoSuchElementException("Heatmap bins not found for stateId=" + stateId + ", group=" + normalizedGroup));
        return withPopulationMeasure(doc.getPayload(), PopulationMeasure.TOTAL);
    }

    public Map<String, Object> getDistrictTable(String stateIdInput, String election) {
        String stateId = StateCodeUtil.normalizeOrThrow(stateIdInput);
        String electionId = (election == null || election.isBlank()) ? "2024_pres" : election;
        DistrictTableDocument doc = districtTableRepository.findByStateIdAndElectionId(stateId, electionId)
                .orElseThrow(() -> new NoSuchElementException("District table not found for stateId=" + stateId + ", election=" + electionId));
        return withPopulationMeasure(doc.getPayload(), PopulationMeasure.TOTAL);
    }

    public Map<String, Object> getGingles(String stateIdInput, String group, String election) {
        String stateId = StateCodeUtil.normalizeOrThrow(stateIdInput);
        String electionId = (election == null || election.isBlank()) ? "2024_pres" : election;
        GinglesResultDocument doc;
        if (group != null && !group.isBlank()) {
            String normalizedGroup = normalizeGroup(group);
            requireFeasibleGroup(stateId, normalizedGroup);
            doc = ginglesResultRepository.findByStateIdAndGroupKeyAndElectionId(stateId, normalizedGroup, electionId)
                    .orElseThrow(() -> new NoSuchElementException("Gingles data not found for requested group"));
        } else {
            doc = ginglesResultRepository.findByStateIdAndElectionId(stateId, electionId).stream().findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Gingles data not found for stateId=" + stateId));
        }
        PopulationMeasure measure = PopulationMeasure.valueOf(
                Optional.ofNullable(doc.getPopulationMeasure()).orElse("TOTAL")
        );
        return withPopulationMeasure(doc.getPayload(), measure);
    }

    public Map<String, Object> getEiSupport(String stateIdInput, String groups, String election, String party) {
        String stateId = StateCodeUtil.normalizeOrThrow(stateIdInput);
        String electionId = (election == null || election.isBlank()) ? "2024_pres" : election;
        if (groups == null || groups.isBlank()) {
            throw new IllegalArgumentException("groups query parameter is required");
        }
        if (party == null || party.isBlank()) {
            throw new IllegalArgumentException("party query parameter is required");
        }
        String partyUpper = party.trim().toUpperCase(Locale.US);
        if (!partyUpper.equals("DEM") && !partyUpper.equals("REP")) {
            throw new IllegalArgumentException("party must be DEM or REP");
        }

        String primaryGroup = normalizeGroup(groups.split(",")[0]);
        requireFeasibleGroup(stateId, primaryGroup);

        EiSupportResultDocument doc = eiSupportResultRepository.findByStateIdAndElectionIdAndGroupKey(stateId, electionId, primaryGroup)
                .orElseGet(() -> eiSupportResultRepository.findByStateIdAndElectionId(stateId, electionId).stream().findFirst()
                        .orElseThrow(() -> new NoSuchElementException("EI support data not found")));

        PopulationMeasure measure = PopulationMeasure.valueOf(
                Optional.ofNullable(doc.getPopulationMeasure()).orElse("CVAP")
        );
        Map<String, Object> payload = withPopulationMeasure(doc.getPayload(), measure);
        payload.put("selectedParty", partyUpper);
        return payload;
    }

    public Map<String, Object> getEnsembleSplits(String stateIdInput, String ensembleSize, String election) {
        String stateId = StateCodeUtil.normalizeOrThrow(stateIdInput);
        String electionId = (election == null || election.isBlank()) ? "2024_pres" : election;
        String metricKey = (ensembleSize == null || ensembleSize.isBlank()) ? "final" : ensembleSize.trim().toLowerCase(Locale.US);
        if (!metricKey.equals("test") && !metricKey.equals("final")) {
            throw new IllegalArgumentException("ensembleSize must be test or final");
        }

        EnsembleSplitDocument doc = ensembleSplitRepository.findByStateIdAndElectionIdAndMetricKey(stateId, electionId, metricKey)
                .orElseGet(() -> ensembleSplitRepository.findByStateIdAndElectionIdAndMetricKey(stateId, electionId, "final")
                        .orElseThrow(() -> new NoSuchElementException("Ensemble splits not found")));
        return withPopulationMeasure(doc.getPayload(), PopulationMeasure.TOTAL);
    }

    public Map<String, Object> getBoxWhisker(String stateIdInput, String group, String ensembleType, String metric) {
        String stateId = StateCodeUtil.normalizeOrThrow(stateIdInput);
        if (ensembleType == null || ensembleType.isBlank()) {
            throw new IllegalArgumentException("ensembleType is required");
        }
        String normalizedEnsembleType = ensembleType.trim().toLowerCase(Locale.US);
        if (!normalizedEnsembleType.equals("vra_constrained") && !normalizedEnsembleType.equals("race_blind")) {
            throw new IllegalArgumentException("ensembleType must be vra_constrained or race_blind");
        }

        if (metric == null || metric.isBlank()) {
            throw new IllegalArgumentException("metric is required");
        }
        String metricKey = metric.trim().toLowerCase(Locale.US);
        if (!metricKey.equals("minority_share")) {
            throw new IllegalArgumentException("metric must be minority_share");
        }

        String normalizedGroup = normalizeGroup(group);
        requireFeasibleGroup(stateId, normalizedGroup);

        BoxWhiskerResultDocument doc = boxWhiskerResultRepository
                .findByStateIdAndGroupKeyAndEnsembleTypeAndMetricKey(stateId, normalizedGroup, normalizedEnsembleType, metricKey)
                .orElseGet(() -> boxWhiskerResultRepository
                        .findByStateIdAndEnsembleTypeAndMetricKey(stateId, normalizedEnsembleType, metricKey)
                        .stream().findFirst()
                        .orElseThrow(() -> new NoSuchElementException("Box whisker data not found")));

        PopulationMeasure measure = PopulationMeasure.valueOf(
                Optional.ofNullable(doc.getPopulationMeasure()).orElse("CVAP")
        );
        return withPopulationMeasure(doc.getPayload(), measure);
    }

    private String normalizeGroup(String group) {
        if (group == null || group.isBlank()) {
            throw new IllegalArgumentException("group is required");
        }
        return group.trim().toLowerCase(Locale.US);
    }

    private void requireFeasibleGroup(String stateId, String group) {
        if (!GroupThresholds.isFeasible(stateId, group)) {
            throw new IllegalArgumentException("Group does not meet threshold (>=200000) or is unsupported for state");
        }
    }

    private Map<String, Object> withPopulationMeasure(Map<String, Object> payload, PopulationMeasure measure) {
        Map<String, Object> copy = new LinkedHashMap<>(payload);
        if (measure != null) {
            copy.put("populationMeasureUsed", measure.name());
        }
        if (!copy.containsKey("schemaVersion")) {
            copy.put("schemaVersion", "v1");
        }
        return copy;
    }
}
