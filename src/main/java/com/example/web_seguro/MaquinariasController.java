package com.example.web_seguro;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.web_seguro.model.Maquinarias;
import com.example.web_seguro.model.TipoMaquinaria;
import com.example.web_seguro.service.MaquinariasService;
import com.example.web_seguro.service.TipoMaquinariaService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/maquinarias")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class MaquinariasController {
    private final MaquinariasService maquinariasService; 
    private final TipoMaquinariaService tipoMaquinariaService; 

    @GetMapping 
    public String vistaMaquinarias(Model model) {
        log.info("GETMAPPING /maquinarias recibido: {}", model);
        System.out.println("Field error: " + model);
        model.addAttribute("maq", new Maquinarias()); 
        model.addAttribute("listaTipos", tipoMaquinariaService.listaTipoMaquinarias());
        model.addAttribute("lista", maquinariasService.listaMaquinarias()); 
        return "maquinarias";
    }

    //con esto estará disponible para utilizar dentro del controller
    @ModelAttribute("listaTipos")
    public List<TipoMaquinaria> cargarTipos() {
        return tipoMaquinariaService.listaTipoMaquinarias();
    }

    @PostMapping("/guardarMaquinaria")
    public String guardarMaquinaria(@Valid @ModelAttribute("maq") Maquinarias maq, 
                                        BindingResult result, 
                                        Model model, 
                                        RedirectAttributes ra)
    {
        log.info("POST /maquinarias/guardarMaquinaria payload: {}", maq);

        if(result.hasErrors()){
            result.getFieldErrors().forEach(e -> 
            System.out.println("Field error: " + e.getField() + " -> " + e.getDefaultMessage()));
            model.addAttribute("lista", maquinariasService.listaMaquinarias()); 
            return "maquinarias"; 
        }    
        
        try{
            maquinariasService.guardarMaquinaria(maq); 
            ra.addFlashAttribute("success", "Maquinaria guardada correctamente"); 
        }
        catch(DataIntegrityViolationException exv){
            log.warn("Violación de integridad al guardar maquinaria", exv);            
            ra.addFlashAttribute("error", "No se pudo guardar: ya existe un registro con la misma descripción o UUID."); 
        }
        catch (Exception ex) {
            ex.printStackTrace();  
            log.error("Error al guardar maquinaria", ex);
            ra.addFlashAttribute("error", "Error interno al guardar el registro."); 
        }

        return "redirect:/maquinarias"; 
    }

    @GetMapping("/editar/{id}")
    public String editarMaquinaria(@PathVariable Long id, Model model, RedirectAttributes ra) {
        var opt = maquinariasService.buscaMaquinariaPorId(id); 
        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "Maquinaria no existe.");
            return "redirect:/maquinarias";
        }

        model.addAttribute("maq", opt.get());     
        model.addAttribute("lista", maquinariasService.listaMaquinarias()); 
        model.addAttribute("editMode", true);     
        return "maquinarias";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarMaquinaria(@PathVariable Long id,
                             @Valid @ModelAttribute("maq") Maquinarias maq,
                             BindingResult result,
                             Model model,
                             RedirectAttributes ra) {
     
        if(result.hasErrors()){
            model.addAttribute("lista", maquinariasService.listaMaquinarias()); 
            model.addAttribute("editMode", true); 
            return "maquinarias";
        }        
        
        try
        {
            maquinariasService.actualizarMaquinaria(id, maq, id); 
            ra.addFlashAttribute("success", "Maquinaria actualizada correctamente.");
        }
        catch (DataIntegrityViolationException ex)
        {
            log.warn("Violación de integridad al actualizar maquinaria", ex);
            ra.addFlashAttribute("error", "No se pudo actualizar: descripción/UUID duplicados.");
        } catch (RuntimeException ex) 
        {
            ra.addFlashAttribute("error", ex.getMessage()); 
        } 
        catch (Exception ex) 
        {
            log.error("Error al actualizar maquinaria", ex);
            ra.addFlashAttribute("error", "Error interno al actualizar el registro.");
        }

        return "redirect:/maquinarias";  
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarMaquinaria(@PathVariable Long id, RedirectAttributes ra) {
        try{
            maquinariasService.eliminarMaquinaria(id); 
             ra.addFlashAttribute("success", "Maquinaria eliminada correctamente.");
        } catch (DataIntegrityViolationException ex) {            
            log.warn("No se puede eliminar: FK en uso", ex);
            ra.addFlashAttribute("error", "No se puede eliminar: existen empresas asociadas.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        } catch (Exception ex) {
            log.error("Error al eliminar maquinaria", ex);
            ra.addFlashAttribute("error", "Error interno al eliminar el registro.");
        }
        return "redirect:/maquinarias";        
    }

}
