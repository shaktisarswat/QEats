package com.javaproject.qeats.models;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "items")
public class ItemEntity {

    @Id
    private String id;

    @NotNull
    private String itemId;

    @NotNull
    private String name;

    @NotNull
    private String imageUrl;

    @NotNull
    private Double price;

    @NotNull
    private List<String> attributes = new ArrayList<>();

}
