package com.example.demo.controller;

import com.example.demo.Constants;
import com.example.demo.dto.IProduct;
import com.example.demo.service.ProductService;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductAPI {
    private final ProductService service;

    @RequestMapping(method = RequestMethod.POST)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success", response = IProduct.class)
    })
    public Single<IProduct> save(@RequestBody IProduct product) {
        return service.save(product);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success", response = IProduct.class)
    })
    public Maybe<IProduct> get(@PathVariable String id) {
        return service.findById(id);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Single<Page<IProduct>> findAll(@PageableDefault(page = 0, size = Constants.DEFAULT_PAGE_SIZE) Pageable pageable) {
        return service.findAll(pageable);
    }
}
