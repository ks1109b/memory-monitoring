package ru.netology.memorymonitoring;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MemoryLoadService {

    private final List<byte[]> allocations = new ArrayList<>();

    public MemoryLoadService(MeterRegistry registry) {
        Gauge.builder(
                        "demo_process_resident_memory_bytes",
                        this,
                        MemoryLoadService::residentMemoryBytes
                )
                .description("Process RSS memory in bytes")
                .baseUnit("bytes")
                .register(registry);
    }

    public synchronized long allocateMegabytes(int mb) {
        if (mb < 1 || mb > 256) {
            throw new IllegalArgumentException("mb must be 1..256");
        }

        for (int i = 0; i < mb; i++) {
            byte[] block = new byte[1024 * 1024];

            // Принудительно затрагиваем страницы памяти
            for (int p = 0; p < block.length; p += 4096) {
                block[p] = 1;
            }

            allocations.add(block);
        }

        return allocatedMegabytes();
    }

    public synchronized void clear() {
        allocations.clear();
    }

    public synchronized long allocatedMegabytes() {
        return allocations.stream()
                .mapToLong(a -> a.length)
                .sum() / 1024 / 1024;
    }

    private double residentMemoryBytes() {
        try {
            String status = Files.readString(
                    Path.of("/proc/self/status")
            );

            Pattern pattern = Pattern.compile(
                    "^VmRSS:\\s+(\\d+)\\s+kB$",
                    Pattern.MULTILINE
            );

            Matcher matcher = pattern.matcher(status);

            if (matcher.find()) {
                return Long.parseLong(matcher.group(1)) * 1024.0;
            }

        } catch (IOException | NumberFormatException ignored) {
            // Если метрика недоступна, возвращаем NaN
        }

        return Double.NaN;
    }
}
