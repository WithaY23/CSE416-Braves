package edu.stonybrook.cse416.braves.server.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "state_summaries")
public class StateSummaryDocument extends BasePayloadDocument {
}
