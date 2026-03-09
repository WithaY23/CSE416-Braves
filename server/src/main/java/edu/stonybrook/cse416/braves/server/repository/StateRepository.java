package edu.stonybrook.cse416.braves.server.repository;

import edu.stonybrook.cse416.braves.server.model.StateDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StateRepository extends MongoRepository<StateDocument, String> {
    List<StateDocument> findAllByOrderByStateIdAsc();
}
