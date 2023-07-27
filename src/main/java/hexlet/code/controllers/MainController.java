package hexlet.code.controllers;

import io.javalin.http.Handler;

public class MainController {

    public static Handler welcome = ctx -> {
        ctx.render("index.html");
    };
}
