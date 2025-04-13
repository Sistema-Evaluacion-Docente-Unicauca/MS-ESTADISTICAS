package co.edu.unicauca.estadistica.api.client;

import co.edu.unicauca.estadistica.api.dto.ConsolidadoDTO;
import co.edu.unicauca.estadistica.api.dto.ApiResponse;
import co.edu.unicauca.estadistica.api.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsolidadoClient {

    private static final Logger logger = LoggerFactory.getLogger(ConsolidadoClient.class);
    private final RestTemplate restTemplate;

    @Value("${SED_DOCENTE_API_URL}")
    private String baseUrl;

    public List<ConsolidadoDTO> obtenerConsolidados(Integer idPeriodo) {
        String url = baseUrl + "consolidado";
        if (idPeriodo != null) {
            url += "&idPeriodo=" + idPeriodo;
        }
        PageResponse<ConsolidadoDTO> resultado = ejecutarConsulta(url, new ParameterizedTypeReference<>() {});
        return resultado != null ? resultado.getContent() : List.of();
    }

    public List<ConsolidadoDTO> obtenerTodosConsolidados() {
        String url = baseUrl + "consolidado/obtener-todos";
        List<ConsolidadoDTO> resultado = ejecutarConsulta(url, new ParameterizedTypeReference<>() {});
        return resultado != null ? resultado : List.of();
    }

    private <T> T ejecutarConsulta(String url, ParameterizedTypeReference<ApiResponse<T>> typeReference) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Void> request = new HttpEntity<>(headers);
    
            ResponseEntity<ApiResponse<T>> response = restTemplate.exchange(url, HttpMethod.GET, request, typeReference);
    
            if (response.getBody() != null) {
                return response.getBody().getData();
            }
        } catch (Exception e) {
            logger.error("❌ Error en llamada a la URL {}: {}", url, e.getMessage(), e);
        }
        return null;
    }
}
