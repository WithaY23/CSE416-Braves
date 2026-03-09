package edu.stonybrook.cse416.braves.server.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "heatmap_bins")
public class HeatmapBinDocument extends BasePayloadDocument {
}
