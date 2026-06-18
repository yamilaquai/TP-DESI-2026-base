package tuti.desi.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "Ciudad")
public class Ciudad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nombre;

    @ManyToOne
    private Provincia provincia;

    public Ciudad() {}

    public Ciudad(String nombre, Provincia provincia) {
        this.nombre = nombre;
        this.provincia = provincia;
    }

    public Long getId()                   { return this.id; }
    public String getNombre()             { return this.nombre; }
    public Provincia getProvincia()       { return this.provincia; }

    public void setNombre(String nombre)       { this.nombre = nombre; }
    // CORREGIDO: typo original "serProvincia" → "setProvincia"
    public void setProvincia(Provincia p)      { this.provincia = p; }
}
