package com.ivanjiang.druginteraction.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final Map<String, Bucket> unauthenticatedBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> authenticatedBuckets = new ConcurrentHashMap<>();

    private static final int UNAUTHENTICATED_LIMIT = 100;
    private static final int AUTHENTICATED_LIMIT = 1000;

    public Bucket resolveUnauthenticatedBucket(String ipAddress) {
        return unauthenticatedBuckets.computeIfAbsent(ipAddress, this::newUnauthenticatedBucket);
    }

    public Bucket resolveAuthenticatedBucket(String username) {
        return authenticatedBuckets.computeIfAbsent(username, this::newAuthenticatedBucket);
    }

    private Bucket newUnauthenticatedBucket(String key) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(UNAUTHENTICATED_LIMIT)
                .refillGreedy(UNAUTHENTICATED_LIMIT, Duration.ofMinutes(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket newAuthenticatedBucket(String key) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(AUTHENTICATED_LIMIT)
                .refillGreedy(AUTHENTICATED_LIMIT, Duration.ofMinutes(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    public boolean tryConsume(String ipAddress, String username) {
        if (username != null && !username.isEmpty()) {
            return resolveAuthenticatedBucket(username).tryConsume(1);
        }
        return resolveUnauthenticatedBucket(ipAddress).tryConsume(1);
    }
}
