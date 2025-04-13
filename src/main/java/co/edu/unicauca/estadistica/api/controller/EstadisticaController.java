package co.edu.unicauca.estadistica.api.controller;

import co.edu.unicauca.estadistica.api.dto.ApiResponse;
import co.edu.unicauca.estadistica.api.dto.ComparacionActividadDTO;
import co.edu.unicauca.estadistica.api.service.EstadisticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Estadísticas")
@RestController
@RequestMapping("/api/estadisticas")
@RequiredArgsConstructor
public class EstadisticaController {

    private final EstadisticaService estadisticaService;

    /**
     * Endpoint para obtener la comparación de evaluaciones por actividad para un docente.
     *
     * @param idEvaluado ID del docente evaluado (obligatorio)
     * @param idPeriodo ID del periodo académico (opcional, por defecto se usa el activo)
     * @return ApiResponse con la estructura ComparacionActividadDTO
     */
    @Operation(
        summary = "Comparación de Evaluaciones por Actividad",
        description = "Agrupa actividades evaluadas por fuente, tipo de actividad y departamento para un docente."
    )
    @GetMapping("/comparacion-fuente")
    public ResponseEntity<ApiResponse<ComparacionActividadDTO>> obtenerComparacion(
        @RequestParam("idEvaluado") Integer idEvaluado,
        @RequestParam(value = "idPeriodo", required = false) Integer idPeriodo,
        @RequestParam(value = "idTipoActividad", required = false) Integer idTipoActividad
    ) {
        ApiResponse<ComparacionActividadDTO> response =
            estadisticaService.obtenerComparacionPorDocente(idEvaluado, idPeriodo, idTipoActividad);
        return ResponseEntity.status(response.getCodigo()).body(response);
    }
}
