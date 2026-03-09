package edu.stonybrook.cse416.braves.server.repository;

import edu.stonybrook.cse416.braves.server.model.IngestManifestDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IngestManifestRepository extends MongoRepository<IngestManifestDocument, String> {
}
