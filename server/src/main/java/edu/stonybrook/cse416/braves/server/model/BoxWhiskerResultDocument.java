package edu.stonybrook.cse416.braves.server.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "box_whisker_results")
public class BoxWhiskerResultDocument extends BasePayloadDocument {
}
