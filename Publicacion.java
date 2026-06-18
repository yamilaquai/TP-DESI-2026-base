package tuti.desi.entidades;

import jakarta.persistence.*;
import tuti.desi.enums.EstadoPublicacion;
import tuti.desi.historial.HistorialEstadoPublicacion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Publicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // AGREGADO: relación a Propiedad (faltaba en el original)
    @ManyToOne
    @JoinColumn(name = "propiedad_id", nullable = false)
    private Propiedad propiedad;

    private BigDecimal precioMensual;

    // Renombrado internamente para mayor claridad; getter/setter mantienen compatibilidad
    @Column(columnDefinition = "TEXT")
    private String condiciones;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private LocalDate fechaPublicacion;

    @Enumerated(EnumType.STRING)
    private EstadoPublicacion estado;

    private boolean eliminado;

    // AGREGADO: historial de cambios de estado (requerido en HU 2.1)
    @OneToMany(mappedBy = "publicacion", cascade = CascadeType.ALL)
    private List<HistorialEstadoPublicacion> historialEstados = new ArrayList<>();

    // Constructores
    public Publicacion() {
        this.estado           = EstadoPublicacion.ACTIVA;
        this.fechaPublicacion = LocalDate.now();
    }

    public Publicacion(BigDecimal precioMensual, String condiciones, LocalDate fechaPublicacion,
                       EstadoPublicacion estado, boolean eliminado, String descripcion) {
        this.precioMensual    = precioMensual;
        this.condiciones      = condiciones;
        this.fechaPublicacion = fechaPublicacion;
        this.estado           = estado;
        this.eliminado        = eliminado;
        this.descripcion      = descripcion;
    }

    // ── Método de negocio: cambia estado y registra historial ──────────────
    public void cambiarEstado(EstadoPublicacion nuevoEstado) {
        EstadoPublicacion anterior = this.estado;
        this.estado = nuevoEstado;

        HistorialEstadoPublicacion registro = new HistorialEstadoPublicacion();
        registro.setEstadoAnterior(anterior);
        registro.setEstadoNuevo(nuevoEstado);
        registro.setFechaHora(LocalDateTime.now());
        registro.setPublicacion(this);
        this.historialEstados.add(registro);
    }

    // Getters
    public long getId()                             { return id; }
    public Propiedad getPropiedad()                 { return propiedad; }
    public BigDecimal getPrecioMensual()            { return precioMensual; }
    public String getCondiciones()                  { return condiciones; }
    public String getDescripcion()                  { return descripcion; }
    public LocalDate getFechaPublicacion()          { return fechaPublicacion; }
    public EstadoPublicacion getEstado()            { return estado; }
    public boolean isEliminado()                    { return eliminado; }
    public List<HistorialEstadoPublicacion> getHistorialEstados() { return historialEstados; }

    // Setters
    public void setId(long id)                             { this.id = id; }
    public void setPropiedad(Propiedad propiedad)          { this.propiedad = propiedad; }
    public void setPrecioMensual(BigDecimal p)             { this.precioMensual = p; }
    public void setCondiciones(String condiciones)         { this.condiciones = condiciones; }
    public void setDescripcion(String descripcion)         { this.descripcion = descripcion; }
    public void setFechaPublicacion(LocalDate f)           { this.fechaPublicacion = f; }
    public void setEstado(EstadoPublicacion estado)        { this.estado = estado; }
    public void setEliminado(boolean eliminado)            { this.eliminado = eliminado; }
    public void setHistorialEstados(List<HistorialEstadoPublicacion> h) { this.historialEstados = h; }
}
