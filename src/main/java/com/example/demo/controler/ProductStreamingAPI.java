package com.example.demo.controler;

import com.example.demo.Constants;
import com.example.demo.dto.IProduct;
import com.example.demo.service.ProductService;
import io.reactivex.Flowable;
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
    public Flowable<IProduct> findAllUsingSolrTemplate(@PageableDefault(page = 0, size = Constants.DEFAULT_PAGE_SIZE) Pageable pageable) {
        return service.findUsingSolrTemplate(pageable);
    }

    @RequestMapping(path = "/findAllUsingStreamFactory", method = RequestMethod.GET)
    public Flowable<IProduct> findAllUsingStreamFactory(@PageableDefault(page = 0, size = Constants.DEFAULT_PAGE_SIZE) Pageable pageable) {
        return service.findUsingStreamFactory(pageable);
    }
}
