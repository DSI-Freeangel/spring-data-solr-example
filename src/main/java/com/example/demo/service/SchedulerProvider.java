package com.example.demo.service;

import io.reactivex.Scheduler;

public interface SchedulerProvider {
    Scheduler io();
    Scheduler computation();
}
