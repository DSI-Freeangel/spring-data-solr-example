package com.example.demo.repository;

import com.example.demo.model.Product;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import lombok.RequiredArgsConstructor;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.StreamingResponseCallback;
import org.apache.solr.common.SolrDocument;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.atomic.AtomicLong;

@Repository
@RequiredArgsConstructor
public class CustomProductRepositoryImpl implements CustomProductRepository {
    private final SolrTemplate solrTemplate;

    @Override
    public Flowable<Product> performStreamSearch() {
        return Flowable.create(emitter -> {
            solrTemplate.execute((solrClient) -> {
                SolrQuery query = new SolrQuery("*:*");
                query.setRows(10);
                AtomicLong cursor = new AtomicLong(0);
                AtomicLong found = new AtomicLong(10);
                solrClient.queryAndStreamResponse("techproducts", query, new StreamingResponseCallback() {
                    @Override
                    public void streamSolrDocument(SolrDocument solrDocument) {
                        Product product = solrTemplate.convertSolrDocumentToBean(solrDocument, Product.class);
                        emitter.onNext(product);
                        if(cursor.incrementAndGet() == found.get()) {
                            emitter.onComplete();
                        }
                    }

                    @Override
                    public void streamDocListInfo(long numFound, long start, Float maxScore) {
                        System.out.println(String.format("found=%s, start=%s, maxScore=%s", numFound, start, maxScore));
                    }
                });
                return null;
            });
        }, BackpressureStrategy.BUFFER);
    }
}
