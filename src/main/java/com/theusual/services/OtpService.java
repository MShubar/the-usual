package com.theusual.services;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private static class Entry {
        final String code;
        final long expiresAt;
        Entry(String code, long expiresAt) {
            this.code = code;
            this.expiresAt = expiresAt;
        }
    }

    private final Map<String, Entry> store = new ConcurrentHashMap<>();
    private final Random rnd = new Random();

    public void sendOtp(String phone) {
        String code = String.format("%06d", rnd.nextInt(1_000_000));
        long expires = Instant.now().plusSeconds(300).toEpochMilli(); // 5 minutes
        store.put(phone, new Entry(code, expires));
        System.out.println("[OTP] Phone: " + phone + " Code: " + code + " (valid 5m)");
        // TODO: integrate SMS provider here.
    }

    public boolean verifyOtp(String phone, String code) {
        Entry e = store.get(phone);
        if (e == null) return false;
        long now = Instant.now().toEpochMilli();
        if (now > e.expiresAt) {
            store.remove(phone);
            return false;
        }
        boolean ok = e.code.equals(code);
        if (ok) store.remove(phone);
        return ok;
    }
}
