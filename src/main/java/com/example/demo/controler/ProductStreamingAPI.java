package com.example.demo.controler;

import com.example.demo.Constants;
import com.example.demo.dto.IProduct;
import com.example.demo.service.ProductService;
import io.reactivex.Flowable;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductStreamingAPI {
    private final ProductService service;

    @RequestMapping(path = "/findAllUsingSolrTemplate", method = RequestMethod.GET)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Failure", response = IProduct.class, responseContainer = "List")
    })
    public Flowable<IProduct> findAllUsingSolrTemplate(@PageableDefault(page = 0, size = Constants.DEFAULT_PAGE_SIZE) Pageable pageable) {
        return service.findUsingSolrTemplate(pageable);
    }

    @RequestMapping(path = "/findAllUsingStreamFactory", method = RequestMethod.GET)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Failure", response = IProduct.class, responseContainer = "List")
    })

    public Flowable<IProduct> findAllUsingStreamFactory(@PageableDefault(page = 0, size = Constants.DEFAULT_PAGE_SIZE) Pageable pageable) {
        return service.findUsingStreamFactory(pageable);
    }
}
