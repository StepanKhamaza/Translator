# Translator

Автор: Хамаза Степан

## Требования для запуска
* Java >= 17
* Apache Maven >= 3.9.8
* PostgreSQL 16

## Как запустить проект

1. Копируем проект на локальную машину: `git clone https://github.com/StepanKhamaza/Translator.git`
2. Исправляем поля (URL, USERNAME, PASSWORD) в файле `src/main/java/ru/example/translator/repository/TranslationRepository.java` на нужные нам. Поля отвечают за подключение к базе данных PostgreSQL.
3. Для работы с Яндекс Переводчиком, нужно получить ключ к api. Инструкция `https://yandex.cloud/ru/docs/translate/api-ref/authentication`.
4. Также, исправляем поля для базы данных в файле `src/main/resources/application.properties`. В нем же, меняем yandex.api.key на ключ к api Яндекс Переводчика.
5. Для запуска нам нужен apache maven. Открываем каталог с проектом в консоли и прописываем команду: `mvn spring-bot:run`.

## Как работать с переводчиком

Для отправки запроса на перевод есть два способа:
1. Проект запускается на порту 8080, в браузере переходим по адресу `http://localhost:8080/` и вводим все данные на странице. Текст для перевода, текущий язык и целевой язык.
   ![image](https://github.com/user-attachments/assets/cca81b01-faca-4622-9101-f47a7489fb17)

2. Отправляем POST запрос по адресу `http://localhost:8080/api/translate` c помощью Postman или других аналогов. Запрос отправляется в виде JSON объекта, который содержит: text - текст для перевода, sourceLang - текущий язык, targetLang - целевой язык.
   ![image](https://github.com/user-attachments/assets/a816a264-3aa5-4d0d-ad54-80a9be175e5c)

Возможна задержка при переводе, т.к. api Яндекс Переводчика позволяет делать не больше 20 запросов в секунду.
