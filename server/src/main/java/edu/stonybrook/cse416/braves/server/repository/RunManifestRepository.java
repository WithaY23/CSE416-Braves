package edu.stonybrook.cse416.braves.server.repository;

import edu.stonybrook.cse416.braves.server.model.RunManifestDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RunManifestRepository extends MongoRepository<RunManifestDocument, String> {
}
