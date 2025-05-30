package com.gs.EcoDenuncia.controller;

import com.gs.EcoDenuncia.dto.State.StateRequestDTO;
import com.gs.EcoDenuncia.dto.State.StateResponseDTO;
import com.gs.EcoDenuncia.model.State;
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
@RequestMapping("/estados")
@RequiredArgsConstructor
public class StateController {

    private final StateRepository repository;

    @PostMapping
    @Operation(summary = "Criar estado", description = "Cadastra um novo estado no sistema")
    @CacheEvict(value = "estados", allEntries = true)
    public ResponseEntity<StateResponseDTO> criar(@RequestBody @Valid StateRequestDTO dto) {
        var estado = State.builder()
                .nome(dto.getNome())
                .uf(dto.getUf())
                .build();

        repository.save(estado);

        return ResponseEntity.ok(toResponseDTO(estado));
    }

    @GetMapping
    @Operation(summary = "Listar estados", description = "Retorna uma lista com todos os estados cadastrados")
    @Cacheable("estados")
    public ResponseEntity<List<StateResponseDTO>> listar() {
        var estados = repository.findAll();
        var dtos = estados.stream().map(this::toResponseDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar estado por ID", description = "Retorna os dados de um estado específico pelo ID")
    public ResponseEntity<StateResponseDTO> buscarPorId(@PathVariable Long id) {
        var estado = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estado não encontrado"));
        return ResponseEntity.ok(toResponseDTO(estado));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar estado", description = "Atualiza os dados de um estado existente")
    @CacheEvict(value = "estados", allEntries = true)
    public ResponseEntity<StateResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid StateRequestDTO dto) {
        var estado = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estado não encontrado"));

        estado.setNome(dto.getNome());
        estado.setUf(dto.getUf());

        repository.save(estado);

        return ResponseEntity.ok(toResponseDTO(estado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar estado", description = "Remove um estado do sistema")
    @CacheEvict(value = "estados", allEntries = true)
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        var estado = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estado não encontrado"));

        repository.delete(estado);
        return ResponseEntity.noContent().build();
    }

    private StateResponseDTO toResponseDTO(State estado) {
        return StateResponseDTO.builder()
                .id(estado.getId())
                .nome(estado.getNome())
                .uf(estado.getUf())
                .build();
    }
}
