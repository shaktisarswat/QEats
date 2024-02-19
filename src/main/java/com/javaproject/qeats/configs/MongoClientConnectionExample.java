package com.javaproject.qeats.configs;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoClientConnectionExample {
    public static void main(String[] args) {
        String connectionString = "mongodb+srv://shaktisarswat01:7568127174@cluster0.u62ozhu.mongodb.net/?retryWrites=true&w=majority";

        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

//        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            // Attempt to connect to the 'admin' database and send a ping command
            MongoDatabase database = mongoClient.getDatabase("admin");
            database.runCommand(new Document("ping", 1));
            System.out.println("Pinged your deployment. You successfully connected to MongoDB!");
        } catch (MongoException e) {
            // Handle any MongoDB-specific exceptions
            System.err.println("Failed to connect to MongoDB: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // Handle any other unexpected exceptions
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
