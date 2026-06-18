package tuti.desi.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tuti.desi.entidades.Publicacion;
import tuti.desi.enums.EstadoPublicacion;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface publicacionPersistencia extends JpaRepository<Publicacion, Long> {

    /** Listado base: no eliminadas */
    List<Publicacion> findByEliminadoFalse();

    /** Publicaciones activas de una propiedad (regla: solo 1 activa por propiedad) */
    @Query("SELECT p FROM Publicacion p WHERE p.propiedad.id = :propId " +
           "AND p.estado = 'ACTIVA' AND p.eliminado = false")
    List<Publicacion> findActivasByPropiedad(@Param("propId") Long propiedadId);

    /**
     * Verifica si existe una publicación ACTIVA para la propiedad,
     * excluyendo opcionalmente la publicación con el id dado (para edición).
     */
    @Query("SELECT COUNT(p) > 0 FROM Publicacion p " +
           "WHERE p.propiedad.id = :propId " +
           "AND p.estado = 'ACTIVA' " +
           "AND p.eliminado = false " +
           "AND (:excludeId IS NULL OR p.id <> :excludeId)")
    boolean existeActivaParaPropiedad(@Param("propId") Long propiedadId,
                                      @Param("excludeId") Long excludeId);

    /**
     * Listado con filtros opcionales (HU 2.4).
     * Todos los parámetros son opcionales; pasar null para ignorar el filtro.
     */
    @Query("SELECT p FROM Publicacion p " +
           "WHERE p.eliminado = false " +
           "AND (:propId   IS NULL OR p.propiedad.id = :propId) " +
           "AND (:ciudad   IS NULL OR LOWER(p.propiedad.ciudad) LIKE LOWER(CONCAT('%', :ciudad, '%'))) " +
           "AND (:estado   IS NULL OR p.estado = :estado) " +
           "AND (:precioMin IS NULL OR p.precioMensual >= :precioMin) " +
           "AND (:precioMax IS NULL OR p.precioMensual <= :precioMax)")
    List<Publicacion> buscarConFiltros(@Param("propId")    Long propiedadId,
                                       @Param("ciudad")    String ciudad,
                                       @Param("estado")    EstadoPublicacion estado,
                                       @Param("precioMin") BigDecimal precioMin,
                                       @Param("precioMax") BigDecimal precioMax);
}
