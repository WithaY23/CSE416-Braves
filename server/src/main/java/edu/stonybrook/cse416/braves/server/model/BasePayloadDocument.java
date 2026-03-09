package edu.stonybrook.cse416.braves.server.model;

import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.Map;

public abstract class BasePayloadDocument {
    @Id
    private String id;
    private String stateId;
    private String electionId;
    private String groupKey;
    private String ensembleType;
    private String metricKey;
    private String populationMeasure;
    private String schemaVersion;
    private String sourceManifestId;
    private Instant createdAt;
    private Map<String, Object> payload;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getElectionId() {
        return electionId;
    }

    public void setElectionId(String electionId) {
        this.electionId = electionId;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getEnsembleType() {
        return ensembleType;
    }

    public void setEnsembleType(String ensembleType) {
        this.ensembleType = ensembleType;
    }

    public String getMetricKey() {
        return metricKey;
    }

    public void setMetricKey(String metricKey) {
        this.metricKey = metricKey;
    }

    public String getPopulationMeasure() {
        return populationMeasure;
    }

    public void setPopulationMeasure(String populationMeasure) {
        this.populationMeasure = populationMeasure;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public String getSourceManifestId() {
        return sourceManifestId;
    }

    public void setSourceManifestId(String sourceManifestId) {
        this.sourceManifestId = sourceManifestId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
}
