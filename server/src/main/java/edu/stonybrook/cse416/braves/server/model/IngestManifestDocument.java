package edu.stonybrook.cse416.braves.server.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ingest_manifests")
public class IngestManifestDocument extends BasePayloadDocument {
}
