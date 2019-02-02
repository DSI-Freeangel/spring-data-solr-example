package com.example.demo.service;

import com.example.demo.dto.IProduct;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Single<IProduct> save(IProduct product);

    Maybe<IProduct> findById(String id);

    void delete(String id);

    Single<Page<IProduct>> findAll(Pageable pageable);

    Flowable<IProduct> findUsingStreamFactory();

    Flowable<IProduct> findUsingSolrTemplate();
}
