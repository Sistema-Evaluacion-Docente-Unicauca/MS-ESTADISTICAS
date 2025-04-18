package co.edu.unicauca.estadistica.api.controller;

import co.edu.unicauca.estadistica.api.dto.ApiResponse;
import co.edu.unicauca.estadistica.api.dto.ComparacionActividadDTO;
import co.edu.unicauca.estadistica.api.dto.EvolucionDepartamentoDTO;
import co.edu.unicauca.estadistica.api.dto.PeriodoDocenteDTO;
import co.edu.unicauca.estadistica.api.dto.PeriodoRankingDTO;
import co.edu.unicauca.estadistica.api.dto.PromedioDepartamentalResponse;
import co.edu.unicauca.estadistica.api.service.EstadisticaService;
import co.edu.unicauca.estadistica.api.service.EvolucionDepartamentoService;
import co.edu.unicauca.estadistica.api.service.PromedioDepartamentoService;
import co.edu.unicauca.estadistica.api.service.PromedioPreguntaService;
import co.edu.unicauca.estadistica.api.service.RankingDocenteService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Estadísticas", description = "Operaciones para análisis de evaluación docente por fuente, departamento, actividad y pregunta.")
@RestController
@RequestMapping("/api/estadisticas")
@RequiredArgsConstructor
public class EstadisticaController {

    private final EstadisticaService estadisticaService;
    private final PromedioPreguntaService promedioPreguntaService;
    private final EvolucionDepartamentoService evolucionService;
    private final PromedioDepartamentoService promedioDepartamentoService;
    private final RankingDocenteService rankingDocenteService;

    @Operation(
        summary = "Comparación de Evaluaciones por Actividad",
        description = "Agrupa actividades evaluadas por fuente, tipo de actividad y departamento para un docente específico."
    )
    @GetMapping("/comparacion-fuente")
    public ResponseEntity<ApiResponse<ComparacionActividadDTO>> obtenerComparacion(
            @Parameter(description = "ID del docente evaluado", required = true)
            @RequestParam(value = "idEvaluado", required = false) Integer idEvaluado,
    
            @Parameter(description = "ID del periodo académico. Opcional. Si no se envía, se usa el periodo activo.")
            @RequestParam(value = "idPeriodo", required = false) Integer idPeriodo,
    
            @Parameter(description = "Lista de IDs de tipo de actividad separados por comas. Opcional.")
            @RequestParam(value = "idTipoActividad", required = false) String idTipoActividad) {
    
        ApiResponse<ComparacionActividadDTO> response = estadisticaService.obtenerComparacionPorDocente(idEvaluado, idPeriodo, idTipoActividad);
    
        return ResponseEntity.status(response.getCodigo()).body(response);
    }
    

    @Operation(
        summary = "Evolución de Promedio por Departamento",
        description = "Calcula la evolución del promedio de evaluación por departamento para uno o más periodos académicos."
    )
    @GetMapping("/evolucion-promedio")
    public ResponseEntity<ApiResponse<List<EvolucionDepartamentoDTO>>> obtenerEvolucion(
            @Parameter(description = "Lista de IDs de periodo académico separados por comas. Si no se envía, se usa el activo.")
            @RequestParam(required = false) String periodos,

            @Parameter(description = "Lista de nombres de departamentos separados por comas. Opcional.")
            @RequestParam(required = false) String nombresDepartamentos) {

        ApiResponse<List<EvolucionDepartamentoDTO>> response =
            evolucionService.obtenerEvolucionPromedios(periodos, nombresDepartamentos);

        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    @Operation(
        summary = "Promedio de Evaluación por Departamento",
        description = "Calcula el promedio general de evaluación por departamento para un periodo específico. Si no se envía, se usa el activo."
    )
    @GetMapping("/promedio-departamento")
    public ResponseEntity<ApiResponse<PromedioDepartamentalResponse>> obtenerPromedios(
            @Parameter(description = "ID del periodo académico. Opcional, por defecto toma el periodo activo.")
            @RequestParam(value = "idPeriodo", required = false) Integer idPeriodo) {

        ApiResponse<PromedioDepartamentalResponse> response = promedioDepartamentoService.obtenerPromediosPorDepartamento(idPeriodo);

        return ResponseEntity.status(response.getCodigo()).body(response);
    }

    @Operation(
        summary = "Ranking de Preguntas por Promedio",
        description = "Genera un ranking de preguntas ordenadas por promedio de calificación, agrupadas por periodo, departamento y tipo de actividad."
    )
    @GetMapping("/ranking-preguntas")
    public ResponseEntity<ApiResponse<List<PeriodoRankingDTO>>> obtenerRankingPreguntas(
            @Parameter(description = "Lista de IDs de periodo académico separados por comas. Si no se envía, se usa el activo.")
            @RequestParam(required = false) String periodos,

            @Parameter(description = "Lista de nombres de departamentos separados por comas. Opcional.")
            @RequestParam(required = false) String departamentos,

            @Parameter(description = "Lista de IDs de tipo de actividad separados por comas. Opcional.")
            @RequestParam(required = false) String tiposActividad) {

        return ResponseEntity.ok(
            promedioPreguntaService.obtenerRankingPreguntas(periodos, departamentos, tiposActividad)
        );
    }

    @Operation(
        summary = "Ranking de Docentes por Calificación",
        description = "Retorna un listado agrupado por periodo, departamento y tipo de actividad, ordenado por calificación descendente."
    )
    @GetMapping("/ranking-docentes")
    public ResponseEntity<ApiResponse<List<PeriodoDocenteDTO>>> obtenerRankingDocentes(
            @Parameter(description = "Lista de IDs de periodo académico separados por comas. Si no se especifica, se usa el activo.")
            @RequestParam(required = false) String periodos,

            @Parameter(description = "Lista de nombres de departamentos separados por comas. Opcional.")
            @RequestParam(required = false) String departamentos) {

        ApiResponse<List<PeriodoDocenteDTO>> response = rankingDocenteService.obtenerRankingDocentes(periodos, departamentos);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }
}
