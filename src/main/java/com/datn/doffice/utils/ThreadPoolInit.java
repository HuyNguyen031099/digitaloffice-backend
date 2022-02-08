package com.datn.doffice.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;

@Service
public final class ThreadPoolInit {
    @Value("${sign.manager.pool-size}")
    private Integer poolSize;

    @EventListener
    public void initialize(ApplicationReadyEvent event) {
        ThreadPoolUtil.quickService = Executors.newScheduledThreadPool(poolSize);
        ThreadPoolUtil.quickServiceSingle = Executors.newSingleThreadScheduledExecutor();
    }
}
