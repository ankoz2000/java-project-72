import hexlet.code.App;
import hexlet.code.model.Url;
import hexlet.code.model.query.QUrl;
import io.ebean.DB;
import io.ebean.Database;
import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Nested;

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

    @BeforeAll
    public static void beforeAll() {
        app = App.getApp();
        app.start();
        int port = app.port();
        baseUrl = "http://localhost:" + port;
        database = DB.getDefault();
    }

    @AfterAll
    public static void afterAll() {
        app.stop();
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
            assertThat(body).contains("Страница успешно добавлена");

            Url actualUrl = new QUrl()
                    .name.equalTo(inputUrlName)
                    .findOne();

            assertThat(actualUrl).isNotNull();
            assertThat(actualUrl.getName()).isEqualTo(inputUrlName);

            Instant today = Instant.now();
            Instant createdAt = actualUrl.getCreatedAt();

            assertThat(createdAt.get(ChronoField.YEAR))
                    .isEqualTo(today.get(ChronoField.YEAR));
            assertThat(createdAt.get(ChronoField.MONTH_OF_YEAR))
                    .isEqualTo(today.get(ChronoField.MONTH_OF_YEAR));
            assertThat(createdAt.get(ChronoField.DAY_OF_MONTH))
                    .isEqualTo(today.get(ChronoField.DAY_OF_MONTH));
            assertThat(createdAt.get(ChronoField.HOUR_OF_DAY))
                    .isEqualTo(today.get(ChronoField.HOUR_OF_DAY));
        }
    }
}
