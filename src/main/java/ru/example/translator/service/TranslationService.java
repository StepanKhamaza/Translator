package ru.example.translator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.example.translator.controller.TranslationController;
import ru.example.translator.entity.RequestTranslation;
import ru.example.translator.entity.TranslatedData;
import ru.example.translator.error.ResponseError;
import ru.example.translator.repository.TranslationRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class TranslationService {
    private final Logger logger = LoggerFactory.getLogger(TranslationController.class);

    @Value("${yandex.api.key}")
    private String apiKey;
    @Value("${yandex.url}")
    private String url;

    private final TranslationRepository repository;
    private final RestTemplate restTemplate;
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduler;

    @Autowired
    public TranslationService(TranslationRepository repository) {
        this.repository = repository;
        this.restTemplate = new RestTemplate();
        this.executorService = Executors.newFixedThreadPool(10);
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public ResponseEntity<String> getTranslatedText(RequestTranslation requestTranslation,
                                                    HttpServletRequest request) {

        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"Translations\":\"%s\"}".
                            formatted(translateText(requestTranslation, request)));
        } catch (ExecutionException | InterruptedException exception) {
            logger.error(exception.getMessage());
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"Error\":\"%s\"}".
                            formatted(parseException(exception)));
        }
    }

    private String translateText(RequestTranslation requestTranslation,
                                 HttpServletRequest request) throws ExecutionException, InterruptedException {
        String text = requestTranslation.getText();
        String sourceLang = requestTranslation.getSourceLang();
        String targetLang = requestTranslation.getTargetLang();

        List<Future<String>> futures = new ArrayList<>();
        for (String word : text.split(" ")) {
            futures.add(submitTranslationWord(word, sourceLang, targetLang));
        }

        StringBuilder translatedText = new StringBuilder();
        for (Future<String> future : futures) {
            translatedText.append(future.get()).append(" ");
        }

        repository.save(new TranslatedData(getClientIp(request), text, translatedText.toString().trim()));
        return translatedText.toString().trim();
    }

    private Future<String> submitTranslationWord(String word, String sourceLang, String targetLang) {
        Callable<String> task = () -> translateWord(word, sourceLang, targetLang);
        return executorService.submit(() -> scheduler
                .schedule(task, 50L, TimeUnit.MILLISECONDS)
                .get());
    }

    private String translateWord(String word, String sourceLang, String targetLang) throws HttpClientErrorException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Api-Key " + apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("sourceLanguageCode", sourceLang);
        requestBody.put("targetLanguageCode", targetLang);
        requestBody.put("format", "PLAIN_TEXT");
        requestBody.put("texts", word);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            return parseJsonResponse(response.getBody());
        } catch (HttpClientErrorException e) {
            throw new HttpClientErrorException(e.getStatusCode(), e.getResponseBodyAsString());
        }
    }

    private String parseJsonResponse(String jsonResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        StringBuilder result = new StringBuilder();

        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode translationsNode = rootNode.path("translations");
            if (translationsNode.isArray()) {
                for (JsonNode translationNode : translationsNode) {
                    String translatedText = translationNode.path("text").asText();
                    result.append(translatedText).append(" ");
                }
            }
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
        }

        return result.toString().trim();
    }

    private String getClientIp(HttpServletRequest request) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || remoteAddr.isEmpty()) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return remoteAddr;
    }

    private String parseException(Exception exception) {
        if (exception.getMessage().contains(ResponseError.LENGTH_ERROR.getTitle()))
            return "Total texts length must be not greater than 10000";

        if (exception.getMessage().contains(ResponseError.SOURCE_LANGUAGE_ERROR.getTitle()))
            return "Source message language not found";

        if (exception.getMessage().contains(ResponseError.TARGET_LANGUAGE_ERROR.getTitle()))
            return "Target message language not found";

        if (exception.getMessage().contains(ResponseError.EMPTY_TEXT_ERROR.getTitle()))
            return "Text cannot be empty";

        if (exception.getMessage().contains(ResponseError.LIMIT_ON_REQUESTS_ERROR.getTitle()))
            return "Limit on requests was exceeded. Limit: 20, Interval: 1s";

        logger.error("Exception parsing error", exception);
        return "Server error";
    }
}
