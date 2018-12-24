package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, defaultImpl = ProductDTO.class)
public interface IProduct {
    String getId();
    String getName();
    boolean isAvailable();
    List<String> getFeatures();
    Float getPrice();
    List<String> getCategories();
    Integer getPopularity();
}
