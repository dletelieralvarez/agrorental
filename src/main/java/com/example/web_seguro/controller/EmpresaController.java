package com.example.web_seguro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.web_seguro.model.Empresa;
import com.example.web_seguro.model.TipoCultivo;
import com.example.web_seguro.model.Usuario;
import com.example.web_seguro.repository.EmpresaRepository;
import com.example.web_seguro.repository.TipoCultivoRepository;
import com.example.web_seguro.repository.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private TipoCultivoRepository tipoCultivoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("")
    public String getEmpresas(@AuthenticationPrincipal Object principal, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Long idUsuario = usuario.getId();

        // 🧩 Log de validación
        log.info("Usuario autenticado: {} (ID: {})", email, idUsuario);

        // 🔹 Trae las empresas del usuario autenticado
        List<Empresa> empresas = empresaRepository.findByUsuarioIdOrderByRazonSocialAsc(idUsuario);

        // 🧩 Log de cantidad de empresas encontradas
        log.info("Empresas encontradas: {}", empresas.size());

        // 🧩 Log de cada empresa (si quieres ver los detalles)
        for (Empresa e : empresas) {
            log.info(" - Empresa: {} | Tipo cultivo: {}",
                    e.getRazonSocial(),
                    e.getTipoCultivo() != null ? e.getTipoCultivo().getDescripcion() : "Sin tipo");
        }

        model.addAttribute("id", idUsuario);
        model.addAttribute("empresas", empresas);
        return "mis_empresas";
    }

    // Muestra el formulario para nuevo registro
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {

        // lista de tipos de cultivo para el select
        List<TipoCultivo> tiposCultivos = tipoCultivoRepository.findAllByOrderByDescripcionAsc();

        model.addAttribute("empresa", new Empresa());
        model.addAttribute("tiposCultivos", tiposCultivos);

        return "empresa_form";
    }

    @PostMapping("/nuevo")
    public String guardarEmpresa(@ModelAttribute("empresa") Empresa empresa,
            @RequestParam("tipoCultivoUuid") String tipoCultivoUuid) {

        // Buscar usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Buscar el tipo de cultivo por UUID
        TipoCultivo tipoCultivo = tipoCultivoRepository.findByUuid(tipoCultivoUuid)
                .orElseThrow(() -> new RuntimeException("Tipo de cultivo no encontrado"));

        // Asignar relaciones
        empresa.setUsuario(usuario);
        empresa.setTipoCultivo(tipoCultivo);

        // Guardar
        empresaRepository.save(empresa);

        return "redirect:/empresas?success";
    }

    // --- Mostrar formulario de edición ---
    @GetMapping("/editar/{uuid}")
    public String mostrarFormularioEditar(@PathVariable String uuid, Model model) {
        Empresa empresa = empresaRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        model.addAttribute("empresa", empresa);
        model.addAttribute("tiposCultivos", tipoCultivoRepository.findAll());
        return "empresa_form_editar";
    }

    // --- Actualizar empresa existente ---
    @PostMapping("/actualizar/{uuid}")
    public String actualizarEmpresa(@PathVariable String uuid,
            @ModelAttribute("empresa") Empresa actualizada,
            @RequestParam("tipoCultivoUuid") String tipoCultivoUuid) {

        Empresa existente = empresaRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        TipoCultivo tipoCultivo = tipoCultivoRepository.findByUuid(tipoCultivoUuid)
                .orElseThrow(() -> new RuntimeException("Tipo de cultivo no encontrado"));

        existente.setRazonSocial(actualizada.getRazonSocial());
        existente.setDireccion(actualizada.getDireccion());
        existente.setTelefono(actualizada.getTelefono());
        existente.setNota(actualizada.getNota());
        existente.setTipoCultivo(tipoCultivo);

        empresaRepository.save(existente);

        return "redirect:/empresas?updated";
    }

    @PostMapping("/eliminar/{uuid}")
    public String eliminar(@PathVariable String uuid) {

        // Obtener usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Buscar la empresa por UUID
        Empresa empresa = empresaRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        // Verificar que pertenece al usuario actual
        if (!empresa.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para eliminar esta empresa");
        }

        // Eliminar registro
        empresaRepository.delete(empresa);

        return "redirect:/empresas?deleted";
    }

    }
