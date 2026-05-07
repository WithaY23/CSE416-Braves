package edu.stonybrook.cse416.braves.server.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "ei_support_results")
public class EiSupportResultDocument extends BasePayloadDocument {
    @Field("partyKey")
    private String partyKey;

    public String getPartyKey() { return partyKey; }
    public void setPartyKey(String partyKey) { this.partyKey = partyKey; }
}
