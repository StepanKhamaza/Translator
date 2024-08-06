package ru.example.translator.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.example.translator.entity.RequestTranslation;
import ru.example.translator.service.TranslationService;


@RestController
public class TranslationController {
    private final Logger logger = LoggerFactory.getLogger(TranslationController.class);
    private final TranslationService translationService;

    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    @PostMapping("/api/translate")
    public ResponseEntity<String> translate(@Valid @RequestBody RequestTranslation requestTranslation,
                                            HttpServletRequest request) {

        return translationService.getTranslatedText(requestTranslation, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.error(e.getMessage());
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"Error\":\"%s\"}".
                        formatted("Fields text, sourceLang, targetLang cannot be empty"));
    }
}
