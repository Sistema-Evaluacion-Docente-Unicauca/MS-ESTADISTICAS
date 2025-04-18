package co.edu.unicauca.estadistica.api.client;

import co.edu.unicauca.estadistica.api.dto.PageResponse;
import co.edu.unicauca.estadistica.api.dto.TipoActividadDTO;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
    public TipoActividadDTO obtenerPorId(Integer idTipoActividad) {
        String url = baseUrl + "tipo-actividad/" + idTipoActividad;
        return get(url, new ParameterizedTypeReference<>() {});
    }

    public List<TipoActividadDTO> obtenerTipoActividad() {
        String url = baseUrl + "tipo-actividad";
    
        try {
            ResponseEntity<PageResponse<TipoActividadDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
    
            PageResponse<TipoActividadDTO> result = response.getBody();
    
            if (result != null && result.getContent() != null) {
                logger.info("✅ Tipos de actividad recibidos: {} elementos", result.getContent().size());
                return result.getContent();
            }
    
        } catch (Exception e) {
            logger.error("❌ Error al obtener tipos de actividad: {}", e.getMessage(), e);
        }
    
        return List.of();
    }
}
