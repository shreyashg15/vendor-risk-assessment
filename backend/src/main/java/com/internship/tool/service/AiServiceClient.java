package com.internship.tool.service;

import com.internship.tool.entity.Vendor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class AiServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(AiServiceClient.class);

    @Value("${ai.service.url:http://localhost:5000}")
    private String aiServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Async
    public CompletableFuture<String> getVendorDescriptionAsync(Vendor vendor) {
        try {
            String url = aiServiceUrl + "/api/ai/describe";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("vendor", vendor);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestMap, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            if (response.getBody() != null && response.getBody().containsKey("data")) {
                return CompletableFuture.completedFuture(String.valueOf(response.getBody().get("data")));
            }
        } catch (Exception e) {
            logger.error("Failed to fetch AI description for vendor: {}", vendor.getId(), e);
        }
        return CompletableFuture.completedFuture(null);
    }
}
