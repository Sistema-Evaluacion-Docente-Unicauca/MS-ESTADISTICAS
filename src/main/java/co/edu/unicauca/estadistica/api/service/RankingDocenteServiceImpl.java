package co.edu.unicauca.estadistica.api.service;

import co.edu.unicauca.estadistica.api.client.ConsolidadoClient;
import co.edu.unicauca.estadistica.api.client.PeriodoAcademicoClient;
import co.edu.unicauca.estadistica.api.dto.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingDocenteServiceImpl implements RankingDocenteService {

    private final ConsolidadoClient consolidadoClient;
    private final PeriodoAcademicoClient periodoAcademicoClient;

    private static final Logger logger = LoggerFactory.getLogger(RankingDocenteServiceImpl.class);
    @Override
    public ApiResponse<List<PeriodoDocenteDTO>> obtenerRankingDocentes(String periodos, String departamentos, String token) {
        List<Integer> periodosFiltrados = obtenerPeriodos(periodos, token);
        if (periodosFiltrados == null || periodosFiltrados.isEmpty()) {
            return new ApiResponse<>(400, "Debe especificar al menos un periodo académico válido.", List.of());
        }
    
        List<String> departamentosFiltrados = parsearComas(departamentos);
    
        List<ConsolidadoDTO> consolidados = consolidadoClient.obtenerTodosConsolidados(token);
    
        if (consolidados == null || consolidados.isEmpty()) {
            return new ApiResponse<>(500, "No se pudo obtener la información de consolidados o no se encontraron datos.", null);
        }
    
        List<ConsolidadoDTO> filtrados = aplicarFiltros(consolidados, periodosFiltrados, departamentosFiltrados);
        List<PeriodoDocenteDTO> resultado = construirRanking(filtrados);
    
        return new ApiResponse<>(200, "Ranking generado correctamente", resultado);
    }    

    private List<Integer> obtenerPeriodos(String periodos, String token) {
        if (periodos == null || periodos.isBlank()) {
            PeriodoAcademicoDTO activo = periodoAcademicoClient.obtenerPeriodoActivo(token);
            if (activo != null) {
                logger.info("\uD83D\uDFE2 Usando periodo académico activo: {} (ID={})", activo.getIdPeriodo(), activo.getOidPeriodoAcademico());
                return List.of(activo.getOidPeriodoAcademico());
            }
            logger.warn("⚠️ No se encontró periodo académico activo.");
            return null;
        }
        return parsearEnteros(periodos);
    }

    private List<ConsolidadoDTO> aplicarFiltros(List<ConsolidadoDTO> datos, List<Integer> periodos, List<String> departamentos) {
        return datos.stream()
            .filter(c -> periodos.contains(c.getIdPeriodoAcademico()))
            .filter(c -> departamentos.isEmpty() || departamentos.stream().anyMatch(dep -> dep.equalsIgnoreCase(c.getDepartamento())))
            .toList();
    }

    private List<PeriodoDocenteDTO> construirRanking(List<ConsolidadoDTO> consolidados) {
        Map<Integer, Map<String, List<ConsolidadoDTO>>> agrupado = consolidados.stream()
                .filter(c -> c.getIdPeriodoAcademico() != null && c.getDepartamento() != null)
                .collect(Collectors.groupingBy(
                    ConsolidadoDTO::getIdPeriodoAcademico,
                    Collectors.groupingBy(ConsolidadoDTO::getDepartamento)
                ));

        return agrupado.entrySet().stream()
            .map((Map.Entry<Integer, Map<String, List<ConsolidadoDTO>>> periodoEntry) -> {
                Integer idPeriodo = periodoEntry.getKey();
                Map<String, List<ConsolidadoDTO>> departamentos = periodoEntry.getValue();
                String nombrePeriodo = obtenerNombrePeriodo(departamentos);

                List<DepartamentoDocenteDTO> listaDepartamentos = departamentos.entrySet().stream()
                    .map(depEntry -> {
                        String nombreDepto = depEntry.getKey();
                        List<DocenteRankingDTO> docentes = depEntry.getValue().stream()
                            .map(this::mapearDocente)
                            .sorted(Comparator.comparing(DocenteRankingDTO::getCalificacion).reversed())
                            .toList();
                        return new DepartamentoDocenteDTO(nombreDepto, docentes);
                    }).toList();

                return new PeriodoDocenteDTO(idPeriodo, nombrePeriodo, listaDepartamentos);
            })
            .toList();
    }

    private DocenteRankingDTO mapearDocente(ConsolidadoDTO c) {
        return new DocenteRankingDTO(
                c.getOidUsuario(),
                c.getNombreDocente(),
                c.getNumeroIdentificacion(),
                c.getCalificacion()
        );
    }

    private String obtenerNombrePeriodo(Map<String, List<ConsolidadoDTO>> grupo) {
        return grupo.values().stream()
                .flatMap(Collection::stream)
                .map(ConsolidadoDTO::getPeriodoAcademico)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("DESCONOCIDO");
    }

    private List<Integer> parsearEnteros(String input) {
        if (input == null || input.isBlank()) return List.of();
        try {
            return Arrays.stream(input.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .map(Integer::parseInt)
                    .toList();
        } catch (NumberFormatException e) {
            logger.error("❌ Error al parsear enteros desde '{}': {}", input, e.getMessage());
            return List.of();
        }
    }

    private List<String> parsearComas(String input) {
        if (input == null || input.isBlank()) return List.of();
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }
}
