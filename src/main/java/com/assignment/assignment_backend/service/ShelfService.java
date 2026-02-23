package com.assignment.assignment_backend.service;

import java.util.UUID;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Values;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.Record;
import org.springframework.beans.factory.annotation.Value;        
import org.springframework.stereotype.Service;

import com.assignment.assignment_backend.entity.Shelf;
import com.assignment.assignment_backend.requests.CreateShelfRequest;
import com.assignment.assignment_backend.requests.UpdateShelfRequest;

@Service
public class ShelfService {
    private final Driver driver;
    private final String database;                              

    public ShelfService(Driver driver,@Value("${neo4j.database:neo4j}") String database) {
        this.driver = driver;
        this.database = database;
    }

    public Shelf createShelf(CreateShelfRequest req) {
        String shelfId = "SHELF-" + UUID.randomUUID();
        try (Session session = driver.session(SessionConfig.forDatabase(database))) {
            return session.executeWrite(tx -> {
                Record rec = tx.run("""
                        MERGE (s:Shelf {shelfId: $id})
                        ON CREATE SET 
                          s.shelfName = $name,
                          s.partNumber = $pn,
                          s.isDeleted = false,
                          s.createdAt = datetime(),
                          s.updatedAt = datetime()
                        ON MATCH SET
                          s.shelfName = $name,
                          s.partNumber = $pn,
                          s.updatedAt = datetime()
                        RETURN s
                        """, Values.parameters(
                            "id", shelfId,
                            "name", req.getShelfName(),
                            "pn", req.getPartNumber()
                        )).single();
                Node s = rec.get("s").asNode();
                return new Shelf(
                    s.get("shelfId").asString(),
                    s.get("shelfName").asString(),
                    s.get("partNumber").asString(),
                    s.get("isDeleted").asBoolean()
                );
            });
        }
    }

    public Shelf getShelf(String shelfId) {
        try (Session session = driver.session(SessionConfig.forDatabase(database))) {
            return session.executeRead(tx -> {
                Record rec = tx.run("""
                        MATCH (s:Shelf {shelfId: $id})
                        WHERE s.isDeleted = false
                        RETURN s
                        """, Values.parameters("id", shelfId)).single();
                Node s = rec.get("s").asNode();
                return new Shelf(
                    s.get("shelfId").asString(),
                    s.get("shelfName").asString(),
                    s.get("partNumber").asString(),
                    s.get("isDeleted").asBoolean()
                );
            });
        }
    }

    public Shelf updateShelf(String shelfId, UpdateShelfRequest req) {
        try (Session session = driver.session(SessionConfig.forDatabase(database))) {
            return session.executeWrite(tx -> {
                Record rec = tx.run("""
                        MATCH (s:Shelf {shelfId: $id})
                        WHERE s.isDeleted = false
                        SET s.shelfName = $name,
                            s.partNumber = $pn,
                            s.updatedAt = datetime()
                        RETURN s
                        """, Values.parameters(
                            "id", shelfId,
                            "name", req.getShelfName(),
                            "pn", req.getPartNumber()
                        )).single();
                Node s = rec.get("s").asNode();
                return new Shelf(
                    s.get("shelfId").asString(),
                    s.get("shelfName").asString(),
                    s.get("partNumber").asString(),
                    s.get("isDeleted").asBoolean()
                );
            });
        }
    }

    public void softDeleteShelf(String shelfId) {
        try (Session session = driver.session(SessionConfig.forDatabase(database))) {
            session.executeWrite(tx -> {
                tx.run("""
                    MATCH (s:Shelf {shelfId: $id})
                    WHERE s.isDeleted = false
                    SET s.isDeleted = true,
                        s.deletedAt = datetime(),
                        s.updatedAt = datetime()
                    """, Values.parameters("id", shelfId));

                tx.run("""
                    MATCH (sp:ShelfPosition)-[r:HAS]->(s:Shelf {shelfId: $id})
                    DELETE r
                    """, Values.parameters("id", shelfId));
                return null;
            });
        }
    }

    public void attachShelfToPosition(String shelfPositionId, String shelfId) {
        try (Session session = driver.session(SessionConfig.forDatabase(database))) {
            session.executeWrite(tx -> {
                Record spRec = tx.run("""
                    MATCH (sp:ShelfPosition {shelfPositionId: $spId})
                    WHERE sp.isDeleted = false
                    OPTIONAL MATCH (sp)-[:HAS]->(existing:Shelf)
                    RETURN sp, existing
                    """, Values.parameters("spId", shelfPositionId)).single();

                if (!spRec.get("existing").isNull()) {
                    throw new IllegalStateException("ShelfPosition already occupied");
                }

                Record shelfRec = tx.run("""
                    MATCH (s:Shelf {shelfId: $shelfId})
                    WHERE s.isDeleted = false
                    OPTIONAL MATCH (anySp:ShelfPosition)-[:HAS]->(s)
                    RETURN s, anySp
                    """, Values.parameters("shelfId", shelfId)).single();

                if (!shelfRec.get("anySp").isNull()) {
                    throw new IllegalStateException("Shelf is already attached to another position");
                }

                tx.run("""
                    MATCH (sp:ShelfPosition {shelfPositionId: $spId})
                    MATCH (s:Shelf {shelfId: $shelfId})
                    MERGE (sp)-[:HAS]->(s)
                    SET s.updatedAt = datetime()
                    """, Values.parameters("spId", shelfPositionId, "shelfId", shelfId));

                return null;
            });
        }
    }

    public void detachShelfFromPosition(String shelfPositionId) {
        try (Session session = driver.session(SessionConfig.forDatabase(database))) {
            session.executeWrite(tx -> {
                tx.run("""
                    MATCH (sp:ShelfPosition {shelfPositionId: $spId})
                    WHERE sp.isDeleted = false
                    OPTIONAL MATCH (sp)-[r:HAS]->(s:Shelf)
                    WHERE s.isDeleted = false
                    DELETE r
                    """, Values.parameters("spId", shelfPositionId));
                return null;
            });
        }
    }
}