package hexlet.code.controllers;

import groovy.util.logging.Slf4j;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.model.query.QUrl;
import io.ebean.PagedList;
import io.javalin.http.Handler;

import java.net.MalformedURLException;
import io.javalin.http.NotFoundResponse;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.avaje.classpath.scanner.internal.ScanLog.log;

@Slf4j
public class UrlController {

    public static Handler addUrl = ctx -> {
        String receivedUrl = ctx.formParam("name");

        java.net.URL url;
        log.log(System.Logger.Level.INFO, "Received url: " + receivedUrl);
        try {
            if (receivedUrl == null || receivedUrl.isEmpty()) {
                throw new MalformedURLException();
            }
            url = new java.net.URL(receivedUrl);
        } catch (MalformedURLException urlEx) {
            log.log(System.Logger.Level.ERROR, "Incorrect input url");

            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.attribute("url", receivedUrl);
            ctx.render("index.html");
            return;
        }
        String port = url.getPort() != -1 ? ":" + url.getPort() : "";
        Url urlEntity = new Url(url.getProtocol() + "://" + url.getHost() + port);
        boolean exists = new QUrl()
                .name.equalTo(url.toString())
                .exists();
        if (exists) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.attribute("url", receivedUrl);
            ctx.render("urls/index.html");
        }

        log.log(System.Logger.Level.INFO, "Add url: " + url.getProtocol() + "://" + url.getHost() + port);

        urlEntity.save();

        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/urls");
    };

    public static Handler listUrls = ctx -> {
        String term = ctx.queryParamAsClass("term", String.class).getOrDefault("");
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1) - 1;
        int rowsPerPage = 10;

        PagedList<Url> pagedUrls = new QUrl()
                .name.icontains(term)
                .setFirstRow(page * rowsPerPage)
                .setMaxRows(rowsPerPage)
                .orderBy()
                .id.asc()
                .findPagedList();

        List<Url> urls = pagedUrls.getList();

        int lastPage = pagedUrls.getTotalPageCount() + 1;
        int currentPage = pagedUrls.getPageIndex() + 1;
        List<Integer> pages = IntStream
                .range(1, lastPage)
                .boxed()
                .collect(Collectors.toList());

        log.log(System.Logger.Level.INFO, urls.get(0).getUrlCheck());

        ctx.attribute("urls", urls);
        ctx.attribute("term", term);
        ctx.attribute("pages", pages);
        ctx.attribute("currentPage", currentPage);
        ctx.render("urls/index.html");
    };

    public static Handler newUrl = ctx -> {
        Url url = new Url();

        ctx.attribute("url", url);
        ctx.render("urls/new.html");
    };

    public static Handler showUrl = ctx -> {
        int id = ctx.pathParamAsClass("id", Integer.class).getOrDefault(null);

        Url url = new QUrl()
                .id.equalTo(id)
                .findOne();

        if (url == null) {
            throw new NotFoundResponse();
        }

        ctx.attribute("url", url);
        ctx.attribute("urlChecks", url.getUrlCheck());
        ctx.render("urls/show.html");
    };

    public static Handler checkUrl = ctx -> {
        int id = ctx.pathParamAsClass("id", Integer.class).getOrDefault(null);

        Url url = new QUrl()
                .id.equalTo(id)
                .findOne();

        if (url == null) {
            throw new NotFoundResponse();
        }

        UrlCheck urlCheck = new UrlCheck("new check");
        urlCheck.setUrl(url);

        urlCheck.save();

        ctx.attribute("url", url);
        ctx.attribute("urlCheck", urlCheck);
        ctx.render("urls/show.html");
    };
}
