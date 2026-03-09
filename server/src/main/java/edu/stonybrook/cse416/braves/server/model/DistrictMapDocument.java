package edu.stonybrook.cse416.braves.server.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "district_maps")
public class DistrictMapDocument extends BasePayloadDocument {
}
