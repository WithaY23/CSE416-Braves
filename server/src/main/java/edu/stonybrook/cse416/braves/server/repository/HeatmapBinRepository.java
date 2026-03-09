package edu.stonybrook.cse416.braves.server.repository;

import edu.stonybrook.cse416.braves.server.model.HeatmapBinDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface HeatmapBinRepository extends MongoRepository<HeatmapBinDocument, String> {
    Optional<HeatmapBinDocument> findByStateIdAndGroupKey(String stateId, String groupKey);
}
