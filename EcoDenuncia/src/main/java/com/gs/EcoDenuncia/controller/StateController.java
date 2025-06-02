package com.gs.EcoDenuncia.controller;

import com.gs.EcoDenuncia.dto.State.StateRequestDTO;
import com.gs.EcoDenuncia.dto.State.StateResponseDTO;
import com.gs.EcoDenuncia.model.RoleType;
import com.gs.EcoDenuncia.model.State;
import com.gs.EcoDenuncia.model.User;
import com.gs.EcoDenuncia.repository.StateRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/state")
@RequiredArgsConstructor
public class StateController {

    @Autowired
    private final StateRepository repository;

    @PostMapping
    @Operation(summary = "Criar estado", description = "Cadastra um novo estado no sistema (Apenas ADMIN)")
    @CacheEvict(value = "estados", allEntries = true)
    public ResponseEntity<?> criar(
            @RequestBody @Valid StateRequestDTO dto,
            @AuthenticationPrincipal User userAuth) {

        if (!userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Somente administradores podem criar estados");
        }

        var estado = State.builder()
                .nome(dto.getNome())
                .uf(dto.getUf())
                .build();

        State savedState = repository.save(estado);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(savedState));
    }

    @GetMapping
    @Operation(summary = "Listar estados", description = "Retorna uma lista com todos os estados cadastrados")
    @Cacheable("estados")
    public ResponseEntity<List<StateResponseDTO>> listar() {
        var estados = repository.findAll();
        var dtos = estados.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar estado por ID", description = "Retorna os dados de um estado específico pelo ID")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        var estado = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estado não encontrado"));
        return ResponseEntity.ok(toResponseDTO(estado));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar estado", description = "Atualiza os dados de um estado existente (Apenas ADMIN)")
    @CacheEvict(value = "estados", allEntries = true)
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid StateRequestDTO dto,
            @AuthenticationPrincipal User userAuth) {

        if (!userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Somente administradores podem atualizar estados");
        }

        var estado = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estado não encontrado"));

        estado.setNome(dto.getNome());
        estado.setUf(dto.getUf());

        State updatedState = repository.save(estado);
        return ResponseEntity.ok(toResponseDTO(updatedState));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar estado", description = "Remove um estado do sistema (Apenas ADMIN)")
    @CacheEvict(value = "estados", allEntries = true)
    public ResponseEntity<?> deletar(
            @PathVariable Long id,
            @AuthenticationPrincipal User userAuth) {

        if (!userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Somente administradores podem remover estados");
        }

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
