package com.onedao.service;

import java.net.InetAddress;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.onedao.exception.CountryNotAllowedException;

@Service
public class CountryService {

    private static final Logger log = LoggerFactory.getLogger(CountryService.class);

    // Syria, Afghanistan, Iran. Add more ISO codes here.
    private static final Set<String> BLOCKED_COUNTRIES = Set.of("SY", "AF", "IR");

    private final RestClient restClient = RestClient.create();

    public void validate(String clientIp) {
        String countryCode = lookupCountryCode(clientIp);
        log.info("Sign-up country code: {}", countryCode);

        if (countryCode != null && BLOCKED_COUNTRIES.contains(countryCode)) {
            throw new CountryNotAllowedException("Sign-up is not allowed from your country");
        }
    }

    private String lookupCountryCode(String ip) {
        try {
            // On localhost the IP is private, so the API uses your public IP instead
            String url = "https://ipwho.is/" + (isLocal(ip) ? "" : ip);
            Map<?, ?> body = restClient.get().uri(url).retrieve().body(Map.class);

            if (body != null && Boolean.TRUE.equals(body.get("success"))) {
                return (String) body.get("country_code");
            }
        } catch (Exception e) {
            log.warn("Country lookup failed: {}", e.getMessage()); // if the API is down, allow sign-up
        }
        return null;
    }

    private boolean isLocal(String ip) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            return address.isLoopbackAddress() || address.isSiteLocalAddress() || address.isAnyLocalAddress();
        } catch (Exception e) {
            return true;
        }
    }
}