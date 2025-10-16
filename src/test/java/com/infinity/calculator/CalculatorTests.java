package com.infinity.calculator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;

public class CalculatorTests {
    @Test
    void testParserBasic() {
        CalculatorService s = new CalculatorService();
        assertEquals("3.5", s.evaluate("3+4×2/(1-5)^2", AngleMode.DEG).result);
    }
    @Test
    void testTrigDeg() {
        CalculatorService s = new CalculatorService();
        String res = s.evaluate("sin(30)", AngleMode.DEG).result;
        assertTrue(res.startsWith("0.5"));
    }
    @Test
    void testTrigRad() {
        CalculatorService s = new CalculatorService();
        String res = s.evaluate("sin(3.1415926535/6)", AngleMode.RAD).result;
        assertTrue(res.startsWith("0.5"));
    }
    @Test
    void testLnE() {
        CalculatorService s = new CalculatorService();
        String res = s.evaluate("ln(e)", AngleMode.DEG).result;
        assertTrue(new BigDecimal(res.replace(",","")) .subtract(BigDecimal.ONE).abs().doubleValue() < 1e-9);
    }
    @Test
    void testLog10() {
        CalculatorService s = new CalculatorService();
        assertEquals("3", s.evaluate("log(1000)", AngleMode.DEG).result);
    }
    @Test
    void testFactorial() {
        CalculatorService s = new CalculatorService();
        assertEquals("120", s.evaluate("fact(5)", AngleMode.DEG).result);
    }
    @Test
    void testPower() {
        CalculatorService s = new CalculatorService();
        assertEquals("1,024", s.evaluate("2^10", AngleMode.DEG).result);
    }
    @Test
    void testMemory() {
        CalculatorService s = new CalculatorService();
        s.memoryOp("MC", null);
        s.memoryOp("M+", new BigDecimal("5"));
        s.memoryOp("M+", new BigDecimal("7"));
        assertEquals(new BigDecimal("12"), s.memoryOp("MR", null).memory);
        s.memoryOp("M-", new BigDecimal("2"));
        assertEquals(new BigDecimal("10"), s.memoryOp("MR", null).memory);
    }
}


