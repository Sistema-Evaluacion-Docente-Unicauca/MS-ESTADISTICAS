package co.edu.unicauca.estadistica.api.client;

import co.edu.unicauca.estadistica.api.dto.ApiResponse;
import co.edu.unicauca.estadistica.api.dto.PageResponse;
import co.edu.unicauca.estadistica.api.util.AuthHeaderUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public abstract class BaseRestClient {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected abstract RestTemplate getRestTemplate();

    protected <T> T get(String url, ParameterizedTypeReference<ApiResponse<T>> responseType, String token) {
        try {
            HttpEntity<Void> request = AuthHeaderUtil.crearRequest(token);
            ResponseEntity<ApiResponse<T>> response = getRestTemplate().exchange(url, HttpMethod.GET, request, responseType);
            return response.getBody() != null ? response.getBody().getData() : null;
        } catch (Exception e) {
            logger.error("❌ Error al consumir '{}': {}", url, e.getMessage(), e);
            return null;
        }
    }

    protected <T> PageResponse<T> getPage(String url, ParameterizedTypeReference<ApiResponse<PageResponse<T>>> responseType, String token) {
        try {
            HttpEntity<Void> request = AuthHeaderUtil.crearRequest(token);
            ResponseEntity<ApiResponse<PageResponse<T>>> response = getRestTemplate().exchange(url, HttpMethod.GET, request, responseType);
            return response.getBody() != null ? response.getBody().getData() : null;
        } catch (Exception e) {
            logger.error("❌ Error al consumir página '{}': {}", url, e.getMessage(), e);
            return null;
        }
    }
}
