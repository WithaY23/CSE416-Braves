package edu.stonybrook.cse416.braves.server.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "states")
public class StateDocument extends BasePayloadDocument {
}
