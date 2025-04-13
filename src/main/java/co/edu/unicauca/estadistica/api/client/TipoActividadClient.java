package co.edu.unicauca.estadistica.api.client;

import co.edu.unicauca.estadistica.api.dto.TipoActividadDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class TipoActividadClient {

    private static final Logger logger = LoggerFactory.getLogger(TipoActividadClient.class);
    private final RestTemplate restTemplate;

    @Value("${SED_DOCENTE_API_URL}")
    private String baseUrl;

    /**
     * Consulta el tipo de actividad por su ID desde el microservicio sedDocente.
     *
     * @param idTipoActividad ID del tipo de actividad
     * @return TipoActividadDTO o null si no se encuentra o hay error
     */
    public TipoActividadDTO obtenerPorId(Integer idTipoActividad) {
        try {
            String url = baseUrl + "tipo-actividad/" + idTipoActividad;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<TipoActividadDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {}
            );

            if (response.getBody() != null) {
                logger.debug("✅ Tipo de actividad encontrado: {}", response.getBody().getNombre());
                return response.getBody();
            } else {
                logger.warn("⚠️ No se obtuvo respuesta válida para idTipoActividad={}", idTipoActividad);
                return null;
            }

        } catch (Exception e) {
            logger.error("❌ Error al consultar tipo de actividad {}: {}", idTipoActividad, e.getMessage(), e);
            return null;
        }
    }
}
