
package com.javaproject.qeats.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
//@Document(collection = "items")
@NoArgsConstructor
@Entity
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
