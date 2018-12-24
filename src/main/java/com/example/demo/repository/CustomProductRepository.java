package com.example.demo.repository;

import com.example.demo.model.Product;
import io.reactivex.Flowable;

import java.util.List;

public interface CustomProductRepository {
    Flowable<Product> performStreamSearch();
}
