package tuti.desi.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tuti.desi.entidades.Propiedad;
import tuti.desi.enums.EstadoDisponibilidad;

import java.util.List;

@Repository
public interface propiedadPersistencia extends JpaRepository<Propiedad, Long> {

    /** Todas las propiedades no eliminadas (para listados y filtros) */
    @Query("SELECT p FROM Propiedad p WHERE p.eliminada = false")
    List<Propiedad> buscarTodasActivas();

    /** Solo propiedades DISPONIBLES y no eliminadas (para el combo de alta de Publicacion) */
    @Query("SELECT p FROM Propiedad p WHERE p.eliminada = false AND p.estadoDisp = 'DISPONIBLE'")
    List<Propiedad> buscarDisponibles();

    /** Propiedades no eliminadas de un propietario */
    List<Propiedad> findByPropietarioIdAndEliminadaFalse(Long propietarioId);
}
