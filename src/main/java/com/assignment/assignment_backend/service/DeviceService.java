package com.assignment.assignment_backend.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Values;
import org.neo4j.driver.types.Node;
import org.springframework.beans.factory.annotation.Value;              
import org.springframework.stereotype.Service;

import com.assignment.assignment_backend.entity.Device;
import com.assignment.assignment_backend.entity.Shelf;
import com.assignment.assignment_backend.requests.CreateDeviceRequest;
import com.assignment.assignment_backend.requests.UpdateDeviceRequest;
import com.assignment.assignment_backend.view.DeviceView;
import com.assignment.assignment_backend.view.PositionView;

@Service
public class DeviceService {

    private final Driver driver;
    private final String database;                                

    public DeviceService(Driver driver, @Value("${neo4j.database:neo4j}") String database) {
        this.driver = driver;
        this.database = database;
    }

    public Device createDevice(CreateDeviceRequest req) {
        String deviceId = "D-" + UUID.randomUUID();
        int n = req.getNumberOfShelfPositions();

        try (Session session = driver.session(SessionConfig.forDatabase(database))) {  
            return session.executeWrite(tx -> {
                Record rec = tx.run("""
                        MERGE (d:Device { deviceId: $deviceId })
                        ON CREATE SET
                          d.deviceName = $deviceName,
                          d.partNumber = $partNumber,
                          d.buildingName = $buildingName,
                          d.deviceType = $deviceType,
                          d.numberOfShelfPositions = $n,
                          d.isDeleted = false,
                          d.createdAt = datetime(),
                          d.updatedAt = datetime()
                        ON MATCH SET
                          d.deviceName = $deviceName,
                          d.partNumber = $partNumber,
                          d.buildingName = $buildingName,
                          d.deviceType = $deviceType,
                          d.numberOfShelfPositions = $n,
                          d.updatedAt = datetime()
                        RETURN d
                        """,
                        Values.parameters(
                            "deviceId", deviceId,
                            "deviceName", req.getDeviceName(),
                            "partNumber", req.getPartNumber(),
                            "buildingName", req.getBuildingName(),
                            "deviceType", req.getDeviceType(),
                            "n", n
                        )
                ).single();

                for (int i = 1; i <= n; i++) {                      
                    String spId = "SP-" + UUID.randomUUID();
                    tx.run("""
                            MATCH (d:Device { deviceId: $deviceId })
                            MERGE (sp:ShelfPosition { shelfPositionId: $spId })
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
                            """,
                            Values.parameters(
                                "deviceId", deviceId,
                                "spId", spId,
                                "idx", i
                            )
                    );
                }

                Node d = rec.get("d").asNode();
                return new Device(
                    d.get("deviceId").asString(),
                    d.get("deviceName").asString(),
                    d.get("partNumber").asString(),
                    d.get("buildingName").asString(),
                    d.get("deviceType").asString(),
                    d.get("numberOfShelfPositions").asInt(),
                    d.get("isDeleted").asBoolean()
                );
            });
        }
    }

    public List<Device> getAllDevices() {                            
        try (Session session = driver.session(SessionConfig.forDatabase(database))) {
            return session.executeRead(tx -> {
                Result res = tx.run("""
                        MATCH (d:Device)
                        WHERE d.isDeleted = false
                        RETURN d ORDER BY d.deviceName
                        """);
                List<Device> list = new ArrayList<>();
                while (res.hasNext()) {
                    Node d = res.next().get("d").asNode();
                    list.add(new Device(
                        d.get("deviceId").asString(),
                        d.get("deviceName").asString(),
                        d.get("partNumber").asString(),
                        d.get("buildingName").asString(),
                        d.get("deviceType").asString(),
                        d.get("numberOfShelfPositions").asInt(),
                        d.get("isDeleted").asBoolean()
                    ));
                }
                return list;
            });
        }
    }

    public DeviceView getDeviceView(String deviceId) {
        try (Session session = driver.session(SessionConfig.forDatabase(database))) {
            return session.executeRead(tx -> {
                Result res = tx.run("""
                        MATCH (d:Device { deviceId: $deviceId })
                        WHERE d.isDeleted = false
                        OPTIONAL MATCH (d)-[:HAS]->(sp:ShelfPosition { isDeleted:false })    
                        OPTIONAL MATCH (sp)-[:HAS]->(s:Shelf { isDeleted:false })            
                        RETURN d, sp, s
                        """, Values.parameters("deviceId", deviceId));

                Device device = null;
                Map<String, PositionView> positionsMap = new LinkedHashMap<>();

                while (res.hasNext()) {
                    Record r = res.next();

                    if (device == null) {
                        Node dn = r.get("d").asNode();
                        device = new Device(
                            dn.get("deviceId").asString(),
                            dn.get("deviceName").asString(),
                            dn.get("partNumber").asString(),
                            dn.get("buildingName").asString(),
                            dn.get("deviceType").asString(),
                            dn.get("numberOfShelfPositions").asInt(),
                            dn.get("isDeleted").asBoolean()
                        );
                    }

                    if (!r.get("sp").isNull()) {
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

                        positionsMap.put(spId, new PositionView(spId, index, shelf));
                    }
                }

                if (device == null) {
                    throw new IllegalArgumentException("Device Not Found: " + deviceId);
                }

                List<PositionView> ordered = positionsMap.values().stream()
                    .sorted(Comparator.comparingInt(PositionView::getIndex))
                    .collect(Collectors.toList());

                return new DeviceView(device, ordered);
            });
        }
    }

    public Device updateDevice(String deviceId, UpdateDeviceRequest req) {
        try (Session session = driver.session(SessionConfig.forDatabase(database))) {
            return session.executeWrite(tx -> {
                Record rec = tx.run("""
                        MATCH (d:Device { deviceId: $deviceId })
                        WHERE d.isDeleted = false
                        SET d.deviceName = $name,
                            d.partNumber = $pn,
                            d.buildingName = $bn,
                            d.deviceType = $dt,
                            d.numberOfShelfPositions = $n,
                            d.updatedAt = datetime()
                        RETURN d
                        """,
                        Values.parameters(
                            "deviceId", deviceId,
                            "name", req.getDeviceName(),
                            "pn", req.getPartNumber(),
                            "bn", req.getBuildingName(),
                            "dt", req.getDeviceType(),
                            "n", req.getNumberOfShelfPositions()
                        )
                ).single();

                Node dn = rec.get("d").asNode();
                return new Device(
                    dn.get("deviceId").asString(),
                    dn.get("deviceName").asString(),
                    dn.get("partNumber").asString(),
                    dn.get("buildingName").asString(),
                    dn.get("deviceType").asString(),
                    dn.get("numberOfShelfPositions").asInt(),
                    dn.get("isDeleted").asBoolean()
                );
            });
        }
    }

    public void softDeleteDevice(String deviceId) {
        try (Session session = driver.session(SessionConfig.forDatabase(database))) {
            session.executeWrite(tx -> {
                tx.run("""
                        MATCH (d:Device { deviceId: $deviceId })
                        WHERE d.isDeleted = false
                        SET d.isDeleted = true,
                            d.deletedAt = datetime(),
                            d.updatedAt = datetime()
                        """, Values.parameters("deviceId", deviceId));

                tx.run("""
                        MATCH (d:Device { deviceId: $deviceId })-[:HAS]->(sp:ShelfPosition)
                        WHERE sp.isDeleted = false
                        SET sp.isDeleted = true,
                            sp.deletedAt = datetime(),
                            sp.updatedAt = datetime()
                        """, Values.parameters("deviceId", deviceId));

                tx.run("""
                        MATCH (d:Device { deviceId: $deviceId })-[:HAS]->(sp:ShelfPosition)
                        OPTIONAL MATCH (sp)-[r:HAS]->(s:Shelf)
                        WHERE s.isDeleted = false
                        DELETE r
                        """, Values.parameters("deviceId", deviceId));

                return null;
            });
        }
    }
}