package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@SpringBootTest
public class FilmControllerTest {

    @Test
    void nameIsEmptyValidtest() throws IOException, InterruptedException {
        String testFilm = "{\n" +
                "  \"name\": \"nisi eiusmod\",\n" +
                "  \"description\": \"adipisicing\",\n" +
                "  \"releaseDate\": \"1967-03-25\",\n" +
                "  \"duration\": 100\n" +
                "}";
        URI uri =  URI.create("http://localhost:8080/films");
        HttpRequest requestToApi = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(testFilm))
                .uri(uri)
                .build();
        HttpClient client = HttpClient.newHttpClient();   // HTTP-клиент с настройками по умолчанию
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();  // получаем стандартный обработчик тела запроса с конвертацией содержимого в строку
        HttpResponse<String> response = client.send(requestToApi, handler);  // дергаем метод отправки клиента передав объект запроса и обработчик тела

    }

    @Test
    void descLen200ValidTest(){

    }

    @Test
    void releaseDateValidTest(){

    }

    @Test
    void durationValidTest(){

    }


}
