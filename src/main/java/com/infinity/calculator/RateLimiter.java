package com.infinity.calculator;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimiter {
    private final int limit;
    private final long windowMs;
    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    public RateLimiter(int limit, long windowMs) {
        this.limit = limit;
        this.windowMs = windowMs;
    }

    public boolean allow(String key) {
        long now = Instant.now().toEpochMilli();
        Window w = windows.computeIfAbsent(key, k -> new Window(now, new AtomicInteger(0)));
        synchronized (w) {
            if (now - w.start >= windowMs) { w.start = now; w.count.set(0); }
            if (w.count.incrementAndGet() <= limit) return true; else return false;
        }
    }

    private static class Window {
        long start;
        AtomicInteger count;
        Window(long start, AtomicInteger count) { this.start = start; this.count = count; }
    }
}


