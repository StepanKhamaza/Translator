package ru.example.translator.error;

public enum ResponseError {
    LENGTH_ERROR("total texts length must be not greater than 10000"),
    SOURCE_LANGUAGE_ERROR("unsupported source_language_code"),
    TARGET_LANGUAGE_ERROR("unsupported target_language_code"),
    EMPTY_TEXT_ERROR("texts are empty"),
    LIMIT_ON_REQUESTS_ERROR("limit on requests was exceeded");

    private final String title;

    ResponseError(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
