package co.edu.unicauca.estadistica.api.service;

import org.springframework.stereotype.Service;
import co.edu.unicauca.estadistica.api.client.ConsolidadoClient;
import co.edu.unicauca.estadistica.api.dto.ApiResponse;
import co.edu.unicauca.estadistica.api.dto.ConsolidadoDTO;
import co.edu.unicauca.estadistica.api.dto.PromedioDepartamentalResponse;
import co.edu.unicauca.estadistica.api.dto.PromedioDepartamentoDTO;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class PromedioDepartamentoServiceImpl implements PromedioDepartamentoService {

    private final ConsolidadoClient consolidadoClient;
    private static final Logger logger = LoggerFactory.getLogger(PromedioDepartamentoServiceImpl.class);

    @Override
    public ApiResponse<PromedioDepartamentalResponse> obtenerPromediosPorDepartamento(Integer idPeriodo, String token) {
        List<ConsolidadoDTO> consolidados = consolidadoClient.obtenerConsolidados(idPeriodo, token);

        if (consolidados.isEmpty()) {
            logger.warn("⚠️ No se encontraron consolidados para el periodo {}", idPeriodo);
            return respuestaSinResultados();
        }

        List<PromedioDepartamentoDTO> resultado = calcularPromedios(consolidados);

        return new ApiResponse<>(200, "Promedios generados correctamente", new PromedioDepartamentalResponse(resultado));
    }

    /**
     * Calcula los promedios por departamento agrupando los consolidados.
     */
    private List<PromedioDepartamentoDTO> calcularPromedios(List<ConsolidadoDTO> consolidados) {
        Map<String, Double> promedios = consolidados.stream()
            .filter(c -> c.getDepartamento() != null && c.getCalificacion() != null)
            .collect(Collectors.groupingBy(
                ConsolidadoDTO::getDepartamento,
                Collectors.averagingDouble(ConsolidadoDTO::getCalificacion)
            ));

        return promedios.entrySet().stream()
            .map(entry -> new PromedioDepartamentoDTO(
                entry.getKey(),
                Math.round(entry.getValue() * 100.0) / 100.0
            ))
            .sorted(Comparator.comparing(PromedioDepartamentoDTO::getDepartamento))
            .toList();
    }

    /**
     * Construye una respuesta vacía estandarizada.
     */
    private ApiResponse<PromedioDepartamentalResponse> respuestaSinResultados() {
        return new ApiResponse<>(200, "No se encontraron consolidados.", new PromedioDepartamentalResponse(List.of()));
    }
}
