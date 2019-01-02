package com.example.demo.service;

import com.example.demo.dto.IProduct;
import com.example.demo.dto.ProductDTO;
import com.example.demo.model.Product;
import com.example.demo.repository.CustomProductRepository;
import com.example.demo.repository.ProductRepository;
import io.reactivex.Flowable;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository;
    private final CustomProductRepository customRepository;
    private final MapperFacade mapper;

    @Override
    public IProduct save(IProduct product) {
        Product existing = Optional.ofNullable(product)
                .map(IProduct::getId)
                .flatMap(repository::findById)
                .orElseGet(Product::new);
        mapper.map(product, existing);
        existing = repository.save(existing);
        return mapper.map(existing, ProductDTO.class);
    }

    @Override
    public Optional<IProduct> findById(String id) {
        return repository.findById(id)
                .map(product -> mapper.map(product, ProductDTO.class));
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }

    @Override
    public Page<IProduct> findAll(Pageable pageable) {
        Page<Product> products = repository.findAll(pageable);
        List<ProductDTO> result = mapper.mapAsList(products, ProductDTO.class);
        return new PageImpl(result, pageable, products.getTotalElements());
    }

    @Override
    public Flowable<IProduct> findUsingStreamFactory(Pageable pageable) {
        return customRepository.findUsingStreamFactory()
                .filter(Objects::nonNull)
                .map(product -> mapper.map(product, ProductDTO.class));
    }

    @Override
    public Flowable<IProduct> findUsingSolrTemplate(Pageable pageable) {
        return customRepository.findUsingSolrTemplate()
                .filter(Objects::nonNull)
                .map(product -> mapper.map(product, ProductDTO.class));
    }

}
