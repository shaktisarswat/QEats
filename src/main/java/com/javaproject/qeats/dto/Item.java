
/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.javaproject.qeats.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @NotNull
    String itemId;
    @NotNull
    String name;
    @NotNull
    String imageUrl;
    @NotNull
    List<String> attributes = new ArrayList<>();
    @NotNull
    int price;
    private String id;
}