package edu.stonybrook.cse416.braves.server.repository;

import edu.stonybrook.cse416.braves.server.model.InterestingPlanDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface InterestingPlanRepository extends MongoRepository<InterestingPlanDocument, String> {
    List<InterestingPlanDocument> findByStateId(String stateId);
    Optional<InterestingPlanDocument> findByStateIdAndPlanId(String stateId, String planId);
}
