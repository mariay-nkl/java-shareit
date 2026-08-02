package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class BaseClient {
    protected final RestTemplate rest = new RestTemplate();
    protected final String serverUrl;

    public BaseClient(@Value("${shareit-server.url}") String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public ResponseEntity<Object> post(String path, Object body) {
        return send(HttpMethod.POST, path, null, body);
    }

    public ResponseEntity<Object> get(String path) {
        return send(HttpMethod.GET, path, null, null);
    }

    public ResponseEntity<Object> get(String path, Map<String, Object> params) {
        return send(HttpMethod.GET, path, params, null);
    }

    public ResponseEntity<Object> patch(String path, Object body) {
        return send(HttpMethod.PATCH, path, null, body);
    }

    public ResponseEntity<Object> delete(String path) {
        return send(HttpMethod.DELETE, path, null, null);
    }

    private ResponseEntity<Object> send(HttpMethod method, String path,
                                        Map<String, Object> params, Object body) {
        String url = buildUrl(path, params);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);

        return rest.exchange(url, method, requestEntity, Object.class);
    }

    private String buildUrl(String path, Map<String, Object> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serverUrl + path);
        if (params != null) {
            params.forEach(builder::queryParam);
        }
        return builder.build().toUriString();
    }
}

