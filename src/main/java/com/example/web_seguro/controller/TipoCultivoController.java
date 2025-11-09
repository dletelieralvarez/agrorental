package com.example.web_seguro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.web_seguro.model.TipoCultivo;
import com.example.web_seguro.repository.TipoCultivoRepository;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/tiposcultivos")
@PreAuthorize("hasRole('ADMIN')")
public class TipoCultivoController {

    @Autowired
    private TipoCultivoRepository tipoCultivoRepository;

    
    @GetMapping("")
    public String getTiposCultivos(Model model) {
        List<TipoCultivo> cultivos = tipoCultivoRepository.findAllByOrderByDescripcionAsc();
        model.addAttribute("cultivos", cultivos);
        return "tipo_cultivo"; // la vista html
    }

    // Muestra el formulario
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("tipoCultivo", new TipoCultivo());
        return "tipocultivo_form"; // nombre del archivo .html
    }

    // Procesa el formulario
    @PostMapping("/nuevo")
    public String guardarTipoCultivo(@ModelAttribute TipoCultivo tipoCultivo) {
        tipoCultivoRepository.save(tipoCultivo);
        return "redirect:/tiposcultivos/todos?success";
    }

    @GetMapping("/editar/{uuid}")
    public String editarTipoCultivo(@PathVariable String uuid, Model model) {
        TipoCultivo cultivo = tipoCultivoRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Tipo de cultivo no encontrado"));
        model.addAttribute("tipoCultivo", cultivo);
        return "tipocultivo_form_editar"; // vista que mostrare 
    }

    // --- Actualizar registro ---
    @PostMapping("/actualizar/{uuid}")
    public String actualizar(@PathVariable String uuid, @ModelAttribute TipoCultivo actualizado) {
        TipoCultivo existente = tipoCultivoRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Tipo de cultivo no encontrado"));

        existente.setDescripcion(actualizado.getDescripcion());
        tipoCultivoRepository.save(existente);

        return "redirect:/tiposcultivos/todos?updated";
    }

    @PostMapping("/eliminar/{uuid}")
    public String eliminar(@PathVariable String uuid) {
        TipoCultivo cultivo = tipoCultivoRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Tipo de cultivo no encontrado"));
        tipoCultivoRepository.delete(cultivo);
        return "redirect:/tiposcultivos/todos?deleted";
    }

}