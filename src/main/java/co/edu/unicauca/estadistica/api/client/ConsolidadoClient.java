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
        try {
            String url = baseUrl + "consolidado";
            if (idPeriodo != null) {
                url += "&idPeriodo=" + idPeriodo;
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<ApiResponse<PageResponse<ConsolidadoDTO>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<>() {}
            );

            return response.getBody() != null ? response.getBody().getData().getContent() : List.of();

        } catch (Exception e) {
            logger.error("❌ Error al consultar consolidados: {}", e.getMessage(), e);
            return List.of();
        }
    }
}
