package edu.stonybrook.cse416.braves.server.repository;

import edu.stonybrook.cse416.braves.server.model.DistrictTableDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DistrictTableRepository extends MongoRepository<DistrictTableDocument, String> {
    Optional<DistrictTableDocument> findByStateIdAndElectionId(String stateId, String electionId);
}
