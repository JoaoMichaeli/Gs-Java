package com.gs.EcoDenuncia.controller;

import com.gs.EcoDenuncia.dto.City.CityRequestDTO;
import com.gs.EcoDenuncia.dto.City.CityResponseDTO;
import com.gs.EcoDenuncia.model.City;
import com.gs.EcoDenuncia.repository.CityRepository;
import com.gs.EcoDenuncia.repository.StateRepository;
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
@RequestMapping("/cidades")
@RequiredArgsConstructor
public class CityController {

    private final CityRepository repository;
    private final StateRepository stateRepository;

    @PostMapping
    @Operation(summary = "Criar cidade", description = "Cadastra uma nova cidade no sistema")
    @CacheEvict(value = "cidades", allEntries = true)
    public ResponseEntity<CityResponseDTO> criar(@RequestBody @Valid CityRequestDTO dto) {
        var estado = stateRepository.findById(dto.getIdEstado())
                .orElseThrow(() -> new RuntimeException("Estado não encontrado"));

        var cidade = City.builder()
                .nome(dto.getNome())
                .estado(estado)
                .build();

        repository.save(cidade);

        return ResponseEntity.ok(toResponseDTO(cidade));
    }

    @GetMapping
    @Operation(summary = "Listar cidades", description = "Retorna uma lista com todas as cidades cadastradas")
    @Cacheable("cidades")
    public ResponseEntity<List<CityResponseDTO>> listar() {
        var lista = repository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cidade por ID", description = "Retorna os dados de uma cidade específica pelo ID")
    public ResponseEntity<CityResponseDTO> buscarPorId(@PathVariable Long id) {
        var cidade = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cidade não encontrada"));

        return ResponseEntity.ok(toResponseDTO(cidade));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cidade", description = "Atualiza os dados de uma cidade existente")
    @CacheEvict(value = "cidades", allEntries = true)
    public ResponseEntity<CityResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid CityRequestDTO dto) {
        var cidade = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cidade não encontrada"));

        var estado = stateRepository.findById(dto.getIdEstado())
                .orElseThrow(() -> new RuntimeException("Estado não encontrado"));

        cidade.setNome(dto.getNome());
        cidade.setEstado(estado);

        repository.save(cidade);

        return ResponseEntity.ok(toResponseDTO(cidade));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar cidade", description = "Remove uma cidade do sistema")
    @CacheEvict(value = "cidades", allEntries = true)
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        var cidade = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cidade não encontrada"));

        repository.delete(cidade);

        return ResponseEntity.noContent().build();
    }

    private CityResponseDTO toResponseDTO(City cidade) {
        return CityResponseDTO.builder()
                .id(cidade.getId())
                .nome(cidade.getNome())
                .estado(cidade.getEstado().getNome())
                .build();
    }
}
