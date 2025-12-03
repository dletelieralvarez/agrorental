package com.example.web_seguro;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.web_seguro.model.Maquinarias;
import com.example.web_seguro.model.TipoMaquinaria;
import com.example.web_seguro.service.EmpresaService;
import com.example.web_seguro.service.MaquinariasService;
import com.example.web_seguro.service.TipoMaquinariaService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/maquinarias")
@RequiredArgsConstructor
public class MaquinariasController {
    private final MaquinariasService maquinariasService; 
    private final TipoMaquinariaService tipoMaquinariaService; 
    private final EmpresaService empresaService; 

    public static final String KEY_ERROR = "error";
    public static final String KEY_SUCCESS = "success";

    public static final String VIEW_MAQUINARIAS = "maquinarias";
    public static final String REDIRECT_MAQUINARIAS_ALERTS = "redirect:/maquinarias#alerts";
    
    @GetMapping 
    public String vistaMaquinarias(Model model) {
        log.info("GETMAPPING /maquinarias recibido: {}", model);
        System.out.println("Field error: " + model);
        model.addAttribute("maq", new Maquinarias()); 
        model.addAttribute("listaTipos", tipoMaquinariaService.listaTipoMaquinarias());
        model.addAttribute("lista", maquinariasService.listaMaquinarias());
        model.addAttribute("listaEmpresas", empresaService.listaEmpresas()); 
        return VIEW_MAQUINARIAS;
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
            return VIEW_MAQUINARIAS; 
        }    
        
        try{
            maquinariasService.guardarMaquinaria(maq); 
            ra.addFlashAttribute(KEY_SUCCESS, "Maquinaria guardada correctamente"); 
        }
        catch(DataIntegrityViolationException exv){
            log.warn("Violación de integridad al guardar maquinaria", exv);            
            ra.addFlashAttribute(KEY_ERROR, "No se pudo guardar: ya existe un registro con la misma descripción o UUID."); 
        }
        catch (Exception ex) {
            log.error("Error al guardar maquinaria", ex);
            ra.addFlashAttribute(KEY_ERROR, "Error interno al guardar el registro."); 
        }

        return "redirect:/maquinarias#alerts"; 
    }

    @GetMapping("/editar/{uuid}")
    public String editarMaquinaria(@PathVariable String uuid, Model model, RedirectAttributes ra) {
        var opt = maquinariasService.buscaMaquinariaPorUuid(uuid); 
        if (opt.isEmpty()) {
            ra.addFlashAttribute(KEY_ERROR, "Maquinaria no existe.");
            return "redirect:/maquinarias#alerts";
        }

        model.addAttribute("maq", opt.get());     
        model.addAttribute("lista", maquinariasService.listaMaquinarias()); 
        model.addAttribute("listaTipos", tipoMaquinariaService.listaTipoMaquinarias());
        model.addAttribute("listaEmpresas", empresaService.listaEmpresas());
        model.addAttribute("editMode", true);     
        return VIEW_MAQUINARIAS;
    }

    @PostMapping("/actualizar/{uuid}")
    public String actualizarMaquinaria(@PathVariable String uuid,
                             @Valid @ModelAttribute("maq") Maquinarias maq,
                             BindingResult result,
                             Model model,
                             RedirectAttributes ra) {
     
        if(result.hasErrors()){
            model.addAttribute("lista", maquinariasService.listaMaquinarias()); 
            model.addAttribute("listaTipos", tipoMaquinariaService.listaTipoMaquinarias());
            model.addAttribute("listaEmpresas", empresaService.listaEmpresas());
            model.addAttribute("editMode", true); 
            return VIEW_MAQUINARIAS;
        }        
        
        try
        {
            maquinariasService.actualizarMaquinaria(uuid, maq); 
            ra.addFlashAttribute(KEY_SUCCESS, "Maquinaria actualizada correctamente.");
        }
        catch (DataIntegrityViolationException ex)
        {
            log.warn("Violación de integridad al actualizar maquinaria", ex);
            ra.addFlashAttribute(KEY_ERROR, "No se pudo actualizar: descripción/UUID duplicados.");
        } catch (RuntimeException ex) 
        {
            log.error("Error al actualizar maquinaria", ex);
            ra.addFlashAttribute(KEY_ERROR, ex.getMessage()); 
        }         

        return "redirect:/maquinarias#alerts";  
    }

    @GetMapping("/eliminar/{uuid}")
    public String eliminarMaquinaria(@PathVariable String uuid, RedirectAttributes ra) {
        try{
            maquinariasService.eliminarMaquinaria(uuid); 
             ra.addFlashAttribute(KEY_SUCCESS, "Maquinaria eliminada correctamente.");
        } catch (DataIntegrityViolationException ex) {            
            log.warn("No se puede eliminar: FK en uso", ex);
            ra.addFlashAttribute(KEY_ERROR, "No se puede eliminar: existen empresas asociadas.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute(KEY_ERROR, ex.getMessage());
        } 
        return "redirect:/maquinarias#alerts";        
    }

}
