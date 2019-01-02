package com.example.demo.repository;

import com.example.demo.model.Product;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.StreamingResponseCallback;
import org.apache.solr.client.solrj.io.SolrClientCache;
import org.apache.solr.client.solrj.io.Tuple;
import org.apache.solr.client.solrj.io.stream.StreamContext;
import org.apache.solr.client.solrj.io.stream.TupleStream;
import org.apache.solr.client.solrj.io.stream.expr.DefaultStreamFactory;
import org.apache.solr.client.solrj.io.stream.expr.StreamFactory;
import org.apache.solr.common.SolrDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Repository
public class CustomProductRepositoryImpl implements CustomProductRepository {
    private final SolrTemplate solrTemplate;
    private final StreamFactory streamFactory;
    private final StreamContext streamContext;

    public CustomProductRepositoryImpl(SolrTemplate solrTemplate, @Value("${spring.data.solr.zk-host}") String zkHost) {
        this.solrTemplate = solrTemplate;
        this.streamFactory = new DefaultStreamFactory().withCollectionZkHost("techproducts", zkHost);
        this.streamContext = new StreamContext();
        streamContext.setStreamFactory(streamFactory);
        streamContext.setSolrClientCache(new SolrClientCache());
    }

    @Override
    public Flowable<Product> findUsingSolrTemplate() {
        SolrQuery query = new SolrQuery("*:*");
        query.setRows(10);
        return findWithSolrTemplate("techproducts", query, Product.class);
    }

    private <T> Flowable<T> findWithSolrTemplate(String collection, SolrQuery query, Class<T> returnType) {
        return Flowable.create(emitter ->
            solrTemplate.execute((solrClient) -> {
                //TODO: Temporary. Pagination should be used instead
                AtomicLong cursor = new AtomicLong(0);
                AtomicLong found = new AtomicLong(10);
                solrClient.queryAndStreamResponse(collection, query, new StreamingResponseCallback() {
                    @Override
                    public void streamSolrDocument(SolrDocument solrDocument) {
                        emitter.onNext(solrDocument);
                        if (cursor.incrementAndGet() == found.get()) {
                            emitter.onComplete();
                        }
                    }

                    @Override
                    public void streamDocListInfo(long numFound, long start, Float maxScore) {
                        log.info(String.format("found=%s, start=%s, maxScore=%s", numFound, start, maxScore));
                        if (numFound < found.get()) {
                            found.set(numFound);
                        }
                    }
                });
                return null;
            })
        , BackpressureStrategy.BUFFER)
                .parallel()
                .runOn(Schedulers.computation())
                .map(document -> solrTemplate.convertSolrDocumentToBean((SolrDocument)document, returnType))
                .sequential();
    }

    @Override
    public Flowable<Product> findUsingStreamFactory() {
        return findWithStreamingQuery("parallel(techproducts, search(techproducts, q=\"*.*\", fl=\"id, name, inStock, cat, price, " +
                "features\", sort=\"id asc\", partitionKeys=\"id\"), workers=\"2\", sort=\"id asc\")", Product.class);
    }

    private <T> Flowable<T> findWithStreamingQuery(String query, Class<T> returnType) {
        return Flowable.create(emitter -> {
            TupleStream stream = streamFactory.constructStream(query);
            stream.setStreamContext(streamContext);
            try {
                stream.open();
                Tuple tuple;
                while (!(tuple = stream.read()).EOF) {
                    emitter.onNext(tuple);
                }
            } finally {
                emitter.onComplete();
                stream.close();
            }
        }, BackpressureStrategy.BUFFER)
                .parallel()
                .runOn(Schedulers.computation())
                .map(Tuple.class::cast)
                .map(this::toSolrDocument)
                .map(document -> solrTemplate.convertSolrDocumentToBean(document, returnType))
                .sequential();
    }

    private SolrDocument toSolrDocument(Tuple tuple) {
        final SolrDocument document = new SolrDocument();
        for (Object key : tuple.fields.keySet()) {
            document.setField((String) key, tuple.get(key));
        }
        return document;
    }
}
