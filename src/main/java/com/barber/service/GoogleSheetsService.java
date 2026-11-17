package com.barber.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;

public class GoogleSheetsService {
    private final String webhookUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GoogleSheetsService() {
        this.webhookUrl = System.getenv("GOOGLE_SHEET_URL");
        if (this.webhookUrl == null || this.webhookUrl.isBlank()) {
            System.err.println("WARNING: GOOGLE_SHEET_URL is not configured!");
        }
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
        this.objectMapper = new ObjectMapper();
    }
}