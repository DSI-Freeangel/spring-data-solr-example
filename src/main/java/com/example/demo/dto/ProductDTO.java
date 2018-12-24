package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class ProductDTO implements IProduct {

	private final String id;
	private final String name;
	private final boolean available;
	private final List<String> features;
	private final Float price;
	private final List<String> categories;
	private final Integer popularity;

}