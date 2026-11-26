package com.onedao.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.onedao.exception.OtpException;

@Service
public class OtpService {

    private static final Logger log = LoggerFactory.getLogger(OtpService.class);
    private static final int VALID_MINUTES = 5;

    private record OtpEntry(String otp, LocalDateTime expiry) {}

    // Users who still have to verify: key = username
    private final Map<String, OtpEntry> pending = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    public String generate(String username) {
        String otp = String.format("%06d", random.nextInt(1_000_000));
        pending.put(username, new OtpEntry(otp, LocalDateTime.now().plusMinutes(VALID_MINUTES)));
        log.info("OTP for {}: {}", username, otp); // no email/SMS service, so it is logged
        return otp;
    }

    public void verify(String username, String otp) {
        OtpEntry entry = pending.get(username);
        if (entry == null
                || entry.expiry().isBefore(LocalDateTime.now())
                || !entry.otp().equals(otp)) {
            throw new OtpException("Invalid or expired OTP");
        }
        pending.remove(username);
    }

    public boolean isPending(String username) {
        return pending.containsKey(username);
    }
}