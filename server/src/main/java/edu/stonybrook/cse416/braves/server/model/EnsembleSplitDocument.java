package edu.stonybrook.cse416.braves.server.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ensemble_splits")
public class EnsembleSplitDocument extends BasePayloadDocument {
}
