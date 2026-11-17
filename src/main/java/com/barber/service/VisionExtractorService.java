package com.barber.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;

public class VisionExtractorService {
    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";
    private final String apiKey;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public VisionExtractorService() {
        this.apiKey = System.getenv("GEMINI_API_KEY");
        if (this.apiKey == null || this.apiKey.isBlank()) {
            System.err.println("WARNING: GEMINI_API_KEY is not configured!");
        }
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();

    }


}
