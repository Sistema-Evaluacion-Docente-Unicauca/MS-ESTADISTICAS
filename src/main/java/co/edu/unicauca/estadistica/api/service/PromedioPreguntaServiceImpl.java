package co.edu.unicauca.estadistica.api.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import co.edu.unicauca.estadistica.api.client.EvaluacionDocenteClient;
import co.edu.unicauca.estadistica.api.client.PeriodoAcademicoClient;
import co.edu.unicauca.estadistica.api.dto.ActividadDepartamentoDTO;
import co.edu.unicauca.estadistica.api.dto.ApiResponse;
import co.edu.unicauca.estadistica.api.dto.DepartamentoRankingDTO;
import co.edu.unicauca.estadistica.api.dto.FuenteEvaluadaDTO;
import co.edu.unicauca.estadistica.api.dto.PeriodoAcademicoDTO;
import co.edu.unicauca.estadistica.api.dto.PeriodoEvaluacionDTO;
import co.edu.unicauca.estadistica.api.dto.PeriodoRankingDTO;
import co.edu.unicauca.estadistica.api.dto.PreguntaCalificadaDTO;
import co.edu.unicauca.estadistica.api.dto.PreguntaRankingDTO;
import co.edu.unicauca.estadistica.api.dto.TipoActividadFuentesDTO;
import co.edu.unicauca.estadistica.api.dto.TipoActividadRankingDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PromedioPreguntaServiceImpl implements PromedioPreguntaService {

    private final EvaluacionDocenteClient consolidadoClient;
    private final PeriodoAcademicoClient periodoAcademicoClient;
    private static final Logger logger = LoggerFactory.getLogger(PromedioDepartamentoServiceImpl.class);

    @Override
    public ApiResponse<List<PeriodoRankingDTO>> obtenerRankingPreguntas(String periodos, String departamentos, String tiposActividad, String token) {

        // Obtener lista completa desde sed-docente
        List<PeriodoEvaluacionDTO> periodosEvaluacion = consolidadoClient.obtenerEvaluacionesEstructuradas(token);

        // 🧠 Procesar periodos
        List<Integer> periodosFiltrados;
        if (periodos == null || periodos.isBlank()) {
            PeriodoAcademicoDTO activo = periodoAcademicoClient.obtenerPeriodoActivo(token);
            if (activo == null) {
                return new ApiResponse<>(404, "No se pudo obtener el período académico activo", List.of());
            }
            periodosFiltrados = List.of(activo.getOidPeriodoAcademico());
        } else {
            try {
                periodosFiltrados = Arrays.stream(periodos.split(","))
                        .map(String::trim)
                        .filter(p -> !p.isEmpty())
                        .map(Integer::parseInt)
                        .toList();
            } catch (NumberFormatException e) {
                return new ApiResponse<>(400, "Los valores de periodo deben ser numéricos válidos", List.of());
            }
        }

        // 🧠 Procesar departamentos
        List<String> departamentosFiltrados = departamentos != null && !departamentos.isBlank()
                ? Arrays.stream(departamentos.split(",")).map(String::trim).toList()
                : List.of();

        // 🧠 Procesar tipos de actividad
        List<Integer> tiposActividadFiltrados = tiposActividad != null && !tiposActividad.isBlank()
                ? Arrays.stream(tiposActividad.split(",")).map(String::trim).map(Integer::parseInt).toList()
                : List.of();

        // 🧮 Resultado final
        List<PeriodoRankingDTO> resultado = new ArrayList<>();

        for (PeriodoEvaluacionDTO periodo : periodosEvaluacion) {
            if (!periodosFiltrados.contains(periodo.getOidPeriodo()))
                continue;

            List<DepartamentoRankingDTO> departamentosDTO = new ArrayList<>();

            for (ActividadDepartamentoDTO depto : periodo.getDepartamentos()) {
                if (!departamentosFiltrados.isEmpty() &&
                        departamentosFiltrados.stream().noneMatch(d -> d.equalsIgnoreCase(depto.getDepartamento()))) {
                    continue;
                }

                List<TipoActividadRankingDTO> tiposDTO = new ArrayList<>();

                for (TipoActividadFuentesDTO tipo : depto.getTiposActividad()) {
                    if (!tiposActividadFiltrados.isEmpty() &&
                            !tiposActividadFiltrados.contains(tipo.getOidTipoActividad())) {
                        continue;
                    }

                    // Agrupación por pregunta
                    Map<Integer, List<Float>> calificacionesPorPregunta = new HashMap<>();
                    Map<Integer, String> textosPorPregunta = new HashMap<>();

                    for (FuenteEvaluadaDTO fuente : tipo.getFuentes()) {
                        for (PreguntaCalificadaDTO pregunta : fuente.getPreguntas()) {
                            calificacionesPorPregunta
                                    .computeIfAbsent(pregunta.getOidPregunta(), k -> new ArrayList<>())
                                    .add(pregunta.getCalificacion());
                            textosPorPregunta.putIfAbsent(pregunta.getOidPregunta(), pregunta.getTexto());
                        }
                    }

                    // Construir ranking por tipo de actividad
                    List<PreguntaRankingDTO> ranking = calificacionesPorPregunta.entrySet().stream()
                            .map(entry -> new PreguntaRankingDTO(
                                    entry.getKey(),
                                    textosPorPregunta.get(entry.getKey()),
                                    (float) entry.getValue().stream().mapToDouble(Float::doubleValue).average()
                                            .orElse(0.0)))
                            .sorted(Comparator.comparing(PreguntaRankingDTO::getPromedio).reversed())
                            .toList();

                    tiposDTO.add(new TipoActividadRankingDTO(
                            tipo.getOidTipoActividad(),
                            tipo.getNombre(),
                            ranking));
                }

                if (!tiposDTO.isEmpty()) {
                    departamentosDTO.add(new DepartamentoRankingDTO(depto.getDepartamento(), tiposDTO));
                }
            }

            if (!departamentosDTO.isEmpty()) {
                resultado.add(new PeriodoRankingDTO(periodo.getOidPeriodo(), periodo.getNombre(), departamentosDTO));
            }
        }

        return new ApiResponse<>(200, "Ranking de preguntas generado correctamente", resultado);
    }

}
