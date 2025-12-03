package com.example.web_seguro;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.web_seguro.model.TipoMaquinaria;
import com.example.web_seguro.service.TipoMaquinariaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@RequestMapping("/tipomaquinaria")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class TipoMaquinariaController {
    private final TipoMaquinariaService tipoMaqService; 

    @GetMapping 
    public String vistaTipoMaq(Model model) {
        log.info("GETMAPPING /tipomaquinaria recibido: {}", model);
        System.out.println("Field error: " + model);
        model.addAttribute("tipo", new TipoMaquinaria()); 
        model.addAttribute("lista", tipoMaqService.listaTipoMaquinarias()); 
        return "tipomaquinaria";
    }

    @PostMapping("/guardarTipoMaquinaria")
    public String guardarTipoMaquinaria(@Valid @ModelAttribute("tipo") TipoMaquinaria tipoMaq, 
                                        BindingResult result, 
                                        Model model, 
                                        RedirectAttributes ra)
    {
        if(result.hasErrors()){
            model.addAttribute("lista", tipoMaqService.listaTipoMaquinarias()); 
            return "tipomaquinaria"; 
        }    
        
        try{
            tipoMaqService.guardarTipoMaquinaria(tipoMaq); 
            ra.addFlashAttribute("success", "Tipo de maquinaria guardada correctamente"); 
        }
        catch(DataIntegrityViolationException ex){
            log.warn("Violación de integridad al guardar tipo de maquinaria", ex);
            ra.addFlashAttribute("error", "No se pudo guardar: ya existe un registro con la misma descripción o UUID."); 
        }
        catch (Exception ex) {
            log.error("Error al guardar tipo de maquinaria", ex);
            ra.addFlashAttribute("error", "Error interno al guardar el registro."); 
        }

        return "redirect:/tipomaquinaria"; 
    }

    @GetMapping("/editar/{uuid}")
    public String editarTipoMaquinaria(@PathVariable String uuid, Model model, RedirectAttributes ra) {
        var opt = tipoMaqService.buscarTipoMaquinariaPorUuid(uuid);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "El tipo de maquinaria no existe.");
            return "redirect:/tipomaquinaria";
        }

        model.addAttribute("tipo", opt.get());     
        model.addAttribute("lista", tipoMaqService.listaTipoMaquinarias());
        model.addAttribute("editMode", true);     
        return "tipomaquinaria";
    }

    @PostMapping("/actualizar/{uuid}")
    public String actualizarTipoMaquinaria(@PathVariable String uuid,
                             @Valid @ModelAttribute("tipo") TipoMaquinaria tipoForm,
                             BindingResult result,
                             Model model,
                             RedirectAttributes ra) {
     
        if(result.hasErrors()){
            model.addAttribute("lista", tipoMaqService.listaTipoMaquinarias()); 
            model.addAttribute("editMode", true); 
            return "tipomaquinaria";
        }        
        
        try
        {
            tipoMaqService.actualizaTipoMaquinaria(uuid, tipoForm); 
            ra.addFlashAttribute("success", "Tipo de maquinaria actualizado correctamente.");
        }
        catch (DataIntegrityViolationException ex)
        {
            log.warn("Violación de integridad al actualizar tipo de maquinaria", ex);
            ra.addFlashAttribute("error", "No se pudo actualizar: descripción/UUID duplicados.");
        } catch (RuntimeException ex) 
        {
            log.error("Error interno al actualizar tipo de maquinaria", ex);
            ra.addFlashAttribute("error", ex.getMessage()); 
        } 
        
        return "redirect:/tipomaquinaria";  
    }

    @GetMapping("/eliminar/{uuid}")
    public String eliminarTipoMaquinaria(@PathVariable String uuid, RedirectAttributes ra) {
        try{
            tipoMaqService.eliminarTipoMaquinaria(uuid);
             ra.addFlashAttribute("success", "Tipo de maquinaria eliminado correctamente.");
        } catch (DataIntegrityViolationException ex) {            
            log.warn("No se puede eliminar: FK en uso", ex);
            ra.addFlashAttribute("error", "No se puede eliminar: existen maquinarias asociadas.");
        } catch (RuntimeException ex) {
            log.error("Error interno al eliminar tipo de maquinaria", ex);
            ra.addFlashAttribute("error", ex.getMessage());
        } 
        return "redirect:/tipomaquinaria";        
    }
     
}
