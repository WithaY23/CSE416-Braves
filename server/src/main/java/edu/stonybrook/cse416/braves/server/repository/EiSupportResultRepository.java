package edu.stonybrook.cse416.braves.server.repository;

import edu.stonybrook.cse416.braves.server.model.EiSupportResultDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface EiSupportResultRepository extends MongoRepository<EiSupportResultDocument, String> {
    Optional<EiSupportResultDocument> findByStateIdAndElectionIdAndGroupKey(String stateId, String electionId, String groupKey);

    List<EiSupportResultDocument> findByStateIdAndElectionId(String stateId, String electionId);
}
