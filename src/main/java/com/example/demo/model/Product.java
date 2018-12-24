package com.example.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.List;

@SolrDocument(solrCoreName = "techproducts")
@Data
public class Product implements SearchableProductDefinition {

	@Id
	@Indexed(ID_FIELD_NAME)
	private String id;

	@Indexed(NAME_FIELD_NAME)
	private String name;

	@Indexed(AVAILABLE_FIELD_NAME)
	private boolean available;

	@Indexed(FEATURES_FIELD_NAME)
	private List<String> features;

	@Indexed(PRICE_FIELD_NAME)
	private Float price;

	@Indexed(CATEGORIES_FIELD_NAME)
	private List<String> categories;

	@Indexed
	private Integer popularity;

}