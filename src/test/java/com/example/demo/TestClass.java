package com.example.demo;

import io.reactivex.Flowable;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.flowables.GroupedFlowable;
import io.reactivex.schedulers.Schedulers;
import org.junit.Test;
import org.reactivestreams.Publisher;

import java.sql.SQLOutput;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TestClass {

    @Test
    public void testFlowable1() {
        Flowable.interval(10, 30, TimeUnit.SECONDS, Schedulers.computation())
                .observeOn(Schedulers.io())
                .doOnNext(value -> System.out.println(Thread.currentThread().getName() + ": Iteration: " + value))
                .flatMap(this::compute1)
                .doOnNext(value -> System.out.println(Thread.currentThread().getName() + ": Result: " + value))
                .doOnError(e -> System.out.println(e))
                .retry()
                .blockingSubscribe();
    }

    private Publisher<Integer> compute1(Long aLong) {
        AtomicInteger sum = new AtomicInteger();
        return Flowable.range(1, 1000)
                .doOnNext(value -> System.out.println(Thread.currentThread().getName() + ": Emitting next: " + value))
                .observeOn(Schedulers.io())
                .parallel(4)
                .runOn(Schedulers.io())
                .map(this::updateInteger)
                .sequential()
                .observeOn(Schedulers.computation())
                .doOnNext(sum::getAndAdd)
                .doOnNext(value -> System.out.println(Thread.currentThread().getName() + ": Next item: " + value))
                .map(value -> value%2 != 0)
                .all(Boolean.TRUE::equals)
                .toFlowable()
                .doOnNext(value -> System.out.println(Thread.currentThread().getName() + ": Condition: " + value))
                .map(value -> sum.get());
    }

    private Integer updateInteger(Integer before) throws InterruptedException {
        if(before%2 == 0) {
            int result = before + 1;
            Thread.sleep(Math.round(Math.random() * 300));
            System.out.println(Thread.currentThread().getName() + ": <<<<< value: " + result);
            return result;//assume some time consuming operation is here
        }
        System.out.println(Thread.currentThread().getName() + ": >>>>> value: " + before);
        return before;
    }

    @Test
    public void testFlowable2() {
        Flowable.interval(10, 30, TimeUnit.SECONDS, Schedulers.computation())
                .observeOn(Schedulers.io())
                .doOnNext(value -> System.out.println(Thread.currentThread().getName() + ": Iteration: " + value))
                .flatMap(this::compute2)
                .doOnNext(value -> System.out.println(Thread.currentThread().getName() + ": Result: " + value))
                .doOnError(e -> System.out.println(e))
                .retry()
                .blockingSubscribe();
    }

    private Publisher<Integer> compute2(Long aLong) {
        AtomicInteger sum = new AtomicInteger();
        return Flowable.range(1, 1000)
                .doOnNext(value -> System.out.println(Thread.currentThread().getName() + ": Emitting next: " + value))
                .doOnComplete(() -> System.out.println(Thread.currentThread().getName() + ": Emission complete!"))
                .observeOn(Schedulers.io())
                .groupBy(value -> value%2 == 0)
                .doOnNext(value -> System.out.println(Thread.currentThread().getName() + ": Group: " + value.getKey()))
                .map(this::doWithGroups)
                .flatMap(a -> a)
                .observeOn(Schedulers.computation())
                .doOnNext(sum::getAndAdd)
                .doOnNext(value -> System.out.println(Thread.currentThread().getName() + ": Next item: " + value))
                .map(value -> value%2 != 0)
                .all(Boolean.TRUE::equals)
                .toFlowable()
                .doOnNext(value -> System.out.println(Thread.currentThread().getName() + ": Condition: " + value))
                .map(value -> sum.get());
    }

    private Flowable<Integer> doWithGroups(GroupedFlowable<Boolean, Integer> group) {
        if(group.getKey()) {
            return group.parallel(4)
                    .runOn(Schedulers.io())
                    .map(value -> value + 1)//assume some time consuming operation is here
                    .doOnNext(v -> Thread.sleep(Math.round(Math.random() * 300)))
                    .doOnNext(value -> System.out.println(Thread.currentThread().getName() + ": <<<<< value: " + value))
                    .doOnComplete(() -> System.out.println(Thread.currentThread().getName() + ": Part 1 Complete!"))
                    .sequential();
        }
        return group
                .doOnNext(value -> System.out.println(Thread.currentThread().getName() + ": >>>>> value: " + value))
                .doOnComplete(() -> System.out.println(Thread.currentThread().getName() + ": Part 2 Complete!"));
    }

    @Test
    public void testFlowable3() throws InterruptedException {
        Flowable.interval(10, 30, TimeUnit.SECONDS, Schedulers.computation())
                .observeOn(Schedulers.io())
                .doOnNext(value -> System.out.println(Thread.currentThread().getName() + ": Iteration: " + value))
                .flatMap(this::compute3)
                .doOnNext(value -> System.out.println(Thread.currentThread().getName() + ": Result: " + value))
                .doOnError(e -> System.out.println(e))
                .retry()
                .publish().connect();
        Thread.sleep(Integer.MAX_VALUE);
    }

    private Publisher<Integer> compute3(Long aLong) {
        AtomicInteger sum = new AtomicInteger();
        ConnectableFlowable<Integer> publish = Flowable.range(1, 1000)
                .doOnNext(value -> System.out.println(Thread.currentThread().getName() + ": Emitting next: " + value))
                .doOnComplete(() -> System.out.println(Thread.currentThread().getName() + ": Emission complete!"))
                .publish();
        System.out.println("After publish");
        ConnectableFlowable<Integer> part1 = publish
                .filter(value -> value % 2 == 0)
                .parallel(4)
                .runOn(Schedulers.io())
                .map(value -> value + 1)//assume some time consuming operation is here
                .doOnNext(v -> Thread.sleep(Math.round(Math.random() * 300)))
                .doOnNext(value -> System.out.println(Thread.currentThread().getName() + ": <<<<< value: " + value))
                .doOnComplete(() -> System.out.println(Thread.currentThread().getName() + ": Part 1 Complete!"))
                .sequential()
                .publish();
        ConnectableFlowable<Integer> part2 = publish.filter(value -> value % 2 == 1)
                .doOnNext(value -> System.out.println(Thread.currentThread().getName() + ": >>>>> value: " + value))
                .doOnComplete(() -> System.out.println(Thread.currentThread().getName() + ": Part 2 Complete!"))
                .publish();
        System.out.println("Before connect");
        return Flowable.mergeArray(part1, part2)
                .doOnComplete(() -> System.out.println(Thread.currentThread().getName() + ": Complete after merge!"))
                .observeOn(Schedulers.computation())
                .doOnNext(sum::getAndAdd)
                .doOnNext(value -> System.out.println(Thread.currentThread().getName() + ": Next item: " + value))
                .map(value -> value%2 != 0)
                .doOnComplete(() -> System.out.println(Thread.currentThread().getName() + ": Complete before all!"))
                .all(Boolean.TRUE::equals)
                .toFlowable()
                .doOnNext(value -> System.out.println(Thread.currentThread().getName() + ": Condition: " + value))
                .map(value -> sum.get())
                .doOnSubscribe(subscription -> {
                    part1.connect();
                    part2.connect();
                    publish.connect();
                }).publish().autoConnect();
    }

    @Test
    public void testFlowable4() {
        Flowable.interval(10, 30, TimeUnit.SECONDS, Schedulers.computation())
                .observeOn(Schedulers.io())
                .doOnNext(value -> System.out.println(Thread.currentThread().getName() + ": Iteration: " + value))
                .flatMap(this::compute4)
                .doOnNext(value -> System.out.println(Thread.currentThread().getName() + ": Result: " + value))
                .doOnError(e -> System.out.println(e))
                .retry()
                .blockingSubscribe();
    }

    private Publisher<Integer> compute4(Long aLong) {
        AtomicInteger sum = new AtomicInteger();
        return Flowable.range(1, 1000)
                .doOnNext(value -> System.out.println(Thread.currentThread().getName() + ": Emitting next: " + value))
                .observeOn(Schedulers.io())
                .flatMap(this::flatMapOperation)
                .observeOn(Schedulers.computation())
                .doOnNext(sum::getAndAdd)
                .doOnNext(value -> System.out.println(Thread.currentThread().getName() + ": Next item: " + value))
                .map(value -> value%2 != 0)
                .all(Boolean.TRUE::equals)
                .toFlowable()
                .doOnNext(value -> System.out.println(Thread.currentThread().getName() + ": Condition: " + value))
                .map(value -> sum.get());
    }

    private Flowable<Integer> flatMapOperation(Integer input) {
        if(input%2 == 0) {
            return Flowable.just(input).parallel(4)
                    .runOn(Schedulers.io())
                    .map(value -> value + 1)//assume some time consuming operation is here
                    .doOnNext(v -> Thread.sleep(Math.round(Math.random() * 300)))
                    .doOnNext(value -> System.out.println(Thread.currentThread().getName() + ": <<<<< value: " + value))
                    .sequential();
        }
        return Flowable.just(input)
                .doOnNext(value -> System.out.println(Thread.currentThread().getName() + ": >>>>> value: " + value));
    }
}
