package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
@JsonInclude(value= JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = ProductDTO.ProductDTOBuilder.class)
public class ProductDTO implements IProduct {

	private final String id;
	private final String name;
	private final boolean available;
	private final List<String> features;
	private final Float price;
	private final List<String> categories;
	private final Integer popularity;

	@JsonPOJOBuilder(withPrefix = "")
	public static class ProductDTOBuilder {

	}
}