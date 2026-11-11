package ru.netology.memorymonitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final MemoryLoadService memory;
    private final Counter calls;

    public ApiController(MemoryLoadService memory, MeterRegistry registry) {
        this.memory = memory;
        this.calls = Counter.builder("demo_controller_calls_total").description("Instrumented API calls").register(registry);
    }

    @GetMapping("/ok")
    public ResponseEntity<Map<String, String>> ok() {
        calls.increment();
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @GetMapping("/missing")
    public ResponseEntity<Map<String, String>> missing() {
        calls.increment();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Not found (demo)"));
    }

    @GetMapping("/error")
    public ResponseEntity<Map<String, String>> error() {
        calls.increment();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal error (demo)"));
    }

    @PostMapping("/memory/allocate")
    public Map<String, Long> allocate(@RequestParam(defaultValue = "64") int mb) {
        calls.increment();
        return Map.of("allocatedMegabytes", memory.allocateMegabytes(mb));
    }

    @DeleteMapping("/memory/clear")
    public Map<String, String> clear() {
        calls.increment();
        memory.clear();
        return Map.of("status", "cleared");
    }

    @GetMapping("/memory")
    public Map<String, Long> memory() {
        calls.increment();
        return Map.of("retainedMegabytes", memory.allocatedMegabytes());
    }
}