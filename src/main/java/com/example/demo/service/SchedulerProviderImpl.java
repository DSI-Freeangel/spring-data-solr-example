package com.example.demo.service;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import org.springframework.stereotype.Component;

@Component
public class SchedulerProviderImpl implements SchedulerProvider {
    @Override
    public Scheduler io() {
        return Schedulers.io();
    }

    @Override
    public Scheduler computation() {
        return Schedulers.computation();
    }
}
