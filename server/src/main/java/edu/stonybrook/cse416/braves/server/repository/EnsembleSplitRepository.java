package edu.stonybrook.cse416.braves.server.repository;

import edu.stonybrook.cse416.braves.server.model.EnsembleSplitDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface EnsembleSplitRepository extends MongoRepository<EnsembleSplitDocument, String> {
    Optional<EnsembleSplitDocument> findByStateIdAndElectionIdAndMetricKey(String stateId, String electionId, String metricKey);
}
