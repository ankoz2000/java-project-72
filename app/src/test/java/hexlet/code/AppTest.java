package hexlet.code;

import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.model.Url;
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
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public final class AppTest {

    @Test
    void testInit() {
        assertThat(true).isEqualTo(true);
    }

    private static Javalin app;
    private static String baseUrl;
    private static Url existingUrl;
    private HikariDataSource dataSource;
    private static MockWebServer server;

    private static String getDatabaseUrl() {
        return System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project");
    }

    @BeforeAll
    public static void beforeAll() throws SQLException, IOException {
        app = App.getApp();
        app.start();
        int port = app.port();
        baseUrl = "http://localhost:" + port;

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
    void beforeEach() throws IOException, SQLException {
        var sql2 = "INSERT INTO urls (name, created_at) VALUES ('https://javalin.io', '2023-01-01 14:57')";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql2)) {
            stmt.executeUpdate();
        }
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
            assertThat(body).contains("2023-01-01 14:57");
        }

        @Test
        void testShow() {
            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls/1")
                    .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(body).contains("https://javalin.io");
            assertThat(body).contains("2023-01-01 14:57");
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
                    .field("url", inputUrlName)
                    .asEmpty();

            assertThat(responsePost.getStatus()).isEqualTo(302);
            assertThat(responsePost.getHeaders().getFirst("Location")).isEqualTo("/urls");

            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls")
                    .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(body).contains(inputUrlName);
        }

        @Test
        void testUrlCheck() {
            String inputUrlName = "https://vk.com";
            HttpResponse responsePost = Unirest
                    .post(baseUrl + "/urls")
                    .field("url", inputUrlName)
                    .asEmpty();

            HttpResponse responsePost2 = Unirest
                    .post(baseUrl + "/urls/1/checks")
                    .asString();

            assertThat(responsePost2.getStatus()).isEqualTo(200);

            HttpUrl testUrl = server.url("/v1/chat/");

            HttpResponse responsePost3 = Unirest
                    .get(testUrl.url().toString())
                    .asString();

            assertThat(responsePost3.getBody()).isEqualTo("{test: true}");

        }
    }
}
