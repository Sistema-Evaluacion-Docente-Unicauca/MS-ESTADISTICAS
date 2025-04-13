package co.edu.unicauca.estadistica.api.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicauca.estadistica.api.dto.ApiResponse;
import co.edu.unicauca.estadistica.api.dto.EvolucionDepartamentoDTO;
import co.edu.unicauca.estadistica.api.service.EvolucionDepartamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/estadisticas")
@RequiredArgsConstructor
@Tag(name = "Estadísticas")
public class EvolucionDepartamentoController {

    private final EvolucionDepartamentoService evolucionService;

    @Operation(summary = "Evolución de promedio por departamento", description = "Retorna la evolución del promedio de consolidados agrupado por departamento para los periodos indicados.")
    @GetMapping("/evolucion-promedio")
    public ResponseEntity<ApiResponse<List<EvolucionDepartamentoDTO>>> obtenerEvolucion(
        @RequestParam String periodos,
        @RequestParam(required = false) Integer idDepartamento) {

        List<Integer> periodosAcademicos = Arrays.stream(periodos.split(","))
                                .map(Integer::parseInt)
                                .collect(Collectors.toList());

        ApiResponse<List<EvolucionDepartamentoDTO>> response = evolucionService.obtenerEvolucionPromedios(periodosAcademicos, idDepartamento);

        return ResponseEntity.status(response.getCodigo()).body(response);
    }
}