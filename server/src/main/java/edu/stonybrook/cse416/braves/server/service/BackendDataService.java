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
        throw new UnsupportedOperationException("Planned for next phase");
    }

    public Map<String, Object> getHeatmap(String stateIdInput, String group) {
        throw new UnsupportedOperationException("Planned for next phase");
    }

    public Map<String, Object> getDistrictTable(String stateIdInput, String election) {
        throw new UnsupportedOperationException("Planned for next phase");
    }

    public Map<String, Object> getGingles(String stateIdInput, String group, String election) {
        throw new UnsupportedOperationException("Planned for next phase");
    }

    public Map<String, Object> getEiSupport(String stateIdInput, String groups, String election, String party) {
        throw new UnsupportedOperationException("Planned for next phase");
    }

    public Map<String, Object> getEnsembleSplits(String stateIdInput, String ensembleSize, String election) {
        throw new UnsupportedOperationException("Planned for next phase");
    }

    public Map<String, Object> getBoxWhisker(String stateIdInput, String group, String ensembleType, String metric) {
        throw new UnsupportedOperationException("Planned for next phase");
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
