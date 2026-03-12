package com.aquasmart.irrigationservice.service.ai;

import com.aquasmart.irrigationservice.dto.IrrigationRecommendation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiRecommendationClient {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${ai.gemini.enabled:false}")
    private boolean enabled;

    @Value("${ai.gemini.api-key:}")
    private String apiKey;

    @Value("${ai.gemini.model:gemini-1.5-flash}")
    private String model;

    @Value("${ai.gemini.timeout-seconds:8}")
    private long timeoutSeconds;

    public IrrigationRecommendation applyOn(IrrigationRecommendation baseRecommendation) {
        if (!enabled || !StringUtils.hasText(apiKey)) {
            return baseRecommendation;
        }

        try {
            String responseText = callGemini(buildPrompt(baseRecommendation));
            if (!StringUtils.hasText(responseText)) {
                return withAiWarning(baseRecommendation, "AI response is empty - using local recommendation");
            }

            Map<String, Object> aiDecision = parseJsonFromResponse(responseText);
            return merge(baseRecommendation, aiDecision);
        } catch (Exception ex) {
            log.warn("Gemini recommendation unavailable: {}", ex.getMessage());
            return withAiWarning(baseRecommendation, "AI unavailable - using local recommendation");
        }
    }

    private String callGemini(String prompt) {
        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))));
        requestBody.put("generationConfig", Map.of("temperature", 0.2, "responseMimeType", "application/json"));

        Map<String, Object> response = webClientBuilder
                .baseUrl("https://generativelanguage.googleapis.com")
                .build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/{model}:generateContent")
                        .queryParam("key", apiKey)
                        .build(model))
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .block();

        return extractResponseText(response);
    }

    private String extractResponseText(Map<String, Object> response) {
        if (response == null) {
            return null;
        }

        Object candidatesObj = response.get("candidates");
        if (!(candidatesObj instanceof List<?> candidates) || candidates.isEmpty()) {
            return null;
        }

        Object firstCandidate = candidates.get(0);
        if (!(firstCandidate instanceof Map<?, ?> candidateMap)) {
            return null;
        }

        Object contentObj = candidateMap.get("content");
        if (!(contentObj instanceof Map<?, ?> contentMap)) {
            return null;
        }

        Object partsObj = contentMap.get("parts");
        if (!(partsObj instanceof List<?> parts) || parts.isEmpty()) {
            return null;
        }

        Object firstPart = parts.get(0);
        if (!(firstPart instanceof Map<?, ?> partMap)) {
            return null;
        }

        Object textObj = partMap.get("text");
        return textObj != null ? textObj.toString() : null;
    }

    private Map<String, Object> parseJsonFromResponse(String responseText) throws Exception {
        String json = responseText.trim();
        if (json.startsWith("```") && json.endsWith("```")) {
            json = json.replace("```json", "").replace("```", "").trim();
        }
        return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
    }

    private IrrigationRecommendation merge(IrrigationRecommendation base, Map<String, Object> aiDecision) {
        Boolean shouldIrrigate = getBoolean(aiDecision, "shouldIrrigate", base.getShouldIrrigate());
        Integer duration = getInteger(aiDecision, "recommendedDurationMinutes", base.getRecommendedDurationMinutes());
        Double waterAmount = getDouble(aiDecision, "recommendedWaterAmount", base.getRecommendedWaterAmount());
        Integer confidence = getInteger(aiDecision, "confidenceScore", base.getConfidenceScore());
        LocalDateTime optimalStart = getDateTime(aiDecision, "optimalStartTime", base.getOptimalStartTime());

        List<String> reasons = mergeTextList(base.getReasons(), aiDecision.get("reasons"));
        List<String> warnings = mergeTextList(base.getWarnings(), aiDecision.get("warnings"));
        reasons.add("AI validated with model: " + model);

        return IrrigationRecommendation.builder()
                .parcelId(base.getParcelId())
                .farmId(base.getFarmId())
                .shouldIrrigate(shouldIrrigate)
                .recommendedDurationMinutes(duration)
                .recommendedWaterAmount(waterAmount)
                .optimalStartTime(optimalStart)
                .confidenceScore(Math.max(0, Math.min(100, confidence)))
                .conditions(base.getConditions())
                .reasons(reasons)
                .warnings(warnings)
                .build();
    }

    private IrrigationRecommendation withAiWarning(IrrigationRecommendation base, String message) {
        List<String> warnings = new ArrayList<>();
        if (base.getWarnings() != null) {
            warnings.addAll(base.getWarnings());
        }
        warnings.add(message);

        return IrrigationRecommendation.builder()
                .parcelId(base.getParcelId())
                .farmId(base.getFarmId())
                .shouldIrrigate(base.getShouldIrrigate())
                .recommendedDurationMinutes(base.getRecommendedDurationMinutes())
                .recommendedWaterAmount(base.getRecommendedWaterAmount())
                .optimalStartTime(base.getOptimalStartTime())
                .confidenceScore(base.getConfidenceScore())
                .conditions(base.getConditions())
                .reasons(base.getReasons())
                .warnings(warnings)
                .build();
    }

    private List<String> mergeTextList(List<String> baseList, Object aiListObject) {
        List<String> merged = new ArrayList<>();
        if (baseList != null) {
            merged.addAll(baseList);
        }

        if (aiListObject instanceof List<?> aiList) {
            for (Object item : aiList) {
                if (item != null && StringUtils.hasText(item.toString())) {
                    merged.add(item.toString());
                }
            }
        }
        return merged;
    }

    private Boolean getBoolean(Map<String, Object> map, String key, Boolean defaultValue) {
        Object value = map.get(key);
        if (value instanceof Boolean boolValue) {
            return boolValue;
        }
        if (value instanceof String stringValue) {
            return Boolean.parseBoolean(stringValue);
        }
        return defaultValue;
    }

    private Integer getInteger(Map<String, Object> map, String key, Integer defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number numberValue) {
            return numberValue.intValue();
        }
        if (value instanceof String stringValue) {
            try {
                return Integer.parseInt(stringValue);
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private Double getDouble(Map<String, Object> map, String key, Double defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number numberValue) {
            return numberValue.doubleValue();
        }
        if (value instanceof String stringValue) {
            try {
                return Double.parseDouble(stringValue);
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private LocalDateTime getDateTime(Map<String, Object> map, String key, LocalDateTime defaultValue) {
        Object value = map.get(key);
        if (value instanceof String stringValue && StringUtils.hasText(stringValue)) {
            try {
                return LocalDateTime.parse(stringValue);
            } catch (Exception ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private String buildPrompt(IrrigationRecommendation recommendation) {
        String conditions = recommendation.getConditions() == null ? "{}" : String.format(
                "{soilMoisture: %s, temperature: %s, humidity: %s, windSpeed: %s, rainProbability: %s, expectedRainfall: %s, weatherDescription: '%s'}",
                recommendation.getConditions().getSoilMoisture(),
                recommendation.getConditions().getTemperature(),
                recommendation.getConditions().getHumidity(),
                recommendation.getConditions().getWindSpeed(),
                recommendation.getConditions().getRainProbability(),
                recommendation.getConditions().getExpectedRainfall(),
                recommendation.getConditions().getWeatherDescription()
        );

        return "You are an irrigation agronomy assistant. Analyze the recommendation and return ONLY valid JSON with these exact keys: " +
                "shouldIrrigate (boolean), recommendedDurationMinutes (integer), recommendedWaterAmount (number), " +
                "optimalStartTime (ISO-8601 LocalDateTime), confidenceScore (integer 0-100), reasons (array of strings), warnings (array of strings). " +
                "Do not include markdown.\n" +
                "ParcelId=" + recommendation.getParcelId() + ", FarmId=" + recommendation.getFarmId() + ", Conditions=" + conditions + ", " +
                "BaseRecommendation={shouldIrrigate:" + recommendation.getShouldIrrigate() +
                ", recommendedDurationMinutes:" + recommendation.getRecommendedDurationMinutes() +
                ", recommendedWaterAmount:" + recommendation.getRecommendedWaterAmount() +
                ", optimalStartTime:'" + recommendation.getOptimalStartTime() + "', confidenceScore:" + recommendation.getConfidenceScore() +
                ", reasons:" + recommendation.getReasons() + ", warnings:" + recommendation.getWarnings() + "}.";
    }
}