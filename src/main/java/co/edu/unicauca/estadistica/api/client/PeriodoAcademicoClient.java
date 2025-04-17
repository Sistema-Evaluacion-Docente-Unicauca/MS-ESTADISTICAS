package co.edu.unicauca.estadistica.api.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import co.edu.unicauca.estadistica.api.dto.PeriodoAcademicoDTO;

@Component
@RequiredArgsConstructor
public class PeriodoAcademicoClient extends BaseRestClient {

    private final RestTemplate restTemplate;

    @Value("${SED_DOCENTE_API_URL}")
    private String baseUrl;

    @Override
    protected RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public PeriodoAcademicoDTO obtenerPeriodoActivo() {
        String url = baseUrl + "periodos-academicos/activo";
        return get(url, new ParameterizedTypeReference<>() {});
    }
}
