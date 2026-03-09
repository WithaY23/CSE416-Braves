package edu.stonybrook.cse416.braves.server.repository;

import edu.stonybrook.cse416.braves.server.model.DistrictMapDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DistrictMapRepository extends MongoRepository<DistrictMapDocument, String> {
    Optional<DistrictMapDocument> findByStateId(String stateId);
}
