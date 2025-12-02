package com.example.web_seguro.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.example.web_seguro.model.Empresa;
import com.example.web_seguro.repository.EmpresaRepository;

/**
 * 
 * Este test unitario valida el comportamiento del servicio EmpresaService utilizando
 * Mockito para simular el repositorio. Se comprueban los dos escenarios principales
 * del método getByUuid: cuando la empresa existe y cuando no,
 * asegurando que se lance la excepción correspondiente. Además, 
 * se verifica que el método listaEmpresas retorne correctamente la 
 * lista ordenada según la razón social. Con estas pruebas se garantiza que 
 * la lógica del servicio funciona de forma independiente y sin necesidad 
 * de conectarse a la base de datos real.
 * 
 */
@ExtendWith(MockitoExtension.class)
public class EmpresaServiceTest {
    
   @Mock
    private EmpresaRepository empresaRepository;

    @InjectMocks
    private EmpresaService empresaService;

    @Test
    void getByUuid_debeRetornarEmpresaCuandoExiste() {
        // Arrange preparar
        Empresa empresa = new Empresa();
        empresa.setUuid("123");

        when(empresaRepository.findByUuid("123"))
                .thenReturn(Optional.of(empresa));

        // Act, actuar
        Empresa resultado = empresaService.getByUuid("123");

        // Assert, afirmar
        assertNotNull(resultado);
        assertEquals("123", resultado.getUuid());
        verify(empresaRepository, times(1)).findByUuid("123");
    }

    @Test
    void getByUuid_debeLanzarExcepcionCuandoNoExiste() {
        // Arrange
        when(empresaRepository.findByUuid("noexiste"))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            empresaService.getByUuid("noexiste");
        });

        assertEquals("Empresa no encontrada con UUID: noexiste", ex.getMessage());
        verify(empresaRepository, times(1)).findByUuid("noexiste");
    }

    @Test
    void listaEmpresas_debeRetornarEmpresasOrdenadasPorRazonSocial() {
        // Arrange
        Empresa e1 = new Empresa(); e1.setRazonSocial("AAA");
        Empresa e2 = new Empresa(); e2.setRazonSocial("BBB");
        List<Empresa> lista = Arrays.asList(e1, e2);

        when(empresaRepository.findAll(Sort.by("razonSocial").ascending()))
                .thenReturn(lista);

        // Act
        List<Empresa> resultado = empresaService.listaEmpresas();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals("AAA", resultado.get(0).getRazonSocial());
        verify(empresaRepository, times(1))
                .findAll(Sort.by("razonSocial").ascending());
    }
}
