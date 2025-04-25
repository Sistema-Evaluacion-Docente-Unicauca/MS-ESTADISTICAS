package co.edu.unicauca.estadistica.api.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import co.edu.unicauca.estadistica.api.client.ConsolidadoClient;
import co.edu.unicauca.estadistica.api.client.PeriodoAcademicoClient;
import co.edu.unicauca.estadistica.api.dto.ApiResponse;
import co.edu.unicauca.estadistica.api.dto.ConsolidadoDTO;
import co.edu.unicauca.estadistica.api.dto.EvolucionDepartamentoDTO;
import co.edu.unicauca.estadistica.api.dto.EvolucionPeriodoDTO;
import co.edu.unicauca.estadistica.api.dto.PeriodoAcademicoDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class EvolucionDepartamentoServiceImpl implements EvolucionDepartamentoService {

    private final ConsolidadoClient consolidadoClient;
    private final PeriodoAcademicoClient periodoAcademicoClient;

    private static final Logger logger = LoggerFactory.getLogger(EvolucionDepartamentoServiceImpl.class);

    @Override
    public ApiResponse<List<EvolucionDepartamentoDTO>> obtenerEvolucionPromedios(String periodos, String nombresDepartamentos, String token) {
        List<Integer> periodosFiltrados = obtenerPeriodosValidos(periodos, token);
        if (periodosFiltrados == null) {
            return new ApiResponse<>(400, "Debe especificar al menos un período académico válido.", List.of());
        }

        List<String> departamentosFiltrados = parsearDepartamentos(nombresDepartamentos);
        return construirEvolucion(periodosFiltrados, departamentosFiltrados, token);
    }

    /**
     * Procesa los valores de entrada para extraer una lista de IDs de periodo válidos, o devuelve el activo si no se proporcionan explícitamente.
     */
    private List<Integer> obtenerPeriodosValidos(String periodos, String token) {
        if (periodos == null || periodos.isBlank()) {
            PeriodoAcademicoDTO activo = periodoAcademicoClient.obtenerPeriodoActivo(token);
            if (activo == null) {
                logger.warn("❌ No se pudo determinar el período académico activo");
                return null;
            }
            logger.info("🟢 Usando período académico activo: {} (ID={})", activo.getIdPeriodo(), activo.getOidPeriodoAcademico());
            return List.of(activo.getOidPeriodoAcademico());
        }

        try {
            return Arrays.stream(periodos.split(","))
                    .map(String::trim)
                    .filter(p -> !p.isEmpty())
                    .map(Integer::parseInt)
                    .toList();
        } catch (NumberFormatException e) {
            logger.error("❌ Error al convertir periodos: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Convierte el String separado por comas de departamentos en una lista filtrada.
     */
    private List<String> parsearDepartamentos(String nombresDepartamentos) {
        if (nombresDepartamentos == null || nombresDepartamentos.isBlank()) {
            return List.of();
        }

        return Arrays.stream(nombresDepartamentos.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    /**
     * Construye el DTO final de evolución, agrupando por departamento y periodo con promedio.
     */
    private ApiResponse<List<EvolucionDepartamentoDTO>> construirEvolucion(List<Integer> periodos, List<String> nombresDepartamentos, String token) {
        List<ConsolidadoDTO> consolidados = consolidadoClient.obtenerTodosConsolidados(token);

        if (consolidados.isEmpty()) {
            logger.warn("⚠️ No se encontraron consolidados.");
            return new ApiResponse<>(200, "No se encontraron consolidados.", List.of());
        }

        List<ConsolidadoDTO> filtrados = filtrarConsolidados(consolidados, periodos, nombresDepartamentos);
        Map<String, Map<String, Double>> agrupado = agruparPromedios(filtrados);

        List<EvolucionDepartamentoDTO> resultado = construirRespuestaDTO(agrupado);
        return new ApiResponse<>(200, "Evolución generada correctamente", resultado);
    }

    /**
     * Aplica los filtros de periodo y departamento.
     */
    private List<ConsolidadoDTO> filtrarConsolidados(List<ConsolidadoDTO> consolidados, List<Integer> periodos, List<String> nombresDepartamentos) {
        return consolidados.stream()
                .filter(c -> c.getDepartamento() != null && c.getCalificacion() != null && c.getPeriodoAcademico() != null)
                .filter(c -> periodos.contains(c.getIdPeriodoAcademico()))
                .filter(c -> nombresDepartamentos.isEmpty()
                        || nombresDepartamentos.stream().anyMatch(nombre -> nombre.equalsIgnoreCase(c.getDepartamento())))
                .toList();
    }

    /**
     * Agrupa por departamento y por periodo, promediando las calificaciones.
     */
    private Map<String, Map<String, Double>> agruparPromedios(List<ConsolidadoDTO> filtrados) {
        return filtrados.stream()
            .collect(Collectors.groupingBy(
                ConsolidadoDTO::getDepartamento,
                Collectors.groupingBy(
                        ConsolidadoDTO::getPeriodoAcademico,
                        Collectors.averagingDouble(ConsolidadoDTO::getCalificacion)
                )
            ));
    }

    /**
     * Construye la estructura final para respuesta.
     */
    private List<EvolucionDepartamentoDTO> construirRespuestaDTO(Map<String, Map<String, Double>> agrupado) {
        return agrupado.entrySet().stream()
            .map(entry -> new EvolucionDepartamentoDTO(
                entry.getKey(),
                entry.getValue().entrySet().stream()
                    .map(e -> new EvolucionPeriodoDTO(e.getKey(), Math.round(e.getValue() * 100.0) / 100.0))
                    .sorted(Comparator.comparing(EvolucionPeriodoDTO::getPeriodo))
                    .toList()
            ))
            .sorted(Comparator.comparing(EvolucionDepartamentoDTO::getDepartamento))
            .toList();
    }
}
