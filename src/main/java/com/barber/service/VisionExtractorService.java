package com.barber.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.Map;
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
    private String sendImageToGemini(File imageFile) throws Exception {
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        String prompt = """
                Extract client appointment details from this barbershop booking screenshot.
                Return ONLY a valid JSON object with exact keys:
                {
                  "name": "Client full name or null",
                  "phone": "Phone number or null",
                  "email": "Email address or null",
                  "appointmentTime": "ISO-8601 date-time string like 2026-11-17T14:30:00 or null"
                }
                If year is missing on the screenshot, assume 2026.
                """;

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt),
                                Map.of("inline_data", Map.of(
                                        "mime_type", "image/jpeg",
                                        "data", base64Image
                                ))
                        ))
                ),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json"
                )
        );

        String jsonPayload = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GEMINI_URL + apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Gemini API error (" + response.statusCode() + "): " + response.body());
        }

        return response.body();
    }


}
