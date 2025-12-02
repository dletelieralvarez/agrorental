package com.example.web_seguro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.example.web_seguro.model.TipoCultivo;
import com.example.web_seguro.repository.TipoCultivoRepository;

public class TipoCultivoServiceTest {

  private TipoCultivoRepository tipoCultivoRepository;
  private TipoCultivoService tipoCultivoService;

  @BeforeEach
  void setUp() {
    tipoCultivoRepository = Mockito.mock(TipoCultivoRepository.class);
    tipoCultivoService = new TipoCultivoService(tipoCultivoRepository);
  }

  @Test
  void testGetByUuid_found() {
    String uuid = "123-abc";
    TipoCultivo tipo = new TipoCultivo();
    tipo.setUuid(uuid);
    when(tipoCultivoRepository.findByUuid(uuid)).thenReturn(Optional.of(tipo));
    TipoCultivo resultado = tipoCultivoService.getByUuid(uuid);
    assertNotNull(resultado);
    assertEquals(uuid, resultado.getUuid());
    verify(tipoCultivoRepository, times(1)).findByUuid(uuid);
  }

  @Test
  void testGetByUuid_notFound() {
    String uuid = "no-existe";
    when(tipoCultivoRepository.findByUuid(uuid)).thenReturn(Optional.empty());
    RuntimeException ex = assertThrows(RuntimeException.class, () -> tipoCultivoService.getByUuid(uuid));
    assertEquals("Tipo de cultivo no encontrado con UUID: " + uuid, ex.getMessage());
    verify(tipoCultivoRepository, times(1)).findByUuid(uuid);
  }

}
