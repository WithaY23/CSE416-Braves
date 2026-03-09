package edu.stonybrook.cse416.braves.server.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "gingles_results")
public class GinglesResultDocument extends BasePayloadDocument {
}
