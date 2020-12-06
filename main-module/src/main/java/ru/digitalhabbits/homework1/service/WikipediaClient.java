package ru.digitalhabbits.homework1.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WikipediaClient {
    public static final String WIKIPEDIA_SEARCH_URL = "https://en.wikipedia.org/w/api.php";

    private static final Logger logger = LoggerFactory.getLogger(WikipediaClient.class);

    @Nonnull
    public String search(@Nonnull String searchString) {
        final var uri = prepareSearchUrl(searchString);
        var wikipediaRequestBody = composeWikipediaGetRequest(uri);

        return composeWikipediaJsonExtract(wikipediaRequestBody);
    }

    @Nonnull
    private URI prepareSearchUrl(@Nonnull String searchString) {
        try {
            return new URIBuilder(WIKIPEDIA_SEARCH_URL)
                    .addParameter("action", "query")
                    .addParameter("format", "json")
                    .addParameter("titles", searchString)
                    .addParameter("prop", "extracts")
                    .addParameter("explaintext", StringUtils.EMPTY)
                    .build();
        } catch (URISyntaxException exception) {
            logger.error(exception.getMessage(), exception);
            throw new RuntimeException(exception);
        }
    }

    @Nonnull
    private String composeWikipediaGetRequest(URI uri) {
        var request = new HttpGet(uri);
        try (var httpClient = HttpClients.createDefault();
             var response = httpClient.execute(request)) {

            var entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity);
            }

        } catch (IOException exception) {
            logger.error(exception.getMessage(), exception);
        }

        return StringUtils.EMPTY;
    }

    @Nonnull
    private String composeWikipediaJsonExtract(String wikipediaJson) {
        var json = new Gson()
                .fromJson(wikipediaJson, JsonObject.class)
                .getAsJsonObject("query")
                .getAsJsonObject("pages");

        var firstKey = json
                .keySet()
                .stream()
                .findFirst();

        if (firstKey.isPresent()) {
            return json
                    .getAsJsonObject(firstKey.get())
                    .getAsJsonPrimitive("extract")
                    .getAsString();
        }

        return StringUtils.EMPTY;
    }
}
