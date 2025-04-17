package co.edu.unicauca.estadistica.api.controller;

import co.edu.unicauca.estadistica.api.dto.ApiResponse;
import co.edu.unicauca.estadistica.api.dto.ComparacionActividadDTO;
import co.edu.unicauca.estadistica.api.dto.EvolucionDepartamentoDTO;
import co.edu.unicauca.estadistica.api.dto.PeriodoRankingDTO;
import co.edu.unicauca.estadistica.api.dto.PromedioDepartamentalResponse;
import co.edu.unicauca.estadistica.api.service.EstadisticaService;
import co.edu.unicauca.estadistica.api.service.EvolucionDepartamentoService;
import co.edu.unicauca.estadistica.api.service.PromedioDepartamentoService;
import co.edu.unicauca.estadistica.api.service.PromedioPreguntaService;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Estadísticas")
@RestController
@RequestMapping("/api/estadisticas")
@RequiredArgsConstructor
public class EstadisticaController {

    private final EstadisticaService estadisticaService;

    private final PromedioPreguntaService promedioPreguntaService;

    private final EvolucionDepartamentoService evolucionService;

    private final PromedioDepartamentoService promedioDepartamentoService;

    /**
     * Endpoint para obtener la comparación de evaluaciones por actividad para un docente.
     *
     * @param idEvaluado ID del docente evaluado (obligatorio)
     * @param idPeriodo  ID del periodo académico (opcional, por defecto se usa el activo)
     * @return ApiResponse con la estructura ComparacionActividadDTO
     */
    @Operation(summary = "Comparación de Evaluaciones por Actividad", description = "Agrupa actividades evaluadas por fuente, tipo de actividad y departamento para un docente.")
    @GetMapping("/comparacion-fuente")
    public ResponseEntity<ApiResponse<ComparacionActividadDTO>> obtenerComparacion(
            @RequestParam(value = "idEvaluado", required = false) Integer idEvaluado,
            @RequestParam(value = "idPeriodo", required = false) Integer idPeriodo,
            @RequestParam(value = "idTipoActividad", required = false) String idTipoActividad) {
        if (idEvaluado == null) {
            ApiResponse<ComparacionActividadDTO> errorResponse = new ApiResponse<>(400,"El parámetro idEvaluado es obligatorio.", null);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        List<Integer> tipoActividad;
        try {
            tipoActividad = Arrays.stream(idTipoActividad.split(",")).map(String::trim).filter(p -> !p.isEmpty()).map(Integer::parseInt).collect(Collectors.toList());
        } catch (NumberFormatException e) {
            ApiResponse<ComparacionActividadDTO> error = new ApiResponse<>(400,"Los valores de periodo deben ser numéricos válidos.", null);
            return ResponseEntity.badRequest().body(error);
        }

        ApiResponse<ComparacionActividadDTO> response = estadisticaService.obtenerComparacionPorDocente(idEvaluado, idPeriodo, tipoActividad);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    @Operation(summary = "Evolución de promedio por departamento", description = "Retorna la evolución del promedio de consolidados agrupado por departamento para los periodos indicados.")
    @GetMapping("/evolucion-promedio")
    public ResponseEntity<ApiResponse<List<EvolucionDepartamentoDTO>>> obtenerEvolucion(
            @RequestParam(required = false) String periodos,
            @RequestParam(required = false) String nombresDepartamentos) {

        if (periodos == null || periodos.isBlank()) {
            ApiResponse<List<EvolucionDepartamentoDTO>> error = new ApiResponse<>(400,"Debe especificar al menos un periodo académico.", List.of());
            return ResponseEntity.badRequest().body(error);
        }

        List<Integer> periodosAcademicos;
        try {
            periodosAcademicos = Arrays.stream(periodos.split(",")).map(String::trim).filter(p -> !p.isEmpty()).map(Integer::parseInt).collect(Collectors.toList());
        } catch (NumberFormatException e) {
            ApiResponse<List<EvolucionDepartamentoDTO>> error = new ApiResponse<>(400,"Los valores de periodo deben ser numéricos válidos.", List.of());
            return ResponseEntity.badRequest().body(error);
        }

        // Procesar departamentos separados por coma
        List<String> departamentos = null;
        if (nombresDepartamentos != null && !nombresDepartamentos.isBlank()) {
            departamentos = Arrays.stream(nombresDepartamentos.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }



        if (periodosAcademicos.isEmpty()) {
            ApiResponse<List<EvolucionDepartamentoDTO>> error = new ApiResponse<>(400,"Debe especificar al menos un periodo académico válido.", List.of());
            return ResponseEntity.badRequest().body(error);
        }

        ApiResponse<List<EvolucionDepartamentoDTO>> response = evolucionService.obtenerEvolucionPromedios(periodosAcademicos, departamentos);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    @Operation(summary = "Promedio de Evaluación por Departamento", description = "Calcula el promedio de evaluación por departamento con base en los consolidados generados en un periodo académico. Si no se envía el ID del periodo, se toma el activo.")
    @GetMapping("/promedio-departamento")
    public ResponseEntity<ApiResponse<PromedioDepartamentalResponse>> obtenerPromedios(
            @Parameter(description = "ID del periodo académico. Opcional, por defecto toma el periodo activo")
            @RequestParam(value = "idPeriodo", required = false) Integer idPeriodo) {

        ApiResponse<PromedioDepartamentalResponse> response = promedioDepartamentoService.obtenerPromediosPorDepartamento(idPeriodo);

        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    /**
     * Consulta el promedio de calificaciones por pregunta, agrupadas por periodo,
     * departamento y tipo de actividad.
     *
     * @param idPeriodo          ID del periodo académico (obligatorio)
     * @param nombreDepartamento Nombre exacto del departamento (opcional)
     * @param idTipoActividad    ID del tipo de actividad (opcional)
     * @return Lista agrupada con promedio por pregunta
     */
    @GetMapping("/ranking-preguntas")
    public ResponseEntity<ApiResponse<List<PeriodoRankingDTO>>> obtenerRankingPreguntas(
            @RequestParam(required = false) String periodos,
            @RequestParam(required = false) String departamentos,
            @RequestParam(required = false) String tiposActividad) {
        return ResponseEntity.ok(promedioPreguntaService.obtenerRankingPreguntas(periodos, departamentos, tiposActividad));
    }
}
