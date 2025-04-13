package co.edu.unicauca.estadistica.api.client;

import co.edu.unicauca.estadistica.api.dto.ApiResponse;
import co.edu.unicauca.estadistica.api.dto.UsuarioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class UsuarioDocenteClient {

    private final RestTemplate restTemplate;

    @Value("${SED_DOCENTE_API_URL}")
    private String baseUrl;

    public UsuarioDTO obtenerUsuarioPorId(Integer idUsuario) {
        try {
            String url = baseUrl + "usuarios/" + idUsuario;

            ResponseEntity<ApiResponse<UsuarioDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<>() {}
            );

            return response.getBody() != null ? response.getBody().getData() : null;

        } catch (Exception e) {
            System.err.println("❌ Error al obtener usuario desde sedDocente: " + e.getMessage());
            return null;
        }
    }
}
