package com.example.demo.controler;

import com.example.demo.Constants;
import com.example.demo.dto.IProduct;
import com.example.demo.service.ProductService;
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
    public IProduct save(@RequestBody IProduct product) {
        return service.save(product);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public IProduct get(@PathVariable String id) {
        return service.findById(id).orElse(null);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Page<IProduct> findAll(@PageableDefault(page = 0, size = Constants.DEFAULT_PAGE_SIZE) Pageable pageable) {
        return service.findAll(pageable);
    }
}
