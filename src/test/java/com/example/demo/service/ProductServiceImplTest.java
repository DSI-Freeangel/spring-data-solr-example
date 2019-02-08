package com.example.demo.service;

import com.example.demo.dto.ProductDTO;
import com.example.demo.model.Product;
import com.example.demo.repository.CustomProductRepository;
import com.example.demo.repository.ProductRepository;
import io.reactivex.schedulers.Schedulers;
import ma.glasnost.orika.MapperFacade;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceImplTest {

    public static final String ID = "1";

    @Mock
    private SchedulerProvider schedulerProvider;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private CustomProductRepository customProductRepository;

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductServiceImpl service;

    @Before
    public void setUp() {
        when(schedulerProvider.io()).thenReturn(Schedulers.trampoline());
        when(schedulerProvider.computation()).thenReturn(Schedulers.trampoline());
    }

    @Test
    public void productCreatedSuccessfully() {
        final ProductDTO productDTO = mock(ProductDTO.class);
        final Product product = mock(Product.class);
        when(productDTO.getId()).thenReturn(ID);
        when(repository.findById(ID)).thenReturn(Optional.empty());
        when(repository.save(any())).thenReturn(product);
        when(mapperFacade.map(eq(product), eq(ProductDTO.class))).thenReturn(productDTO);

        service.save(productDTO)
                .test()
                .assertValueCount(1)
                .assertValue(productDTO)
                .assertComplete()
                .dispose();
    }

    @Test
    public void productFoundById() {
        final Product product = mock(Product.class);
        final ProductDTO productDTO = mock(ProductDTO.class);
        when(repository.findById(ID)).thenReturn(Optional.of(product));
        when(mapperFacade.map(eq(product), eq(ProductDTO.class))).thenReturn(productDTO);

        service.findById(ID)
                .test()
                .assertValueCount(1)
                .assertValue(productDTO)
                .assertComplete()
                .dispose();
    }
}
