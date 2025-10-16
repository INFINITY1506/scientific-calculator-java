package com.infinity.calculator;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

public class MemoryStore {
    private final AtomicReference<BigDecimal> memory = new AtomicReference<>(BigDecimal.ZERO);
    public void add(BigDecimal value) { memory.updateAndGet(m -> m.add(value)); }
    public void subtract(BigDecimal value) { memory.updateAndGet(m -> m.subtract(value)); }
    public BigDecimal recall() { return memory.get(); }
    public void clear() { memory.set(BigDecimal.ZERO); }
}


