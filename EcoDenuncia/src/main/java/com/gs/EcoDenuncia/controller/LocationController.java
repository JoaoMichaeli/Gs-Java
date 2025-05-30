package com.gs.EcoDenuncia.controller;

import com.gs.EcoDenuncia.dto.Location.LocationRequestDTO;
import com.gs.EcoDenuncia.dto.Location.LocationResponseDTO;
import com.gs.EcoDenuncia.model.Location;
import com.gs.EcoDenuncia.repository.LocationRepository;
import com.gs.EcoDenuncia.repository.NeighborhoodRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/localizacoes")
@RequiredArgsConstructor
public class LocationController {

    private final LocationRepository repository;
    private final NeighborhoodRepository neighborhoodRepository;

    @PostMapping
    @Operation(summary = "Criar localização", description = "Cadastra uma nova localização no sistema")
    @CacheEvict(value = "localizacoes", allEntries = true)
    public ResponseEntity<LocationResponseDTO> criar(@RequestBody @Valid LocationRequestDTO dto) {
        var bairro = neighborhoodRepository.findById(dto.getIdBairro())
                .orElseThrow(() -> new RuntimeException("Bairro não encontrado"));

        var localizacao = Location.builder()
                .logradouro(dto.getLogradouro())
                .numero(dto.getNumero())
                .complemento(dto.getComplemento())
                .cep(dto.getCep())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .bairro(bairro)
                .build();

        repository.save(localizacao);

        return ResponseEntity.ok(toResponseDTO(localizacao));
    }

    @GetMapping
    @Operation(summary = "Listar localizações", description = "Retorna uma lista com todas as localizações cadastradas")
    @Cacheable("localizacoes")
    public ResponseEntity<List<LocationResponseDTO>> listar() {
        var lista = repository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar localização por ID", description = "Retorna os dados de uma localização específica pelo ID")
    public ResponseEntity<LocationResponseDTO> buscarPorId(@PathVariable Long id) {
        var localizacao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Localização não encontrada"));

        return ResponseEntity.ok(toResponseDTO(localizacao));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar localização", description = "Atualiza os dados de uma localização existente")
    @CacheEvict(value = "localizacoes", allEntries = true)
    public ResponseEntity<LocationResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid LocationRequestDTO dto) {
        var localizacao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Localização não encontrada"));

        var bairro = neighborhoodRepository.findById(dto.getIdBairro())
                .orElseThrow(() -> new RuntimeException("Bairro não encontrado"));

        localizacao.setLogradouro(dto.getLogradouro());
        localizacao.setNumero(dto.getNumero());
        localizacao.setComplemento(dto.getComplemento());
        localizacao.setCep(dto.getCep());
        localizacao.setLatitude(dto.getLatitude());
        localizacao.setLongitude(dto.getLongitude());
        localizacao.setBairro(bairro);

        repository.save(localizacao);

        return ResponseEntity.ok(toResponseDTO(localizacao));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar localização", description = "Remove uma localização do sistema")
    @CacheEvict(value = "localizacoes", allEntries = true)
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        var localizacao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Localização não encontrada"));

        repository.delete(localizacao);

        return ResponseEntity.noContent().build();
    }

    private LocationResponseDTO toResponseDTO(Location l) {
        return LocationResponseDTO.builder()
                .id(l.getId())
                .logradouro(l.getLogradouro())
                .numero(l.getNumero())
                .complemento(l.getComplemento())
                .cep(l.getCep())
                .latitude(l.getLatitude())
                .longitude(l.getLongitude())
                .bairro(l.getBairro().getNome())
                .cidade(l.getBairro().getCidade().getNome())
                .estado(l.getBairro().getCidade().getEstado().getNome())
                .build();
    }
}
