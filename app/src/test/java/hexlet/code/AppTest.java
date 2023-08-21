package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.model.query.QUrl;
import io.ebean.DB;
import io.ebean.Database;
import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Nested;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoField;

import static org.assertj.core.api.Assertions.assertThat;

public final class AppTest {

    @Test
    void testInit() {
        assertThat(true).isEqualTo(true);
    }

    private static Javalin app;
    private static String baseUrl;
    private static Url existingUrl;
    private static Database database;
    private static MockWebServer server;

    @BeforeAll
    public static void beforeAll() {
        app = App.getApp();
        app.start();
        int port = app.port();
        baseUrl = "http://localhost:" + port;
        database = DB.getDefault();

        server = new MockWebServer();

        MockResponse response = new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Cache-Control", "no-cache")
                .setBody("{test: true}");

        server.enqueue(response);
    }

    @AfterAll
    public static void afterAll() throws IOException {
        app.stop();
        server.close();
    }

    @BeforeEach
    void beforeEach() {
        database.script().run("/truncate.sql");
        database.script().run("/seed-test-db.sql");
    }

    @Nested
    class RootTest {

        @Test
        void testIndex() {
            HttpResponse<String> response = Unirest.get(baseUrl).asString();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getBody()).contains("A. Kozlov");
        }
    }


    @Nested
    class UrlTest {

        @Test
        void testIndex() {
            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls")
                    .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(body).contains("https://javalin.io");
            assertThat(body).contains("01/01/2023 14:57");
            assertThat(body).contains("https://commons.apache.org");
            assertThat(body).contains("01/01/2022 13:57");
        }

        @Test
        void testShow() {
            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls/1")
                    .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(body).contains("https://javalin.io");
            assertThat(body).contains("01/01/2023 14:57");
        }

        @Test
        void testNew() {
            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls/new")
                    .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(200);
        }

        @Test
        void testCreate() {
            String inputUrlName = "https://vk.com";
            HttpResponse responsePost = Unirest
                    .post(baseUrl + "/urls")
                    .field("name", inputUrlName)
                    .asEmpty();

            assertThat(responsePost.getStatus()).isEqualTo(302);
            assertThat(responsePost.getHeaders().getFirst("Location")).isEqualTo("/urls");

            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls")
                    .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(body).contains(inputUrlName);
//            assertThat(body).contains("Страница успешно добавлена");

            Url actualUrl = new QUrl()
                    .name.equalTo(inputUrlName)
                    .findOne();

            assertThat(actualUrl).isNotNull();
            assertThat(actualUrl.getName()).isEqualTo(inputUrlName);
        }

        @Test
        void testUrlCheck() {
            String inputUrlName = "https://vk.com";
            HttpResponse responsePost = Unirest
                    .post(baseUrl + "/urls")
                    .field("name", inputUrlName)
                    .asEmpty();

            Url actualUrl = new QUrl()
                    .name.equalTo(inputUrlName)
                    .findOne();

            HttpResponse responsePost2 = Unirest
                    .get(baseUrl + "/urls/3/checks")
                    .asString();

            assertThat(actualUrl).isNotNull();
            assertThat(responsePost2.getStatus()).isEqualTo(200);

            HttpUrl testUrl = server.url("/v1/chat/");

            HttpResponse responsePost3 = Unirest
                    .get(testUrl.url().toString())
                    .asString();

            assertThat(responsePost3.getBody()).isEqualTo("{test: true}");

        }
    }
}
