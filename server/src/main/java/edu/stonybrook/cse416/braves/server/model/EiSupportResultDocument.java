package edu.stonybrook.cse416.braves.server.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ei_support_results")
public class EiSupportResultDocument extends BasePayloadDocument {
}
