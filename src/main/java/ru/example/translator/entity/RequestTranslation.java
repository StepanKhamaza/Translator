package ru.example.translator.entity;

import jakarta.validation.constraints.NotNull;

public class RequestTranslation {
    @NotNull
    private String text;
    @NotNull
    private String sourceLang;
    @NotNull
    private String targetLang;

    public RequestTranslation(String text, String sourceLang, String targetLang) {
        this.text = text;
        this.sourceLang = sourceLang;
        this.targetLang = targetLang;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSourceLang() {
        return sourceLang;
    }

    public void setSourceLang(String sourceLang) {
        this.sourceLang = sourceLang;
    }

    public String getTargetLang() {
        return targetLang;
    }

    public void setTargetLang(String targetLang) {
        this.targetLang = targetLang;
    }
}
