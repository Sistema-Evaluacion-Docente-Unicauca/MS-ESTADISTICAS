package co.edu.unicauca.estadistica.api.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import co.edu.unicauca.estadistica.api.client.ConsolidadoClient;
import co.edu.unicauca.estadistica.api.dto.ApiResponse;
import co.edu.unicauca.estadistica.api.dto.ConsolidadoDTO;
import co.edu.unicauca.estadistica.api.dto.EvolucionDepartamentoDTO;
import co.edu.unicauca.estadistica.api.dto.EvolucionPeriodoDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class EvolucionDepartamentoServiceImpl implements EvolucionDepartamentoService {

    private final ConsolidadoClient consolidadoClient;
    private static final Logger logger = LoggerFactory.getLogger(EvolucionDepartamentoServiceImpl.class);

    @Override
    public ApiResponse<List<EvolucionDepartamentoDTO>> obtenerEvolucionPromedios(List<Integer> periodos, String nombreDepartamento) {
        List<ConsolidadoDTO> consolidados = consolidadoClient.obtenerTodosConsolidados();
    
        if (consolidados.isEmpty()) {
            logger.warn("⚠️ No se encontraron consolidados.");
            return new ApiResponse<>(200, "No se encontraron consolidados.", List.of());
        }
    
        // Filtrar consolidados por periodos, nombre de departamento (si aplica), y que tengan datos válidos
        List<ConsolidadoDTO> filtrados = consolidados.stream()
            .filter(c -> c.getDepartamento() != null && c.getCalificacion() != null && c.getPeriodoAcademico() != null)
            .filter(c -> periodos.contains(c.getIdPeriodoAcademico()))
            .filter(c -> nombreDepartamento == null || c.getDepartamento().equalsIgnoreCase(nombreDepartamento))
            .toList();
    
        // Agrupar por departamento y dentro de eso por periodo, calculando promedio
        Map<String, Map<String, Double>> agrupado = filtrados.stream()
            .collect(Collectors.groupingBy(
                ConsolidadoDTO::getDepartamento,
                Collectors.groupingBy(
                    ConsolidadoDTO::getPeriodoAcademico,
                    Collectors.averagingDouble(ConsolidadoDTO::getCalificacion)
                )
            ));
    
        // Construcción del DTO final
        List<EvolucionDepartamentoDTO> resultado = agrupado.entrySet().stream()
            .map(entry -> new EvolucionDepartamentoDTO(
                entry.getKey(),
                entry.getValue().entrySet().stream()
                    .map(e -> new EvolucionPeriodoDTO(e.getKey(), Math.round(e.getValue() * 100.0) / 100.0))
                    .sorted(Comparator.comparing(EvolucionPeriodoDTO::getPeriodo))
                    .toList()
            ))
            .sorted(Comparator.comparing(EvolucionDepartamentoDTO::getDepartamento))
            .toList();
    
        return new ApiResponse<>(200, "Evolución generada correctamente", resultado);
    }    
}
