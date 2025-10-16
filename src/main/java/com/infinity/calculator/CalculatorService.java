package com.infinity.calculator;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CalculatorService {
    private final CalculatorEngine engine;
    private final ExpressionParser parser;
    private final MemoryStore memory;

    public CalculatorService() {
        this.engine = new CalculatorEngine();
        this.parser = new ExpressionParser(engine);
        this.memory = new MemoryStore();
    }

    public Result evaluate(String expression, AngleMode mode) {
        try {
            BigDecimal v = parser.evaluate(expression, mode);
            return new Result(format(v), null);
        } catch (ArithmeticException ae) {
            return new Result(null, ae.getMessage());
        } catch (Exception e) {
            return new Result(null, "Invalid expression");
        }
    }

    public BigDecimal rawEvaluate(String expression, AngleMode mode) { return parser.evaluate(expression, mode); }

    public MemoryResult memoryOp(String op, BigDecimal value) {
        switch (op) {
            case "M+" -> { if (value != null) memory.add(value); return new MemoryResult(memory.recall()); }
            case "M-" -> { if (value != null) memory.subtract(value); return new MemoryResult(memory.recall()); }
            case "MR" -> { return new MemoryResult(memory.recall()); }
            case "MC" -> { memory.clear(); return new MemoryResult(memory.recall()); }
            default -> throw new IllegalArgumentException("Unknown memory op");
        }
    }

    private String format(BigDecimal v) {
        DecimalFormat df = new DecimalFormat("#,##0.###############", DecimalFormatSymbols.getInstance(Locale.US));
        return df.format(v);
    }

    public static class Result {
        public final String result;
        public final String error;
        public Result(String result, String error) { this.result = result; this.error = error; }
    }

    public static class MemoryResult {
        public final BigDecimal memory;
        public MemoryResult(BigDecimal memory) { this.memory = memory; }
    }
}


