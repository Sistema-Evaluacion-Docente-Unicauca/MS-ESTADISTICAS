package co.edu.unicauca.estadistica.api.client;

import co.edu.unicauca.estadistica.api.dto.ConsolidadoDTO;
import co.edu.unicauca.estadistica.api.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsolidadoClient extends BaseRestClient {

    private final RestTemplate restTemplate;

    @Value("${SED_DOCENTE_API_URL}")
    private String baseUrl;

    @Override
    protected RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public List<ConsolidadoDTO> obtenerConsolidados(Integer idPeriodo, String token) {
        String url = baseUrl + "consolidado";
        if (idPeriodo != null) {
            url += "?idPeriodoAcademico=" + idPeriodo;
        }
    
        PageResponse<ConsolidadoDTO> resultado = getPage(url, new ParameterizedTypeReference<>() {}, token);
        return resultado != null ? resultado.getContent() : List.of();
    }

    public List<ConsolidadoDTO> obtenerTodosConsolidados(String token) {
        String url = baseUrl + "consolidado/obtener-todos";
        List<ConsolidadoDTO> resultado = get(url, new ParameterizedTypeReference<>() {}, token);
        return resultado != null ? resultado : List.of();
    }
}
