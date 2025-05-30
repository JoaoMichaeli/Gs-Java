package com.gs.EcoDenuncia.controller;

import com.gs.EcoDenuncia.dto.Neighborhood.NeighborhoodRequestDTO;
import com.gs.EcoDenuncia.dto.Neighborhood.NeighborhoodResponseDTO;
import com.gs.EcoDenuncia.model.Neighborhood;
import com.gs.EcoDenuncia.repository.CityRepository;
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
@RequestMapping("/bairros")
@RequiredArgsConstructor
public class NeighborhoodController {

    private final NeighborhoodRepository repository;
    private final CityRepository cityRepository;

    @PostMapping
    @Operation(summary = "Criar bairro", description = "Cadastra um novo bairro no sistema")
    @CacheEvict(value = "bairros", allEntries = true)
    public ResponseEntity<NeighborhoodResponseDTO> criar(@RequestBody @Valid NeighborhoodRequestDTO dto) {
        var cidade = cityRepository.findById(dto.getIdCidade())
                .orElseThrow(() -> new RuntimeException("Cidade não encontrada"));

        var bairro = Neighborhood.builder()
                .nome(dto.getNome())
                .cidade(cidade)
                .build();

        repository.save(bairro);

        return ResponseEntity.ok(toResponseDTO(bairro));
    }

    @GetMapping
    @Operation(summary = "Listar bairros", description = "Retorna todos os bairros cadastrados")
    @Cacheable("bairros")
    public ResponseEntity<List<NeighborhoodResponseDTO>> listar() {
        var lista = repository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar bairro por ID", description = "Retorna os dados de um bairro específico")
    public ResponseEntity<NeighborhoodResponseDTO> buscarPorId(@PathVariable Long id) {
        var bairro = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bairro não encontrado"));

        return ResponseEntity.ok(toResponseDTO(bairro));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar bairro", description = "Atualiza os dados de um bairro existente")
    @CacheEvict(value = "bairros", allEntries = true)
    public ResponseEntity<NeighborhoodResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid NeighborhoodRequestDTO dto) {
        var bairro = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bairro não encontrado"));

        var cidade = cityRepository.findById(dto.getIdCidade())
                .orElseThrow(() -> new RuntimeException("Cidade não encontrada"));

        bairro.setNome(dto.getNome());
        bairro.setCidade(cidade);

        repository.save(bairro);

        return ResponseEntity.ok(toResponseDTO(bairro));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar bairro", description = "Remove um bairro do sistema")
    @CacheEvict(value = "bairros", allEntries = true)
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        var bairro = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bairro não encontrado"));

        repository.delete(bairro);

        return ResponseEntity.noContent().build();
    }

    private NeighborhoodResponseDTO toResponseDTO(Neighborhood b) {
        return NeighborhoodResponseDTO.builder()
                .id(b.getId())
                .nome(b.getNome())
                .cidade(b.getCidade().getNome())
                .build();
    }
}
