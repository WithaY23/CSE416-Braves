package edu.stonybrook.cse416.braves.server.service;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.TreeSet;

@Service
public class MongoDatabaseHealthService implements DatabaseHealthService {
    private final MongoTemplate mongoTemplate;

    public MongoDatabaseHealthService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Map<String, Object> getHealth() {
        Document pingResult = mongoTemplate.executeCommand(new Document("ping", 1));

        return Map.of(
                "status", "ok",
                "service", "braves-server",
                "database", mongoTemplate.getDb().getName(),
                "mongoStatus", pingResult.getDouble("ok") == 1.0 ? "ok" : "degraded",
                "collections", Map.of(),
                "availableCollections", new TreeSet<>(mongoTemplate.getCollectionNames())
        );
    }
}
