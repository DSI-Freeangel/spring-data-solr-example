package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends SolrCrudRepository<Product, String> {

}
