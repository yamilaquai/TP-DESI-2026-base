package tuti.desi.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tuti.desi.entidades.Propiedad;
import tuti.desi.entidades.Publicacion;
import tuti.desi.enums.EstadoDisponibilidad;
import tuti.desi.enums.EstadoPublicacion;
import tuti.desi.persistencia.propiedadPersistencia;
import tuti.desi.persistencia.publicacionPersistencia;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class publicacionServicios {

    @Autowired private publicacionPersistencia publicacionRepo;
    @Autowired private propiedadPersistencia   propiedadRepo;

    // ── HU 2.1: Alta ──────────────────────────────────────────────────────────

    public Publicacion crear(Publicacion publicacion) {
        Long idPropiedad = publicacion.getPropiedad().getId();

        Propiedad propiedad = propiedadRepo.findById(idPropiedad)
            .orElseThrow(() -> new IllegalArgumentException("La propiedad seleccionada no existe."));

        if (propiedad.isEliminada()) {
            throw new IllegalArgumentException("No se puede publicar una propiedad eliminada.");
        }
        if (propiedad.getEstadoDisp() != EstadoDisponibilidad.DISPONIBLE) {
            throw new IllegalArgumentException(
                "Solo se pueden publicar propiedades en estado DISPONIBLE. " +
                "Estado actual: " + propiedad.getEstadoDisp().getDescripcion() + ".");
        }

        // Regla: no más de una publicación ACTIVA por propiedad
        if (publicacionRepo.existeActivaParaPropiedad(idPropiedad, null)) {
            throw new IllegalArgumentException(
                "Ya existe una publicación activa para la propiedad: " +
                propiedad.getDireccionCompleta() + ". " +
                "No puede haber más de una publicación activa por propiedad.");
        }

        validarDatos(publicacion);

        publicacion.setPropiedad(propiedad);
        publicacion.setEliminado(false);
        if (publicacion.getEstado() == null) {
            publicacion.setEstado(EstadoPublicacion.ACTIVA);
        }

        // Registrar historial inicial via método de la entidad
        publicacion.cambiarEstado(publicacion.getEstado());

        return publicacionRepo.save(publicacion);
    }

    // ── HU 2.2: Eliminación lógica ────────────────────────────────────────────

    public void eliminar(Long id) {
        Publicacion pub = obtenerOException(id);

        // Solo se pueden eliminar publicaciones ACTIVAS
        if (pub.getEstado() != EstadoPublicacion.ACTIVA) {
            throw new IllegalStateException(
                "Solo se pueden eliminar publicaciones en estado ACTIVA. " +
                "La publicación #" + id + " se encuentra en estado: " +
                pub.getEstado().getDescripcion() + ". " +
                "Si desea dejar de ofrecerla, pause o finalice la publicación.");
        }

        pub.setEliminado(true);
        publicacionRepo.save(pub);
    }

    // ── HU 2.3: Modificación ─────────────────────────────────────────────────

    public Publicacion modificar(Long id, Publicacion datosNuevos) {
        Publicacion existente = obtenerOException(id);

        EstadoPublicacion estadoAnterior = existente.getEstado();
        EstadoPublicacion estadoNuevo    = datosNuevos.getEstado();

        // Condiciones de alquiler: no modificables si está FINALIZADA
        if (existente.getEstado() == EstadoPublicacion.FINALIZADA) {
            if (datosNuevos.getCondiciones() != null &&
                !datosNuevos.getCondiciones().equals(existente.getCondiciones())) {
                throw new IllegalStateException(
                    "No se pueden modificar las condiciones de alquiler de una publicación FINALIZADA.");
            }
        }

        // Validaciones al intentar activar
        if (estadoNuevo == EstadoPublicacion.ACTIVA && estadoAnterior != EstadoPublicacion.ACTIVA) {

            if (publicacionRepo.existeActivaParaPropiedad(existente.getPropiedad().getId(), id)) {
                throw new IllegalStateException(
                    "No se puede activar: ya existe otra publicación activa para esta propiedad.");
            }
            if (existente.getPropiedad().getEstadoDisp() != EstadoDisponibilidad.DISPONIBLE) {
                throw new IllegalStateException(
                    "Solo se puede activar una publicación si la propiedad está en estado DISPONIBLE. " +
                    "Estado actual: " + existente.getPropiedad().getEstadoDisp().getDescripcion() + ".");
            }
        }

        // Actualizar campos (propiedad NO se cambia - solo lectura)
        existente.setPrecioMensual(datosNuevos.getPrecioMensual());
        existente.setCondiciones(datosNuevos.getCondiciones());
        existente.setDescripcion(datosNuevos.getDescripcion());
        existente.setFechaPublicacion(datosNuevos.getFechaPublicacion());

        validarDatos(existente);

        // Cambio de estado con historial
        if (!estadoAnterior.equals(estadoNuevo)) {
            existente.cambiarEstado(estadoNuevo);
        }

        return publicacionRepo.save(existente);
    }

    // ── HU 2.4: Listado ───────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Publicacion> listarTodas() {
        return publicacionRepo.findByEliminadoFalse();
    }

    @Transactional(readOnly = true)
    public List<Publicacion> listarConFiltros(Long propiedadId, String ciudad,
                                               EstadoPublicacion estado,
                                               BigDecimal precioMin, BigDecimal precioMax) {
        String ciudadFiltro = (ciudad != null && ciudad.isBlank()) ? null : ciudad;
        return publicacionRepo.buscarConFiltros(propiedadId, ciudadFiltro, estado, precioMin, precioMax);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Publicacion obtenerOException(Long id) {
        return publicacionRepo.findById(id)
            .filter(p -> !p.isEliminado())
            .orElseThrow(() -> new IllegalArgumentException("No se encontró la publicación con id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Propiedad> obtenerPropiedadesDisponibles() {
        return propiedadRepo.buscarDisponibles();
    }

    @Transactional(readOnly = true)
    public List<Propiedad> obtenerTodasPropiedades() {
        return propiedadRepo.buscarTodasActivas();
    }

    private void validarDatos(Publicacion p) {
        if (p.getPrecioMensual() == null || p.getPrecioMensual().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("El precio mensual debe ser un número positivo.");
        if (p.getFechaPublicacion() == null)
            throw new IllegalArgumentException("La fecha de publicación es requerida.");
        if (p.getCondiciones() == null || p.getCondiciones().isBlank())
            throw new IllegalArgumentException("Las condiciones de alquiler son requeridas.");
        if (p.getDescripcion() == null || p.getDescripcion().isBlank())
            throw new IllegalArgumentException("La descripción es requerida.");
        if (p.getEstado() == null)
            throw new IllegalArgumentException("El estado de la publicación es requerido.");
    }
}
