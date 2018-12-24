package com.example.demo.service;

import com.example.demo.dto.IProduct;
import io.reactivex.Flowable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.stream.Stream;

public interface ProductService {
    IProduct save(IProduct product);

    Optional<IProduct> findById(String id);

    void delete(String id);

    Page<IProduct> findAll(Pageable pageable);

    Flowable<IProduct> getProductsPageStream(Pageable pageable);
}
