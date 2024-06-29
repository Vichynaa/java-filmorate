package test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.FilmorateApplication;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    private final HttpClient client  = HttpClient.newBuilder().build();

    @BeforeEach
    void beforeEach() {
        FilmorateApplication.start();
    }

    @AfterEach
    void afterEach() {
        FilmorateApplication.stop();
    }

    @Test
    void checkValidationFilms() throws Throwable {
        String bodyEmpty = "{}";
        String bodyIncorrectReleaseDate = "{\n" +
                "  \"name\": \"nisi eiusmod\",\n" +
                "  \"description\": \"adipisicing\",\n" +
                "  \"releaseDate\": \"1667-03-25\",\n" +
                "  \"duration\": 100\n" +
                "}";
        String bodyIncorrectDuration = "{\n" +
                "  \"name\": \"nisi eiusmod\",\n" +
                "  \"description\": \"adipisicing\",\n" +
                "  \"releaseDate\": \"1967-03-25\",\n" +
                "  \"duration\": -100\n" +
                "}";
        String bodyIncorrectDescription = "{\n" +
                "  \"name\": \"nisi eiusmod\",\n" +
                "  \"description\": \"adipisasdasdasdddddddddddddddddddddddddddddddddddddddddddddddasdasdasdicingadi" +
                "pisasdasdasdddddddddddddddddddddddddddddddddddddddddddddddasdasdasdicingadipisasdasdasddddddddddddd" +
                "dddddddddddddddddddddddd\",\n" +
                "  \"releaseDate\": \"1967-03-25\",\n" +
                "  \"duration\": 100\n" +
                "}";
        String bodyIncorrectName = "{\n" +
                "  \"name\": \" \",\n" +
                "  \"description\": \"adipisicing\",\n" +
                "  \"releaseDate\": \"1967-03-25\",\n" +
                "  \"duration\": 100\n" +
                "}";

        URI url = URI.create("http://localhost:8080/films");

        HttpRequest requestEmpty = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(bodyEmpty))
                .build();
        HttpRequest requestReleaseDate = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(bodyIncorrectReleaseDate))
                .build();
        HttpRequest requestDuration = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(bodyIncorrectDuration))
                .build();
        HttpRequest requestDescription = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(bodyIncorrectDescription))
                .build();
        HttpRequest requestName = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(bodyIncorrectName))
                .build();

        HttpResponse<String> responseEmpty = client.send(requestEmpty, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseReleaseDate = client.send(requestReleaseDate, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseDuration = client.send(requestDuration, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseDescription = client.send(
                requestDescription, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseName = client.send(requestName, HttpResponse.BodyHandlers.ofString());

        assertTrue(responseEmpty.statusCode() == 500,
                "Ошибка валидации при отправлении пустого запроса");
        assertTrue(responseReleaseDate.statusCode() == 400,
                "Ошибка валидации при указании неправильной даты выпуска");
        assertTrue(responseDuration.statusCode() == 400,
                "Ошибка валидации при указании отрицательной продолжительности фильма");
        assertTrue(responseDescription.statusCode() == 400,
                "Ошибка валидации при привышении органичения в description");
        assertTrue(responseName.statusCode() == 500,
                "Ошибка валидации при указании пустого имени");
    }

}
