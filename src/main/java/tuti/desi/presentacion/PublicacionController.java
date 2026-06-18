package tuti.desi.presentacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tuti.desi.entidades.Propiedad;
import tuti.desi.entidades.Publicacion;
import tuti.desi.enums.EstadoPublicacion;
import tuti.desi.servicios.publicacionServicios;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/publicaciones")
public class PublicacionController {

    @Autowired private publicacionServicios servicio;

    // ── HU 2.4: Listado ───────────────────────────────────────────────────────

    @GetMapping
    public String listado(@RequestParam(required = false) Long propiedadId,
                          @RequestParam(required = false) String ciudad,
                          @RequestParam(required = false) EstadoPublicacion estado,
                          @RequestParam(required = false) BigDecimal precioMin,
                          @RequestParam(required = false) BigDecimal precioMax,
                          Model model) {

        boolean hayFiltros = propiedadId != null
            || (ciudad != null && !ciudad.isBlank())
            || estado != null || precioMin != null || precioMax != null;

        List<Publicacion> publicaciones = hayFiltros
            ? servicio.listarConFiltros(propiedadId, ciudad, estado, precioMin, precioMax)
            : servicio.listarTodas();

        model.addAttribute("publicaciones", publicaciones);
        model.addAttribute("propiedades", servicio.obtenerTodasPropiedades());
        model.addAttribute("estados", EstadoPublicacion.values());
        model.addAttribute("filtroPropiedadId", propiedadId);
        model.addAttribute("filtroCiudad", ciudad);
        model.addAttribute("filtroEstado", estado);
        model.addAttribute("filtroPrecioMin", precioMin);
        model.addAttribute("filtroPrecioMax", precioMax);
        return "publicaciones/listado";
    }

    // ── HU 2.1: Alta ──────────────────────────────────────────────────────────

    @GetMapping("/nueva")
    public String formularioAlta(Model model) {
        Publicacion pub = new Publicacion();
        pub.setPropiedad(new Propiedad());
        model.addAttribute("publicacion", pub);
        model.addAttribute("propiedades", servicio.obtenerPropiedadesDisponibles());
        model.addAttribute("estados", EstadoPublicacion.values());
        model.addAttribute("modoEdicion", false);
        return "publicaciones/formulario";
    }

    @PostMapping("/nueva")
    public String procesarAlta(@ModelAttribute Publicacion publicacion,
                               @RequestParam Long propiedadId,
                               Model model, RedirectAttributes redirectAttributes) {
        try {
            Propiedad prop = new Propiedad();
            prop.setId(propiedadId);
            publicacion.setPropiedad(prop);
            servicio.crear(publicacion);
            redirectAttributes.addFlashAttribute("mensajeExito", "Publicación creada correctamente.");
            return "redirect:/publicaciones";
        } catch (Exception e) {
            model.addAttribute("mensajeError", e.getMessage());
            model.addAttribute("publicacion", publicacion);
            model.addAttribute("propiedades", servicio.obtenerPropiedadesDisponibles());
            model.addAttribute("estados", EstadoPublicacion.values());
            model.addAttribute("modoEdicion", false);
            return "publicaciones/formulario";
        }
    }

    // ── HU 2.3: Modificación ─────────────────────────────────────────────────

    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model,
                                    RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("publicacion", servicio.obtenerOException(id));
            model.addAttribute("estados", EstadoPublicacion.values());
            model.addAttribute("modoEdicion", true);
            return "publicaciones/formulario";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
            return "redirect:/publicaciones";
        }
    }

    @PostMapping("/editar/{id}")
    public String procesarEdicion(@PathVariable Long id,
                                   @ModelAttribute Publicacion publicacionForm,
                                   Model model, RedirectAttributes redirectAttributes) {
        try {
            servicio.modificar(id, publicacionForm);
            redirectAttributes.addFlashAttribute("mensajeExito",
                "Publicación #" + id + " modificada correctamente.");
            return "redirect:/publicaciones";
        } catch (Exception e) {
            model.addAttribute("mensajeError", e.getMessage());
            // Recargar la publicación para mostrar propiedad (solo lectura)
            try {
                Publicacion pub = servicio.obtenerOException(id);
                publicacionForm.setId(pub.getId());
                publicacionForm.setPropiedad(pub.getPropiedad());
            } catch (Exception ignored) {}
            model.addAttribute("publicacion", publicacionForm);
            model.addAttribute("estados", EstadoPublicacion.values());
            model.addAttribute("modoEdicion", true);
            return "publicaciones/formulario";
        }
    }

    // ── HU 2.2: Eliminación ───────────────────────────────────────────────────

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            servicio.eliminar(id);
            redirectAttributes.addFlashAttribute("mensajeExito",
                "Publicación #" + id + " eliminada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/publicaciones";
    }
}
