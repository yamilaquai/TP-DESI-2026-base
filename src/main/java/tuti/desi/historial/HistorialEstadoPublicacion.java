package tuti.desi.historial;

import jakarta.persistence.*;
import tuti.desi.entidades.Publicacion;
import tuti.desi.enums.EstadoPublicacion;

import java.time.LocalDateTime;

@Entity
public class HistorialEstadoPublicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // AGREGADO: estado anterior (para trazabilidad completa)
    @Enumerated(EnumType.STRING)
    private EstadoPublicacion estadoAnterior;

    @Enumerated(EnumType.STRING)
    private EstadoPublicacion estadoNuevo;

    private LocalDateTime fechaHora;

    // AGREGADO: FK a Publicacion (faltaba en el original)
    @ManyToOne
    @JoinColumn(name = "publicacion_id")
    private Publicacion publicacion;

    public HistorialEstadoPublicacion() {}

    // Getters
    public long getId()                             { return id; }
    public EstadoPublicacion getEstadoAnterior()    { return estadoAnterior; }
    public EstadoPublicacion getEstadoNuevo()       { return estadoNuevo; }
    public LocalDateTime getFechaHora()             { return fechaHora; }
    public Publicacion getPublicacion()             { return publicacion; }

    // Setters
    public void setId(long id)                              { this.id = id; }
    public void setEstadoAnterior(EstadoPublicacion e)      { this.estadoAnterior = e; }
    public void setEstadoNuevo(EstadoPublicacion e)         { this.estadoNuevo = e; }
    public void setFechaHora(LocalDateTime f)               { this.fechaHora = f; }
    public void setPublicacion(Publicacion p)               { this.publicacion = p; }

    // --- Retrocompatibilidad con código original que usaba getEstado()/setEstado() ---
    /** @deprecated usar getEstadoNuevo() */
    @Deprecated public EstadoPublicacion getEstado()        { return estadoNuevo; }
    /** @deprecated usar setEstadoNuevo() */
    @Deprecated public void setEstado(EstadoPublicacion e)  { this.estadoNuevo = e; }
}
