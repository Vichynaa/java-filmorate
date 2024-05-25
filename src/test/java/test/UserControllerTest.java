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

public class UserControllerTest {
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
    void checkValidationUsers() throws Throwable {
        String bodyEmpty = "{}";
        String bodyIncorrectEmail = "{\n" +
                "  \"login\": \"dolore\",\n" +
                "  \"name\": \"Nick Name\",\n" +
                "  \"email\": \"mail.ru\",\n" +
                "  \"birthday\": \"1946-08-20\"\n" +
                "}";
        String bodyIncorrectLogin = "{\n" +
                "  \"login\": \" \",\n" +
                "  \"name\": \"Nick Name\",\n" +
                "  \"email\": \"mail@mail.ru\",\n" +
                "  \"birthday\": \"1946-08-20\"\n" +
                "}";
        String bodyIncorrectBirthday = "{\n" +
                "  \"login\": \"NickJack\",\n" +
                "  \"name\": \"Nick Name\",\n" +
                "  \"email\": \"mail@mail.ru\",\n" +
                "  \"birthday\": \"2646-08-20\"\n" +
                "}";

        URI url = URI.create("http://localhost:8080/users");

        HttpRequest requestEmpty = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(bodyEmpty))
                .build();
        HttpRequest requestEmail = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(bodyIncorrectEmail))
                .build();
        HttpRequest requestLogin = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(bodyIncorrectLogin))
                .build();
        HttpRequest requestBirthday = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(bodyIncorrectBirthday))
                .build();


        HttpResponse<String> responseEmpty = client.send(requestEmpty, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseEmail = client.send(requestEmail, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseLogin = client.send(requestLogin, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseBirthday = client.send(requestBirthday, HttpResponse.BodyHandlers.ofString());

        assertTrue(responseEmpty.statusCode() == 500,
                "Ошибка валидации при отправлении пустого запроса");
        assertTrue(responseEmail.statusCode() == 400,
                "Ошибка валидации email");
        assertTrue(responseLogin.statusCode() == 500,
                "Ошибка валидации login");
        assertTrue(responseBirthday.statusCode() == 500,
                "Ошибка валидации birthday");
    }

}
