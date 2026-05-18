package edu.stonybrook.cse416.braves.server.repository;

import edu.stonybrook.cse416.braves.server.model.BoxWhiskerResultDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BoxWhiskerResultRepository extends MongoRepository<BoxWhiskerResultDocument, String> {
    Optional<BoxWhiskerResultDocument> findByStateIdAndGroupKeyAndEnsembleTypeAndMetricKeyAndEnsembleIndex(
            String stateId, String groupKey, String ensembleType, String metricKey, Integer ensembleIndex);

    List<BoxWhiskerResultDocument> findByStateIdAndEnsembleTypeAndMetricKey(String stateId, String ensembleType, String metricKey);
}
