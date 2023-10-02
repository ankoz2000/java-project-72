package hexlet.code.controllers;

import groovy.util.logging.Slf4j;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Handler;

import java.net.MalformedURLException;
import io.javalin.http.NotFoundResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Slf4j
public class UrlController {
    private static final Logger LOG = LoggerFactory.getLogger(UrlController.class);

    public static Handler addUrl = ctx -> {
        String receivedUrl = ctx.formParam("url");

        java.net.URL url;
        LOG.info("Received url: " + receivedUrl);
        try {
            url = new java.net.URL(receivedUrl);
        } catch (MalformedURLException urlEx) {
            LOG.error("Incorrect input url: " + receivedUrl);
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
        Optional<Url> exists = UrlRepository.findByName(normalizedUrl);
        if (exists.isPresent()) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "info");
        } else {
            Url newUrl = new Url(normalizedUrl);
            newUrl.setCreatedAt(new Timestamp(Calendar.getInstance().getTimeInMillis()));
            UrlRepository.save(newUrl);
            LOG.info("Add url: " + normalizedUrl);
            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("flash-type", "success");
        }
        ctx.attribute("urls", UrlRepository.getEntities());
        ctx.redirect("/urls");
    };

    public static Handler listUrls = ctx -> {
        String term = ctx.queryParamAsClass("term", String.class).getOrDefault("");
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1) - 1;
        int rowsPerPage = 10;

        List<Url> urls = UrlRepository.getEntities();
        List<UrlCheck> urlChecksLatest = new ArrayList<>();
        if (!urls.isEmpty()) {
            urlChecksLatest = UrlCheckRepository.findLatestForUrls(urls.stream()
                    .map(Url::getId)
                    .collect(Collectors.toList()));
        }

        Map<Url, UrlCheck> result = new HashMap<>();

        if (!urlChecksLatest.isEmpty()) {
            urlChecksLatest.forEach(uc -> {
                urls.forEach(u -> {
                    if (u.getId().equals(uc.getUrlId())) {
                        result.put(u, uc);
                        u.setUrlCheck(uc);
                    }
                });
            });
        }

        int lastPage = urls.size() / 10 + 1;
        int currentPage = urls.size() / 10 + 1;
        List<Integer> pages = IntStream
                .range(1, lastPage)
                .boxed()
                .collect(Collectors.toList());

        ctx.attribute("urls", urls);
        ctx.attribute("latestChecks", urlChecksLatest);
        ctx.attribute("result", result);
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
        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);

        Optional<Url> optionalUrl = UrlRepository.find(id);

        if (optionalUrl.isEmpty()) {
            throw new NotFoundResponse();
        }
        Url url = optionalUrl.get();

        List<UrlCheck> urlChecks = UrlCheckRepository.findByUrlId(url.getId());

        LOG.info("Show url with id = " + url.getId() + " and name: " + url.getName());
        LOG.info("UrlChecks size is: " + urlChecks.size());

        ctx.attribute("url", url);
        ctx.attribute("urlChecks", urlChecks);
        ctx.render("urls/show.html");
    };

    public static Handler checkUrl = ctx -> {
        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);

        LOG.info("Add url check for url with id = " + id);

        Optional<Url> urlOptional = UrlRepository.find(id);

        if (!urlOptional.isPresent()) {
            throw new NotFoundResponse();
        }

        Url url = urlOptional.get();

        HttpResponse responseGet = Unirest
                .get(url.getName())
                .asString();

        Document doc = Jsoup.parse(responseGet.getBody().toString());

        UrlCheck urlCheck = new UrlCheck();

        urlCheck.setStatusCode(responseGet.getStatus());

        if (!doc.title().isEmpty()) {
            LOG.info("Title is: " + doc.title());
            urlCheck.setTitle(doc.title());
        }

        if (doc.selectFirst("h1") != null) {
            LOG.info("H1 is: " + doc.selectFirst("h1").text());
            urlCheck.setH1(doc.selectFirst("h1").text());
        }

        Element metaElement = doc.selectFirst("meta[name=description]");

        if (metaElement != null) {
            LOG.info("Meta is: " + metaElement);
            LOG.info("Meta content is: " + metaElement.attributes().get("content"));
            urlCheck.setDescription(metaElement.attributes().get("content"));
        }

        urlCheck.setUrlId(url.getId());
        UrlCheckRepository.save(urlCheck);

        ctx.attribute("url", url);
        ctx.attribute("urlCheck", urlCheck);
        ctx.sessionAttribute("flash", "Страница успешно проверена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.render("urls/show.html");
    };
}
