package ru.practicum.shareit.client;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class BaseClient {
    protected final RestTemplate rest;
    protected final String serverUrl;

    public BaseClient(@Value("${shareit-server.url}") String serverUrl) {
        this.serverUrl = serverUrl;
        HttpClient httpClient = HttpClients.createDefault();
        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);
        this.rest = new RestTemplate(requestFactory);
    }

    public ResponseEntity<Object> post(String path, Object body) {
        return send(HttpMethod.POST, path, null, body, null);
    }

    public ResponseEntity<Object> get(String path) {
        return send(HttpMethod.GET, path, null, null, null);
    }

    public ResponseEntity<Object> get(String path, Map<String, Object> params) {
        return send(HttpMethod.GET, path, params, null, null);
    }

    public ResponseEntity<Object> patch(String path, Object body) {
        return send(HttpMethod.PATCH, path, null, body, null);
    }

    public ResponseEntity<Object> delete(String path) {
        return send(HttpMethod.DELETE, path, null, null, null);
    }

    public ResponseEntity<Object> post(String path, Object body, Long userId) {
        return send(HttpMethod.POST, path, null, body, userId);
    }

    public ResponseEntity<Object> get(String path, Long userId) {
        return send(HttpMethod.GET, path, null, null, userId);
    }

    public ResponseEntity<Object> patch(String path, Object body, Long userId) {
        return send(HttpMethod.PATCH, path, null, body, userId);
    }

    public ResponseEntity<Object> delete(String path, Long userId) {
        return send(HttpMethod.DELETE, path, null, null, userId);
    }

    private ResponseEntity<Object> send(HttpMethod method, String path,
                                        Map<String, Object> params, Object body, Long userId) {
        String url = buildUrl(path, params);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }

        HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);

        try {
            return rest.exchange(url, method, requestEntity, Object.class);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    private String buildUrl(String path, Map<String, Object> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serverUrl + path);
        if (params != null) {
            params.forEach(builder::queryParam);
        }
        return builder.build().toUriString();
    }
}