package com.assignment.assignment_backend.service;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.springframework.stereotype.Service;

@Service
public class Neo4jService {
	private final Driver driver;
	
	public Neo4jService(Driver driver)
	{
		this.driver=driver;
	}
	
	public boolean isDriverConnected()
	{
		try
		{
			driver.verifyConnectivity();
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	

    public String pingDatabase() {

        try (Session session = driver.session(SessionConfig.forDatabase("neo4j"))) {
            Result result = session.run("RETURN 'Connected to Neo4j!' AS message");
            return result.single().get("message").asString();
        } catch (Exception e) {
            System.err.println("Neo4j ping failed: " + e.getMessage());
            return null;
        }
    }
}
