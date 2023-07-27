package hexlet.code;

import hexlet.code.controllers.MainController;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class App {

    public static Javalin getApp() {
        Javalin app = Javalin.create(config -> {
                    if (!isProduction()) {
                        config.plugins.enableDevLogging();
                    }
                    JavalinThymeleaf.init(getTemplateEngine());
                })
//                .get("/", ctx -> ctx.result("Hello World"))
                .start(8080);
        addRoutes(app);
        app.before(ctx -> {
            ctx.attribute("ctx", ctx);
        });
        return app;
    }

    private static TemplateEngine getTemplateEngine() {
        TemplateEngine templateEngine = new TemplateEngine();

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");

        templateEngine.addTemplateResolver(templateResolver);
        templateEngine.addDialect(new LayoutDialect());
        templateEngine.addDialect(new Java8TimeDialect());

        return templateEngine;
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

    private static void addRoutes(Javalin app) {
        app.get("/", MainController.welcome);
    }
}
