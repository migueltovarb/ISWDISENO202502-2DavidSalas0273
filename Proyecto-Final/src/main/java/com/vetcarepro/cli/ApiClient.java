package com.vetcarepro.cli;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Encapsula peticiones HTTP con manejo básico de errores y JWT.
 */
public class ApiClient {

    private final String baseUrl;
    private final HttpClient httpClient;
    private final SessionManager session;
    private final MenuRenderer ui;

    public ApiClient(String baseUrl, SessionManager session, MenuRenderer ui) {
        this.baseUrl = baseUrl;
        this.session = session;
        this.ui = ui;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    }

    public String get(String path) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + path))
            .timeout(Duration.ofSeconds(15))
            .GET();
        applyAuth(builder);
        HttpResponse<String> resp = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return handle(resp);
    }

    public String post(String path, String json, boolean withAuth) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + path))
            .timeout(Duration.ofSeconds(15))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json));
        if (withAuth) {
            applyAuth(builder);
        }
        HttpResponse<String> resp = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return handle(resp);
    }

    public String put(String path, String json) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + path))
            .timeout(Duration.ofSeconds(15))
            .header("Content-Type", "application/json")
            .PUT(HttpRequest.BodyPublishers.ofString(json));
        applyAuth(builder);
        HttpResponse<String> resp = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return handle(resp);
    }

    public String delete(String path) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + path))
            .timeout(Duration.ofSeconds(15))
            .DELETE();
        applyAuth(builder);
        HttpResponse<String> resp = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return handle(resp);
    }

    private void applyAuth(HttpRequest.Builder builder) {
        if (session.hasToken()) {
            builder.header("Authorization", "Bearer " + session.getToken());
        }
    }

    private String handle(HttpResponse<String> resp) {
        int code = resp.statusCode();
        String body = resp.body();
        if (code == 401) {
            ui.error("No autorizado, inicie sesión.");
        } else if (code == 403) {
            ui.error("Acceso denegado para su rol.");
        } else if (code == 404) {
            ui.error("No encontrado.");
        } else if (code >= 500) {
            ui.error("Error interno del servidor.");
        }
        return body;
    }
}
