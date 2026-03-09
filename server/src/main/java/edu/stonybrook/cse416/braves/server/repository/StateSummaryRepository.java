package edu.stonybrook.cse416.braves.server.repository;

import edu.stonybrook.cse416.braves.server.model.StateSummaryDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface StateSummaryRepository extends MongoRepository<StateSummaryDocument, String> {
    Optional<StateSummaryDocument> findByStateId(String stateId);
}
