package co.edu.unicauca.estadistica.api.client;

import co.edu.unicauca.estadistica.api.dto.PageResponse;
import co.edu.unicauca.estadistica.api.dto.TipoActividadDTO;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class TipoActividadClient extends BaseRestClient {

    private final RestTemplate restTemplate;

    @Value("${SED_DOCENTE_API_URL}")
    private String baseUrl;

    @Override
    protected RestTemplate getRestTemplate() {
        return restTemplate;
    }

    /**
     * Consulta el tipo de actividad por su ID desde el microservicio sedDocente.
     *
     * @param idTipoActividad ID del tipo de actividad
     * @return TipoActividadDTO o null si no se encuentra o hay error
     */
    public TipoActividadDTO obtenerPorId(Integer idTipoActividad, String token) {
        String url = baseUrl + "tipo-actividad/" + idTipoActividad;
        return get(url, new ParameterizedTypeReference<>() {
        }, token);
    }

    public List<TipoActividadDTO> obtenerTipoActividad(String token) {
        String url = baseUrl + "tipo-actividad?page=0&size=1000";

        PageResponse<TipoActividadDTO> response = getPage(url, new ParameterizedTypeReference<>() {}, token);

        return response != null ? response.getContent() : List.of();
    }
}
