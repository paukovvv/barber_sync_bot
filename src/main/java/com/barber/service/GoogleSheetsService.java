package com.barber.service;

import com.barber.model.Client;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

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

    public void saveClient(Client client) throws Exception {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            throw new IllegalStateException("GOOGLE_SHEET_URL is not set");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String createdAt = LocalDateTime.now().format(formatter);
        String appointmentTime = client.getAppointmentTime() != null
                ? client.getAppointmentTime().format(formatter)
                : "";

        Map<String, Object> payload = new HashMap<>();
        payload.put("createdAt", createdAt);
        payload.put("name", client.getName());
        payload.put("phone", client.getPhone());
        payload.put("email", client.getEmail());
        payload.put("appointmentTime", appointmentTime);
        payload.put("price", "");
        payload.put("notes", "");

        String jsonPayload = objectMapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(webhookUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Google Sheets API error (" + response.statusCode() + "): " + response.body());
        }
    }
}