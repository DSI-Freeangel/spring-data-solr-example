package com.example.demo.service;

import com.example.demo.dto.IProduct;
import com.example.demo.dto.ProductDTO;
import com.example.demo.model.Product;
import com.example.demo.repository.CustomProductRepository;
import com.example.demo.repository.ProductRepository;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository;
    private final CustomProductRepository customRepository;
    private final MapperFacade mapper;
    private final SchedulerProvider schedulerProvider;

    @Override
    public Single<IProduct> save(@NotNull IProduct product) {
        return  Maybe.just(product)
                .map(IProduct::getId)
                .observeOn(schedulerProvider.io())
                .map(repository::findById)
                .observeOn(schedulerProvider.computation())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .switchIfEmpty(Single.just(new Product()))
                .map(existing -> copyFieldValuesToProduct(product, existing))
                .observeOn(schedulerProvider.io())
                .map(repository::save)
                .observeOn(schedulerProvider.computation())
                .map(result -> mapper.map(result, ProductDTO.class));
    }

    @Override
    public Maybe<IProduct> findById(String id) {
        return Maybe.just(id)
                .observeOn(schedulerProvider.io())
                .map(repository::findById)
                .observeOn(schedulerProvider.computation())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(product -> mapper.map(product, ProductDTO.class));
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }

    @Override
    public Single<Page<IProduct>> findAll(Pageable pageable) {
        return Single.just(pageable)
                .observeOn(schedulerProvider.io())
                .map(repository::findAll)
                .observeOn(schedulerProvider.computation())
                .map(this::mapToModelsPage);
    }

    @Override
    public Flowable<IProduct> findUsingStreamFactory() {
        return mapProductFlowToModels(customRepository::findUsingStreamFactory);
    }

    @Override
    public Flowable<IProduct> findUsingSolrTemplate() {
        return mapProductFlowToModels(customRepository::findUsingSolrTemplate);
    }

    private Product copyFieldValuesToProduct(IProduct product, Product existing) {
        mapper.map(product, existing);
        return existing;
    }

    private Page<IProduct> mapToModelsPage(Page<Product> products) {
        List<ProductDTO> result = mapper.mapAsList(products, ProductDTO.class);
        return new PageImpl(result, products.getPageable(), products.getTotalElements());
    }

    private Flowable<IProduct> mapProductFlowToModels(Supplier<Flowable<Product>> source) {
        return source.get()
                .observeOn(schedulerProvider.computation())
                .filter(Objects::nonNull)
                .map(product -> mapper.map(product, ProductDTO.class));
    }

}
