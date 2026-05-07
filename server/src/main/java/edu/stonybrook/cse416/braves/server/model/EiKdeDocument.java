package edu.stonybrook.cse416.braves.server.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "ei_kde_results")
public class EiKdeDocument extends BasePayloadDocument {
    @Field("partyKey")
    private String partyKey;

    public String getPartyKey() { return partyKey; }
    public void setPartyKey(String partyKey) { this.partyKey = partyKey; }
}
