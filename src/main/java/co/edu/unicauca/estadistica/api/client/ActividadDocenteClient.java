package co.edu.unicauca.estadistica.api.client;

import co.edu.unicauca.estadistica.api.dto.ActividadDTO;
import co.edu.unicauca.estadistica.api.dto.ActividadPageResponseDTO;
import co.edu.unicauca.estadistica.api.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ActividadDocenteClient {

    private final RestTemplate restTemplate;

    @Value("${SED_DOCENTE_API_URL}")
    private String baseUrl;

    public List<ActividadDTO> obtenerActividadesPorEvaluado(Integer idEvaluado, Integer idPeriodo) {
        String url = baseUrl + "actividades/buscarActividadesPorEvaluado?idEvaluado=" + idEvaluado + "&page=0&size=1000";
        if (idPeriodo != null) {
            url += "&idPeriodo=" + idPeriodo;
        }
    
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> request = new HttpEntity<>(headers);
    
        ResponseEntity<ApiResponse<ActividadPageResponseDTO>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            request,
            new ParameterizedTypeReference<>() {}
        );
    
        if (response.getBody() != null && response.getBody().getData() != null) {
            return response.getBody().getData().getContent();
        }
    
        return List.of();
    }
}