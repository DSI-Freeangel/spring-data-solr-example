package com.example.demo.controller;

import com.example.demo.dto.IProduct;
import com.example.demo.service.ProductService;
import io.reactivex.Flowable;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductStreamingAPI {
    private final ProductService service;

    @RequestMapping(path = "/findAllUsingSolrTemplate", method = RequestMethod.GET)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success", response = IProduct.class, responseContainer = "List")
    })
    public Flowable<IProduct> findAllUsingSolrTemplate() {
        return service.findUsingSolrTemplate();
    }

    @RequestMapping(path = "/findAllUsingStreamFactory", method = RequestMethod.GET)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success", response = IProduct.class, responseContainer = "List")
    })
    public Flowable<IProduct> findAllUsingStreamFactory() {
        return service.findUsingStreamFactory();
    }
}
