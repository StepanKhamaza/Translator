package ru.example.translator.entity;

public class TranslatedData {
    private long id;
    private String ip;
    private String text;
    private String translatedText;

    public TranslatedData() {
    }

    public TranslatedData(String ip, String text, String translatedText) {
        this.ip = ip;
        this.text = text;
        this.translatedText = translatedText;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }
}
