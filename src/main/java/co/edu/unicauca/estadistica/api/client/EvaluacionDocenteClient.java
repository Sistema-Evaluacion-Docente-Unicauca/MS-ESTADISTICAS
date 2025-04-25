package co.edu.unicauca.estadistica.api.client;

import co.edu.unicauca.estadistica.api.dto.ApiResponse;
import co.edu.unicauca.estadistica.api.dto.PeriodoEvaluacionDTO;
import co.edu.unicauca.estadistica.api.util.AuthHeaderUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EvaluacionDocenteClient {

    private static final Logger logger = LoggerFactory.getLogger(EvaluacionDocenteClient.class);

    private final RestTemplate restTemplate;

    @Value("${SED_DOCENTE_API_URL}")
    private String baseUrl;

    /**
     * Obtiene las evaluaciones estructuradas desde el microservicio sedDocente.
     *
     * @param token JWT enviado desde el frontend
     * @return lista de PeriodoEvaluacionDTO
     */
    public List<PeriodoEvaluacionDTO> obtenerEvaluacionesEstructuradas(String token) {
        String url = baseUrl + "evaluacion-estudiante/respuesta";

        try {
            HttpEntity<Void> request = AuthHeaderUtil.crearRequest(token);

            ResponseEntity<ApiResponse<List<PeriodoEvaluacionDTO>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {}
            );

            if (response.getBody() != null) {
                return response.getBody().getData();
            }
        } catch (Exception e) {
            logger.error("❌ Error al consumir evaluaciones estructuradas: {}", e.getMessage(), e);
        }

        return Collections.emptyList();
    }
}
