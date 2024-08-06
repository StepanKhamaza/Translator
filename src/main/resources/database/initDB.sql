CREATE TABLE IF NOT EXISTS
    translations(id SERIAL PRIMARY KEY,
                 ip VARCHAR(30),
                 text VARCHAR(10000),
                 translatedText VARCHAR(10000));
