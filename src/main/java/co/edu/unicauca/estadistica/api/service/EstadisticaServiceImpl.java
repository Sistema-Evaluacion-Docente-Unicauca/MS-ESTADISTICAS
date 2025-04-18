package co.edu.unicauca.estadistica.api.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import co.edu.unicauca.estadistica.api.client.ActividadDocenteClient;
import co.edu.unicauca.estadistica.api.client.TipoActividadClient;
import co.edu.unicauca.estadistica.api.client.UsuarioDocenteClient;
import co.edu.unicauca.estadistica.api.dto.ActividadDTO;
import co.edu.unicauca.estadistica.api.dto.ActividadEvaluadaDTO;
import co.edu.unicauca.estadistica.api.dto.ActividadEvaluadaWrapper;
import co.edu.unicauca.estadistica.api.dto.ApiResponse;
import co.edu.unicauca.estadistica.api.dto.AtributoDTO;
import co.edu.unicauca.estadistica.api.dto.ComparacionActividadDTO;
import co.edu.unicauca.estadistica.api.dto.EvaluacionDepartamentoDTO;
import co.edu.unicauca.estadistica.api.dto.FuenteDTO;
import co.edu.unicauca.estadistica.api.dto.TipoActividadDTO;
import co.edu.unicauca.estadistica.api.dto.TipoActividadEvaluadaDTO;
import co.edu.unicauca.estadistica.api.dto.UsuarioDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class EstadisticaServiceImpl implements EstadisticaService {

    private final ActividadDocenteClient actividadClient;
    private final UsuarioDocenteClient usuarioClient;
    private final TipoActividadClient tipoActividadClient;

    private static final Logger logger = LoggerFactory.getLogger(EstadisticaServiceImpl.class);

    @Override
    public ApiResponse<ComparacionActividadDTO> obtenerComparacionPorDocente(Integer idEvaluado, Integer idPeriodo, String idTipoActividad) {
        if (idEvaluado == null) {
            return new ApiResponse<>(400, "El parámetro idEvaluado es obligatorio.", null);
        }

        List<Integer> tipoActividadIds = parsearIds(idTipoActividad);
        if (tipoActividadIds == null) {
            return new ApiResponse<>(400, "Los valores de tipo de actividad deben ser numéricos válidos.", null);
        }

        List<ActividadDTO> actividades = actividadClient.obtenerActividadesPorEvaluado(idEvaluado, idPeriodo);
        logger.debug("📦 Actividades recibidas para idEvaluado {}: {}", idEvaluado, actividades.size());

        if (actividades.isEmpty()) {
            return new ApiResponse<>(404, "No se encontraron actividades para el docente", null);
        }

        UsuarioDTO docente = usuarioClient.obtenerUsuarioPorId(idEvaluado);
        if (docente == null) {
            return new ApiResponse<>(404, "Docente no encontrado", null);
        }

        actividades = filtrarPorTipoActividad(actividades, tipoActividadIds);
        List<ActividadEvaluadaWrapper> wrappers = construirWrappers(actividades, docente);

        Map<String, Map<String, List<ActividadEvaluadaDTO>>> agrupado = agruparPorDepartamentoYTipo(wrappers);
        List<EvaluacionDepartamentoDTO> evaluaciones = construirRespuestaAgrupada(agrupado);

        return new ApiResponse<>(200, "Comparación generada correctamente",
                new ComparacionActividadDTO(docente, evaluaciones));
    }

    private List<Integer> parsearIds(String idTipoActividad) {
        if (idTipoActividad == null || idTipoActividad.isBlank())
            return List.of();

        try {
            return Arrays.stream(idTipoActividad.split(","))
                    .map(String::trim)
                    .filter(p -> !p.isEmpty())
                    .map(Integer::parseInt)
                    .toList();
        } catch (NumberFormatException e) {
            logger.error("❌ Error al parsear tipo de actividad: {}", e.getMessage());
            return null;
        }
    }

    private List<ActividadDTO> filtrarPorTipoActividad(List<ActividadDTO> actividades, List<Integer> idTipoActividad) {
        if (idTipoActividad == null || idTipoActividad.isEmpty()) {
            return actividades; // No se filtró por tipo, retorna todas
        }
    
        List<TipoActividadDTO> tiposDisponibles = tipoActividadClient.obtenerTipoActividad();
        
        Set<Integer> tiposValidos = tiposDisponibles.stream()
                .map(TipoActividadDTO::getOidTipoActividad)
                .filter(idTipoActividad::contains)
                .collect(Collectors.toSet());

    
        if (tiposValidos.isEmpty()) {
            logger.warn("⚠️ Ningún tipo de actividad válido encontrado en el catálogo.");
            return List.of(); // No hay coincidencias válidas
        }
    
        List<ActividadDTO> filtradas = actividades.stream()
                .filter(a -> a.getTipoActividad() != null &&
                             tiposValidos.contains(a.getTipoActividad().getOidTipoActividad()))
                .toList();
    
        return filtradas;
    }

    private List<ActividadEvaluadaWrapper> construirWrappers(List<ActividadDTO> actividades, UsuarioDTO docente) {
        return actividades.stream()
                .map(act -> construirWrapper(act, docente))
                .filter(Objects::nonNull)
                .toList();
    }

    private ActividadEvaluadaWrapper construirWrapper(ActividadDTO act, UsuarioDTO docente) {
        String departamento = docente.getUsuarioDetalle() != null
                ? docente.getUsuarioDetalle().getDepartamento()
                : null;

        String tipoActividad = act.getTipoActividad() != null
                ? act.getTipoActividad().getNombre()
                : null;

        if (departamento == null || tipoActividad == null) {
            logger.warn("❗ Actividad omitida (departamento o tipoActividad nulo). ID: {}", act.getOidActividad());
            return null;
        }

        String nombreActividad = act.getAtributos() != null
                ? act.getAtributos().stream()
                        .filter(attr -> "ACTIVIDAD".equals(attr.getCodigoAtributo()))
                        .map(AtributoDTO::getValor).findFirst()
                        .orElse(act.getNombreActividad())
                : act.getNombreActividad();

        if (nombreActividad == null) {
            logger.warn("❌ Actividad sin nombre. ID: {}", act.getOidActividad());
            return null;
        }

        Double fuente1 = obtenerCalificacionPorTipoFuente(act, "1");
        Double fuente2 = obtenerCalificacionPorTipoFuente(act, "2");

        return new ActividadEvaluadaWrapper(
                departamento,
                tipoActividad,
                new ActividadEvaluadaDTO(nombreActividad, fuente1, fuente2));
    }

    private Double obtenerCalificacionPorTipoFuente(ActividadDTO act, String tipoFuente) {
        return act.getFuentes().stream()
                .filter(f -> tipoFuente.equals(f.getTipoFuente()) && f.getCalificacion() != null)
                .map(FuenteDTO::getCalificacion)
                .findFirst()
                .orElse(null);
    }

    private Map<String, Map<String, List<ActividadEvaluadaDTO>>> agruparPorDepartamentoYTipo(
            List<ActividadEvaluadaWrapper> wrappers) {
        return wrappers.stream()
                .collect(Collectors.groupingBy(
                        ActividadEvaluadaWrapper::departamento,
                        Collectors.groupingBy(
                                ActividadEvaluadaWrapper::tipoActividad,
                                Collectors.mapping(ActividadEvaluadaWrapper::actividad, Collectors.toList()))));
    }

    private List<EvaluacionDepartamentoDTO> construirRespuestaAgrupada(
            Map<String, Map<String, List<ActividadEvaluadaDTO>>> agrupado) {
        return agrupado.entrySet().stream()
                .map(entry -> new EvaluacionDepartamentoDTO(
                        entry.getKey(),
                        entry.getValue().entrySet().stream()
                                .map(tipo -> new TipoActividadEvaluadaDTO(tipo.getKey(), tipo.getValue()))
                                .toList()))
                .toList();
    }
    
}
