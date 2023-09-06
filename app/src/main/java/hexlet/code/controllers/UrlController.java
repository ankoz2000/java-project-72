package hexlet.code.controllers;

import groovy.util.logging.Slf4j;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.model.query.QUrl;
import io.ebean.PagedList;
import io.javalin.http.Handler;

import java.net.MalformedURLException;
import io.javalin.http.NotFoundResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

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
            url = new java.net.URL(receivedUrl);
        } catch (MalformedURLException urlEx) {
            log.log(System.Logger.Level.ERROR, "Incorrect input url: " + receivedUrl);
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/");
            return;
        }
        String normalizedUrl = String
            .format(
                "%s://%s%s",
                url.getProtocol(),
                url.getHost(),
                url.getPort() == -1 ? "" : ":" + url.getPort()
            )
            .toLowerCase();
        Url exists = new QUrl()
                .name.equalTo(normalizedUrl)
                .findOne();
        if (exists != null) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "info");
        } else {
            exists = new Url(normalizedUrl);
            exists.save();
            log.log(System.Logger.Level.INFO, "Add url: " + normalizedUrl);
            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("flash-type", "success");
        }
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

        if (!urls.isEmpty()) {
            log.log(System.Logger.Level.INFO, "Urls checks object: " + urls.get(0).getUrlChecks());
            log.log(System.Logger.Level.INFO, "Urls check size: " + urls.get(0).getUrlChecks().size());
        }

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

        log.log(System.Logger.Level.INFO, "Show url with id = " + url.getId() + " and name: " + url.getName());

        ctx.attribute("url", url);
        ctx.attribute("urlChecks", url.getUrlChecks());
        ctx.render("urls/show.html");
    };

    public static Handler checkUrl = ctx -> {
        int id = ctx.pathParamAsClass("id", Integer.class).getOrDefault(null);

        log.log(System.Logger.Level.INFO, "Add url check for url with id = " + id);

        Url url = new QUrl()
                .id.equalTo(id)
                .findOne();

        if (url == null) {
            throw new NotFoundResponse();
        }

        HttpResponse responseGet = Unirest
                .get(url.getName())
                .asString();

        Document doc = Jsoup.parse(responseGet.getBody().toString());

        UrlCheck urlCheck = new UrlCheck();

        urlCheck.setStatusCode(responseGet.getStatus());

        if (!doc.title().isEmpty()) {
            log.log(System.Logger.Level.INFO, "Title is: " + doc.title());
            urlCheck.setTitle(doc.title());
        }

        if (doc.selectFirst("h1") != null) {
            log.log(System.Logger.Level.INFO, "H1 is: " + doc.selectFirst("h1").text());
            urlCheck.setH1(doc.selectFirst("h1").text());
        }

        Element metaElement = doc.selectFirst("meta[name=content]");

        if (metaElement != null) {
            log.log(System.Logger.Level.INFO, "Meta is: " + metaElement);
            log.log(System.Logger.Level.INFO, "Meta content is: " + metaElement.attributes().get("content"));
            urlCheck.setDescription(metaElement.attributes().get("content"));
        }

        url.addUrlCheck(urlCheck);

        url.save();

        ctx.attribute("url", url);
        ctx.attribute("urlCheck", urlCheck);
        ctx.sessionAttribute("flash", "Страница успешно проверена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.render("urls/show.html");
    };
}
