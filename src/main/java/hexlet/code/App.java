package hexlet.code;

import io.javalin.Javalin;

public class App {

    public static Javalin getApp() {
        Javalin app = Javalin.create(config -> {
                    if (!isProduction()) {
                        config.plugins.enableDevLogging();
                    }
                })
                .get("/", ctx -> ctx.result("Hello World"))
                .start(8080);
        return app;
    }

    public static void main(String[] args) {
        Javalin app = getApp();
        app.start();
    }

    private static String getMode() {
        return System.getenv().getOrDefault("APP_ENV", "development");
    }

    private static boolean isProduction() {
        return getMode().equals("production");
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "3000");
        return Integer.valueOf(port);
    }
}
