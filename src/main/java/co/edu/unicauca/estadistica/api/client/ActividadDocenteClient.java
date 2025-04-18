package co.edu.unicauca.estadistica.api.client;

import co.edu.unicauca.estadistica.api.dto.ActividadDTO;
import co.edu.unicauca.estadistica.api.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ActividadDocenteClient extends BaseRestClient {

    private final RestTemplate restTemplate;

    @Value("${SED_DOCENTE_API_URL}")
    private String baseUrl;

    @Override
    protected RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public List<ActividadDTO> obtenerActividadesPorEvaluado(Integer idEvaluado, Integer idPeriodo) {
        String url = baseUrl + "actividades/buscarActividadesPorEvaluado?idEvaluado=" + idEvaluado + "&page=0&size=1000";
        if (idPeriodo != null) {
            url += "&idPeriodo=" + idPeriodo;
        }

        PageResponse<ActividadDTO> response = getPage(url, new ParameterizedTypeReference<>() {});
        return response != null ? response.getContent() : List.of();
    }
}
