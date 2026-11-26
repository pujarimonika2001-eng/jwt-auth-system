package com.onedao.security;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class TokenBlacklist {

    // In-memory set of tokens that were logged out
    private final Set<String> blacklisted = ConcurrentHashMap.newKeySet();

    public void add(String token) {
        blacklisted.add(token);
    }

    public boolean isBlacklisted(String token) {
        return blacklisted.contains(token);
    }
}