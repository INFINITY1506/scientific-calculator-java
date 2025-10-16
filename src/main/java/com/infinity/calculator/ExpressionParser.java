package com.infinity.calculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class ExpressionParser {
    private final CalculatorEngine engine;
    private final MathContext mc = new MathContext(34);

    public ExpressionParser(CalculatorEngine engine) {
        this.engine = engine;
    }

    public BigDecimal evaluate(String expr, AngleMode mode) {
        List<String> tokens = tokenize(expr);
        List<String> rpn = toRpn(tokens);
        return evalRpn(rpn, mode);
    }

    private List<String> tokenize(String s) {
        List<String> out = new ArrayList<>();
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (Character.isWhitespace(c)) { i++; continue; }
            if (Character.isDigit(c) || c == '.') {
                int j = i + 1;
                while (j < s.length()) {
                    char d = s.charAt(j);
                    if (Character.isDigit(d) || d == '.') j++; else break;
                }
                out.add(s.substring(i, j));
                i = j;
                continue;
            }
            if (Character.isLetter(c)) {
                int j = i + 1;
                while (j < s.length() && Character.isLetter(s.charAt(j))) j++;
                out.add(s.substring(i, j));
                i = j;
                continue;
            }
            out.add(String.valueOf(c));
            i++;
        }
        return out;
    }

    private int precedence(String op) {
        return switch (op) {
            case "+", "-" -> 1;
            case "*", "×", "/", "÷", "%" -> 2;
            case "^" -> 3;
            default -> 0;
        };
    }

    private boolean rightAssociative(String op) { return op.equals("^"); }

    private List<String> toRpn(List<String> tokens) {
        List<String> out = new ArrayList<>();
        Deque<String> ops = new ArrayDeque<>();
        String prev = null;
        for (String t : tokens) {
            if (isNumber(t) || t.equals("π") || t.equals("e")) {
                out.add(t);
                prev = t;
                continue;
            }
            if (isFunction(t)) {
                ops.push(t);
                prev = t;
                continue;
            }
            if (t.equals(",")) {
                while (!ops.isEmpty() && !ops.peek().equals("(")) out.add(ops.pop());
                prev = t;
                continue;
            }
            if (t.equals("(")) {
                ops.push(t);
                prev = t;
                continue;
            }
            if (t.equals(")")) {
                while (!ops.isEmpty() && !ops.peek().equals("(")) out.add(ops.pop());
                if (ops.isEmpty()) throw new IllegalArgumentException("Mismatched parentheses");
                ops.pop();
                if (!ops.isEmpty() && isFunction(ops.peek())) out.add(ops.pop());
                prev = t;
                continue;
            }
            if (isOperator(t)) {
                if ((prev == null || isOperator(prev) || prev.equals("(")) && (t.equals("+") || t.equals("-"))) {
                    out.add("0");
                }
                while (!ops.isEmpty() && isOperator(ops.peek())) {
                    String top = ops.peek();
                    if ((rightAssociative(t) && precedence(t) < precedence(top)) || (!rightAssociative(t) && precedence(t) <= precedence(top))) out.add(ops.pop()); else break;
                }
                ops.push(t);
                prev = t;
                continue;
            }
            throw new IllegalArgumentException("Unexpected token");
        }
        while (!ops.isEmpty()) {
            String o = ops.pop();
            if (o.equals("(") || o.equals(")")) throw new IllegalArgumentException("Mismatched parentheses");
            out.add(o);
        }
        return out;
    }

    private BigDecimal evalRpn(List<String> rpn, AngleMode mode) {
        Deque<BigDecimal> st = new ArrayDeque<>();
        for (String t : rpn) {
            if (isNumber(t)) { st.push(new BigDecimal(t, mc)); continue; }
            if (t.equals("π")) { st.push(new BigDecimal(Math.PI, mc)); continue; }
            if (t.equals("e")) { st.push(new BigDecimal(Math.E, mc)); continue; }
            if (isOperator(t)) {
                if (st.size() < 2) throw new IllegalArgumentException("Malformed expression");
                BigDecimal b = st.pop();
                BigDecimal a = st.pop();
                st.push(applyOperator(t, a, b));
                continue;
            }
            if (isFunction(t)) {
                if (t.equals("fact")) {
                    BigDecimal v = st.pop();
                    st.push(engine.factorial(v.intValueExact()));
                    continue;
                }
                BigDecimal v = st.pop();
                st.push(applyFunction(t, v, mode));
                continue;
            }
        }
        if (st.size() != 1) throw new IllegalArgumentException("Malformed expression");
        return st.pop();
    }

    private BigDecimal applyOperator(String op, BigDecimal a, BigDecimal b) {
        return switch (op) {
            case "+" -> engine.add(a, b);
            case "-" -> engine.subtract(a, b);
            case "*", "×" -> engine.multiply(a, b);
            case "/", "÷" -> engine.divide(a, b);
            case "%" -> engine.multiply(a, engine.percent(b));
            case "^" -> engine.pow(a, b);
            default -> throw new IllegalArgumentException("Unknown operator");
        };
    }

    private BigDecimal applyFunction(String f, BigDecimal v, AngleMode mode) {
        return switch (f) {
            case "sin" -> engine.sin(v, mode);
            case "cos" -> engine.cos(v, mode);
            case "tan" -> engine.tan(v, mode);
            case "asin" -> engine.asin(v, mode);
            case "acos" -> engine.acos(v, mode);
            case "atan" -> engine.atan(v, mode);
            case "ln" -> engine.ln(v);
            case "log" -> engine.log10(v);
            case "sqrt" -> engine.sqrt(v);
            case "inv" -> engine.reciprocal(v);
            default -> throw new IllegalArgumentException("Unknown function");
        };
    }

    private boolean isNumber(String t) {
        if (t == null || t.isEmpty()) return false;
        char c = t.charAt(0);
        if (!(Character.isDigit(c) || c == '.')) return false;
        try { new BigDecimal(t); return true; } catch (Exception e) { return false; }
    }

    private boolean isOperator(String t) { return Map.of("+",1,"-",1,"*",1,"×",1,"/",1,"÷",1,"%",1,"^",1).containsKey(t); }
    private boolean isFunction(String t) { return Map.of("sin",1,"cos",1,"tan",1,"asin",1,"acos",1,"atan",1,"ln",1,"log",1,"sqrt",1,"inv",1,"fact",1).containsKey(t); }
}


