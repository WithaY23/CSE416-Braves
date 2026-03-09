package edu.stonybrook.cse416.braves.server.repository;

import edu.stonybrook.cse416.braves.server.model.GinglesResultDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface GinglesResultRepository extends MongoRepository<GinglesResultDocument, String> {
    Optional<GinglesResultDocument> findByStateIdAndGroupKeyAndElectionId(String stateId, String groupKey, String electionId);

    List<GinglesResultDocument> findByStateIdAndElectionId(String stateId, String electionId);
}
