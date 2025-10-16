package com.infinity.calculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class CalculatorEngine {
    private final MathContext mc = new MathContext(34, RoundingMode.HALF_UP);

    public BigDecimal add(BigDecimal a, BigDecimal b) { return a.add(b, mc); }
    public BigDecimal subtract(BigDecimal a, BigDecimal b) { return a.subtract(b, mc); }
    public BigDecimal multiply(BigDecimal a, BigDecimal b) { return a.multiply(b, mc); }
    public BigDecimal divide(BigDecimal a, BigDecimal b) {
        if (b.compareTo(BigDecimal.ZERO) == 0) throw new ArithmeticException("Division by zero");
        return a.divide(b, mc);
    }
    public BigDecimal percent(BigDecimal a) { return a.divide(BigDecimal.valueOf(100), mc); }
    public BigDecimal negate(BigDecimal a) { return a.negate(mc); }
    public BigDecimal reciprocal(BigDecimal a) {
        if (a.compareTo(BigDecimal.ZERO) == 0) throw new ArithmeticException("Division by zero");
        return BigDecimal.ONE.divide(a, mc);
    }
    public BigDecimal pow(BigDecimal a, BigDecimal b) {
        if (b.scale() == 0) return a.pow(b.intValueExact(), mc);
        return exp(ln(a).multiply(b, mc));
    }
    public BigDecimal sqrt(BigDecimal a) {
        if (a.compareTo(BigDecimal.ZERO) < 0) throw new ArithmeticException("Domain error");
        return new BigDecimal(Math.sqrt(a.doubleValue()), mc);
    }
    public BigDecimal ln(BigDecimal a) {
        if (a.compareTo(BigDecimal.ZERO) <= 0) throw new ArithmeticException("Domain error");
        return new BigDecimal(Math.log(a.doubleValue()), mc);
    }
    public BigDecimal log10(BigDecimal a) {
        if (a.compareTo(BigDecimal.ZERO) <= 0) throw new ArithmeticException("Domain error");
        return new BigDecimal(Math.log10(a.doubleValue()), mc);
    }
    public BigDecimal sin(BigDecimal a, AngleMode mode) {
        double x = a.doubleValue();
        if (mode == AngleMode.DEG) x = Math.toRadians(x);
        return new BigDecimal(Math.sin(x), mc);
    }
    public BigDecimal cos(BigDecimal a, AngleMode mode) {
        double x = a.doubleValue();
        if (mode == AngleMode.DEG) x = Math.toRadians(x);
        return new BigDecimal(Math.cos(x), mc);
    }
    public BigDecimal tan(BigDecimal a, AngleMode mode) {
        double x = a.doubleValue();
        if (mode == AngleMode.DEG) x = Math.toRadians(x);
        return new BigDecimal(Math.tan(x), mc);
    }
    public BigDecimal asin(BigDecimal a, AngleMode mode) {
        double v = Math.asin(a.doubleValue());
        if (Double.isNaN(v)) throw new ArithmeticException("Domain error");
        return new BigDecimal(mode == AngleMode.DEG ? Math.toDegrees(v) : v, mc);
    }
    public BigDecimal acos(BigDecimal a, AngleMode mode) {
        double v = Math.acos(a.doubleValue());
        if (Double.isNaN(v)) throw new ArithmeticException("Domain error");
        return new BigDecimal(mode == AngleMode.DEG ? Math.toDegrees(v) : v, mc);
    }
    public BigDecimal atan(BigDecimal a, AngleMode mode) {
        double v = Math.atan(a.doubleValue());
        return new BigDecimal(mode == AngleMode.DEG ? Math.toDegrees(v) : v, mc);
    }
    public BigDecimal factorial(int n) {
        if (n < 0) throw new ArithmeticException("Domain error");
        BigDecimal result = BigDecimal.ONE;
        for (int i = 2; i <= n; i++) result = result.multiply(BigDecimal.valueOf(i), mc);
        return result;
    }
    public BigDecimal exp(BigDecimal a) { return new BigDecimal(Math.exp(a.doubleValue()), mc); }
}


