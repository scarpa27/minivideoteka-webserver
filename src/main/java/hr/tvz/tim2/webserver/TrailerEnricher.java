package hr.tvz.tim2.webserver;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrailerEnricher {

    private static final String TMDB_API_KEY = "db829fa711fa53eeebdbb9756c7e1773";
    private static final String TMDB_BEARER_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJkYjgyOWZhNzExZmE1M2VlZWJkYmI5NzU2YzdlMTc3MyIsIm5iZiI6MTc2ODQxNDA3MC41ODYsInN1YiI6IjY5NjdkYjc2NTkwNDVkZDU2MTJmODdkYSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.dwcpgEGKcvHUYZFPLjPuGiBTY58IpOQ7TIK1YhPixUQ";
    private static final String OG_FILE = "C:\\Projekti\\webserver\\src\\main\\resources\\top250.json";
    private static final String NEW_FILE = "C:\\Projekti\\webserver\\src\\main\\resources\\top250_rich.json";

    // Matches tt1234567 or tt12345678 anywhere in a string
    private static final Pattern IMDB_ID = Pattern.compile("(tt\\d{7,8})");

    public static void main(String[] args) throws Exception {
        String apiKey = TMDB_API_KEY;
        String bearer = TMDB_BEARER_TOKEN;

        Path input = Path.of(OG_FILE);
        Path output = Path.of(NEW_FILE);

        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        HttpClient http = HttpClient.newHttpClient();
        TmdbClient tmdb = new TmdbClient(http, mapper, apiKey, bearer);

        // Read movies as generic JSON objects so we can add new fields without needing your entities
        List<ObjectNode> movies = mapper.readValue(Files.readString(input),
                                                   new TypeReference<>() {});

        Map<String, Optional<TmdbVideo>> cacheByImdb = new HashMap<>();

        int enriched = 0;
        int missingImdb = 0;
        int noTrailer = 0;

        for (int i = 0; i < movies.size(); i++) {
            ObjectNode movie = movies.get(i);

            String imdbId = extractImdbId(movie);
            if (imdbId == null) {
                missingImdb++;
                continue;
            }

            Optional<TmdbVideo> best = cacheByImdb.computeIfAbsent(imdbId, id -> {
                try {
                    OptionalInt tmdbMovieId = tmdb.findMovieIdByImdb(id);
                    if (tmdbMovieId.isEmpty()) return Optional.empty();
                    return tmdb.getBestYoutubeTrailer(tmdbMovieId.getAsInt());
                } catch (Exception e) {
                    System.err.printf("TMDB error for %s: %s%n", id, e.getMessage());
                    return Optional.empty();
                }
            });

            if (best.isEmpty()) {
                noTrailer++;
                continue;
            }

            // Add fields anywhere you like. Here I add a simple "youtubeTrailer" object.
            ObjectNode youtubeTrailer = mapper.createObjectNode();
            youtubeTrailer.put("provider", "YOUTUBE");
            youtubeTrailer.put("videoId", best.get().key());
            youtubeTrailer.put("name", best.get().name());
            youtubeTrailer.put("watchUrl", "https://www.youtube.com/watch?v=" + best.get().key());
            youtubeTrailer.put("embedUrl", "https://www.youtube.com/embed/" + best.get().key());
            movie.set("youtubeTrailer", youtubeTrailer);

            // OPTIONAL: if you already have "trailer.embedUrl" in JSON and want to overwrite it:
            // overwriteExistingTrailerEmbedUrl(movie, "https://www.youtube.com/embed/" + best.get().key());

            enriched++;

            // simple throttle to be nice (adjust/remove if you want)
            Thread.sleep(120);
            if ((i + 1) % 25 == 0) {
                System.out.printf("Progress %d/%d (enriched=%d)%n", (i + 1), movies.size(), enriched);
            }
        }

        String outJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(movies);
        Files.writeString(output, outJson, StandardCharsets.UTF_8);

        System.out.println("Done.");
        System.out.printf("Movies: %d | enriched: %d | missing imdb: %d | no trailer: %d%n",
                          movies.size(), enriched, missingImdb, noTrailer);
        System.out.println("Wrote: " + output.toAbsolutePath());
    }

    private static String extractImdbId(ObjectNode movie) {
        // Try a few common places; expand this list to match your JSON schema.
        List<String> candidateFields = List.of("imdbId", "imdb", "imdbUrl", "url", "imdbPath");

        for (String f : candidateFields) {
            JsonNode n = movie.get(f);
            if (n != null && n.isTextual()) {
                String id = firstImdbIdIn(n.asText());
                if (id != null) return id;
            }
        }

        // If nested like: { "imdb": { "url": "/title/tt..." } }
        JsonNode imdb = movie.get("imdb");
        if (imdb != null && imdb.isObject()) {
            JsonNode url = imdb.get("url");
            if (url != null && url.isTextual()) {
                return firstImdbIdIn(url.asText());
            }
        }

        return null;
    }

    private static String firstImdbIdIn(String text) {
        if (text == null) return null;
        Matcher m = IMDB_ID.matcher(text);
        return m.find() ? m.group(1) : null;
    }

    private static void overwriteExistingTrailerEmbedUrl(ObjectNode movie, String newEmbedUrl) {
        JsonNode trailer = movie.get("trailer");
        if (trailer != null && trailer.isObject()) {
            ((ObjectNode) trailer).put("embedUrl", newEmbedUrl);
        }
    }

    // ---------------- TMDB client & DTOs ----------------

    static final class TmdbClient {
        private final HttpClient http;
        private final ObjectMapper mapper;
        private final String apiKey;
        private final String bearer;

        TmdbClient(HttpClient http, ObjectMapper mapper, String apiKey, String bearer) {
            this.http = http;
            this.mapper = mapper;
            this.apiKey = apiKey;
            this.bearer = bearer;
        }

        OptionalInt findMovieIdByImdb(String imdbId) throws IOException, InterruptedException {
            // GET /3/find/{external_id}?external_source=imdb_id
            // (TMDB docs) :contentReference[oaicite:2]{index=2}
            URI uri = uri("/3/find/" + enc(imdbId),
                          Map.of("external_source", "imdb_id"));

            JsonNode root = getJson(uri);
            JsonNode movieResults = root.get("movie_results");
            if (movieResults == null || !movieResults.isArray() || movieResults.isEmpty()) return OptionalInt.empty();

            JsonNode first = movieResults.get(0);
            JsonNode id = first.get("id");
            return (id != null && id.isInt()) ? OptionalInt.of(id.asInt()) : OptionalInt.empty();
        }

        Optional<TmdbVideo> getBestYoutubeTrailer(int tmdbMovieId) throws IOException, InterruptedException {
            // GET /3/movie/{movie_id}/videos
            // (TMDB docs) :contentReference[oaicite:3]{index=3}
            URI uri = uri("/3/movie/" + tmdbMovieId + "/videos",
                          Map.of("language", "en-US"));

            JsonNode root = getJson(uri);
            JsonNode results = root.get("results");
            if (results == null || !results.isArray() || results.isEmpty()) return Optional.empty();

            List<TmdbVideo> videos = new ArrayList<>();
            for (JsonNode v : results) {
                String site = text(v, "site");
                String type = text(v, "type");
                String key = text(v, "key");
                String name = text(v, "name");
                boolean official = bool(v, "official");
                Instant publishedAt = instant(v, "published_at");

                if (key == null || site == null) continue;
                videos.add(new TmdbVideo(site, type, key, name, official, publishedAt));
            }

            return videos.stream()
                    .filter(v -> "YouTube".equalsIgnoreCase(v.site()))
                    .max(Comparator.comparingInt(TmdbClient::scoreVideo));
        }

        private static int scoreVideo(TmdbVideo v) {
            int score = 0;
            if ("Trailer".equalsIgnoreCase(v.type())) score += 100;
            if ("Teaser".equalsIgnoreCase(v.type())) score += 50;
            if (v.official()) score += 25;
            if (v.name() != null && v.name().toLowerCase().contains("official")) score += 10;
            return score;
        }

        private JsonNode getJson(URI uri) throws IOException, InterruptedException {
            HttpRequest.Builder b = HttpRequest.newBuilder(uri).GET()
                    .header("Accept", "application/json");

            if (bearer != null && !bearer.isBlank()) {
                b.header("Authorization", "Bearer " + bearer);
            }

            HttpResponse<String> resp = http.send(b.build(), HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() / 100 != 2) {
                throw new IOException("TMDB HTTP " + resp.statusCode() + " for " + uri + " body=" + resp.body());
            }
            return mapper.readTree(resp.body());
        }

        private URI uri(String path, Map<String, String> qs) {
            String base = "https://api.themoviedb.org";
            StringBuilder sb = new StringBuilder(base).append(path).append("?");

            Map<String, String> params = new LinkedHashMap<>(qs);
            // If using v3 key auth, add api_key param
            if (apiKey != null && !apiKey.isBlank()) params.put("api_key", apiKey);

            boolean first = true;
            for (var e : params.entrySet()) {
                if (!first) sb.append("&");
                first = false;
                sb.append(enc(e.getKey())).append("=").append(enc(e.getValue()));
            }
            return URI.create(sb.toString());
        }
    }

    record TmdbVideo(String site, String type, String key, String name, boolean official, Instant publishedAt) {}

    private static String text(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return (v != null && v.isTextual()) ? v.asText() : null;
    }

    private static boolean bool(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return v != null && v.isBoolean() && v.asBoolean();
    }

    private static Instant instant(JsonNode node, String field) {
        JsonNode v = node.get(field);
        if (v == null || !v.isTextual()) return null;
        try { return Instant.parse(v.asText()); } catch (Exception ignored) { return null; }
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}