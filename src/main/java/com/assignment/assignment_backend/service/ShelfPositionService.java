package com.assignment.assignment_backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Values;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.springframework.beans.factory.annotation.Value;        
import org.springframework.stereotype.Service;               

import com.assignment.assignment_backend.entity.Shelf;
import com.assignment.assignment_backend.view.PositionView;

@Service
public class ShelfPositionService {
    private final Driver driver;
    private final String database;                         

    public ShelfPositionService(
        Driver driver,
        @Value("${neo4j.database:neo4j}") String database   
    ) {
        this.driver = driver;
        this.database = database;
    }

    public PositionView createShelfPosition(String deviceId, int index) {
    	if (deviceId == null)
            throw new NullPointerException("deviceId cannot be null");

        if (index <= 0)
            throw new IllegalArgumentException("index must be positive");

        String spId = "SP-" + UUID.randomUUID();
        try (Session session = driver.session(SessionConfig.forDatabase(database))) {
            return session.executeWrite(tx -> {

                tx.run("""
                    MATCH (d:Device {deviceId: $deviceId})
                    WHERE d.isDeleted = false
                    RETURN d
                    """, Values.parameters("deviceId", deviceId)).single();

                tx.run("""
                    MATCH (d:Device {deviceId: $deviceId})
                    MERGE (sp:ShelfPosition {shelfPositionId: $spId})
                    ON CREATE SET
                      sp.deviceId = d.deviceId,
                      sp.index = $idx,
                      sp.isDeleted = false,                  
                      sp.createdAt = datetime(),
                      sp.updatedAt = datetime()
                    ON MATCH SET
                      sp.index = $idx,
                      sp.updatedAt = datetime()
                    MERGE (d)-[:HAS]->(sp)
                    """, Values.parameters(
                        "deviceId", deviceId,
                        "spId", spId,
                        "idx", index
                    ));

                return new PositionView(spId, index, null);
            });
        }
    }

    public PositionView getShelfPosition(String shelfPositionId) {
        try (Session session = driver.session(SessionConfig.forDatabase(database))) {
            return session.executeRead(tx -> {
                Record rec = tx.run("""
                    MATCH (sp:ShelfPosition {shelfPositionId: $spId})
                    WHERE sp.isDeleted = false
                    OPTIONAL MATCH (sp)-[:HAS]->(s:Shelf)
                    WHERE s.isDeleted = false
                    RETURN sp, s
                    """, Values.parameters("spId", shelfPositionId)).single();

                Node sp = rec.get("sp").asNode();
                String spId = sp.get("shelfPositionId").asString();
                int index = sp.get("index").asInt();

                Shelf shelf = null;
                if (!rec.get("s").isNull()) {
                    Node sn = rec.get("s").asNode();
                    shelf = new Shelf(
                        sn.get("shelfId").asString(),
                        sn.get("shelfName").asString(),
                        sn.get("partNumber").asString(),
                        sn.get("isDeleted").asBoolean()
                    );
                }
                return new PositionView(spId, index, shelf);
            });
        }
    }

    public List<PositionView> listPositionsByDevice(String deviceId) {
        try (Session session = driver.session(SessionConfig.forDatabase(database))) {
            return session.executeRead(tx -> {
                Result res = tx.run("""
                    MATCH (d:Device {deviceId: $deviceId})
                    WHERE d.isDeleted = false
                    MATCH (d)-[:HAS]->(sp:ShelfPosition)
                    WHERE sp.isDeleted = false
                    OPTIONAL MATCH (sp)-[:HAS]->(s:Shelf { isDeleted: false })
                    RETURN sp, s
                    ORDER BY sp.index
                    """, Values.parameters("deviceId", deviceId));

                List<PositionView> list = new ArrayList<>();
                while (res.hasNext()) {
                    Record r = res.next();
                    Node sp = r.get("sp").asNode();
                    String spId = sp.get("shelfPositionId").asString();
                    int index = sp.get("index").asInt();

                    Shelf shelf = null;
                    if (!r.get("s").isNull()) {
                        Node sn = r.get("s").asNode();
                        shelf = new Shelf(
                            sn.get("shelfId").asString(),
                            sn.get("shelfName").asString(),
                            sn.get("partNumber").asString(),
                            sn.get("isDeleted").asBoolean()
                        );
                    }
                    list.add(new PositionView(spId, index, shelf));
                }
                return list;
            });
        }
    }

    public void softDeleteShelfPosition(String shelfPositionId) {
        try (Session session = driver.session(SessionConfig.forDatabase(database))) {
            session.executeWrite(tx -> {
                tx.run("""
                    MATCH (sp:ShelfPosition {shelfPositionId: $spId})
                    WHERE sp.isDeleted = false
                    SET sp.isDeleted = true,
                        sp.deletedAt = datetime(),
                        sp.updatedAt = datetime()
                    """, Values.parameters("spId", shelfPositionId));

                tx.run("""
                    MATCH (sp:ShelfPosition {shelfPositionId: $spId})-[r:HAS]->(s:Shelf)
                    WHERE s.isDeleted = false
                    DELETE r
                    """, Values.parameters("spId", shelfPositionId));

                return null;
            });
        }
    }
}