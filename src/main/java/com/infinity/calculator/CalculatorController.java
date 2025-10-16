package com.infinity.calculator;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/calc")
@CrossOrigin(origins = "*")
public class CalculatorController {
    private final CalculatorService service = new CalculatorService();
    private final RateLimiter limiter = new RateLimiter(30, 10000);

    @PostMapping("/eval")
    public ResponseEntity<?> eval(@RequestBody EvalRequest req, @RequestHeader(value = "X-Forwarded-For", required = false) String xff, @RequestHeader(value = "X-Real-IP", required = false) String xri) {
        String ip = xri != null ? xri : (xff != null ? xff.split(",")[0] : "unknown");
        if (!limiter.allow(ip)) throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit");
        AngleMode mode = req.angleMode != null && req.angleMode.equalsIgnoreCase("DEG") ? AngleMode.DEG : AngleMode.RAD;
        CalculatorService.Result r = service.evaluate(req.expression == null ? "" : req.expression, mode);
        return ResponseEntity.ok(r);
    }

    @PostMapping("/memory")
    public ResponseEntity<?> memory(@RequestBody MemoryRequest req, @RequestHeader(value = "X-Forwarded-For", required = false) String xff, @RequestHeader(value = "X-Real-IP", required = false) String xri) {
        String ip = xri != null ? xri : (xff != null ? xff.split(",")[0] : "unknown");
        if (!limiter.allow(ip)) throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit");
        BigDecimal v = req.value != null ? new BigDecimal(req.value) : null;
        return ResponseEntity.ok(service.memoryOp(req.op, v));
    }

    public static class EvalRequest { public String expression; public String angleMode; }
    public static class MemoryRequest { public String op; public String value; }
}


