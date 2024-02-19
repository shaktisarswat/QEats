
/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.javaproject.qeats.models;

//import com.javaproject.qeats.dto.Item;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
//import jdk.jfr.Enabled;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "menus")
@NoArgsConstructor
@AllArgsConstructor
//@Entity
public class MenuEntity {

  @Id
  private String id;

  @NotNull
  private String restaurantId;

//  @NotNull
//  private List<Item> items = new ArrayList();

}
