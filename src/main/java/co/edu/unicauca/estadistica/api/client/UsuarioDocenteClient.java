package co.edu.unicauca.estadistica.api.client;

import co.edu.unicauca.estadistica.api.dto.UsuarioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class UsuarioDocenteClient extends BaseRestClient {

    private final RestTemplate restTemplate;

    @Value("${SED_DOCENTE_API_URL}")
    private String baseUrl;

    @Override
    protected RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public UsuarioDTO obtenerUsuarioPorId(Integer idUsuario, String token) {
        String url = baseUrl + "usuarios/" + idUsuario;
        return get(url, new ParameterizedTypeReference<>() {}, token);
    }    
}
