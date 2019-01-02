package com.example.demo.repository;

import com.example.demo.model.Product;
import io.reactivex.Flowable;

public interface CustomProductRepository {
    Flowable<Product> findUsingSolrTemplate();

    Flowable<Product> findUsingStreamFactory();
}
