package com.gs.EcoDenuncia.controller;

import com.gs.EcoDenuncia.dto.Neighborhood.NeighborhoodRequestDTO;
import com.gs.EcoDenuncia.dto.Neighborhood.NeighborhoodResponseDTO;
import com.gs.EcoDenuncia.model.Neighborhood;
import com.gs.EcoDenuncia.model.RoleType;
import com.gs.EcoDenuncia.model.User;
import com.gs.EcoDenuncia.repository.CityRepository;
import com.gs.EcoDenuncia.repository.NeighborhoodRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/neighborhood")
@RequiredArgsConstructor
public class NeighborhoodController {

    private final NeighborhoodRepository repository;
    private final CityRepository cityRepository;

    @PostMapping
    @Operation(summary = "Criar bairro", description = "Cadastra um novo bairro no sistema (Apenas ADMIN)")
    @CacheEvict(value = "bairros", allEntries = true)
    public ResponseEntity<?> criar(
            @RequestBody @Valid NeighborhoodRequestDTO dto,
            @AuthenticationPrincipal User userAuth) {

        if (!userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Somente administradores podem criar bairros");
        }

        var cidade = cityRepository.findById(dto.getIdCidade())
                .orElseThrow(() -> new RuntimeException("Cidade não encontrada"));

        var bairro = Neighborhood.builder()
                .nome(dto.getNome())
                .cidade(cidade)
                .build();

        Neighborhood savedBairro = repository.save(bairro);
        return ResponseEntity.ok(toResponseDTO(savedBairro));
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
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        var bairro = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bairro não encontrado"));

        return ResponseEntity.ok(toResponseDTO(bairro));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar bairro", description = "Atualiza os dados de um bairro existente (Apenas ADMIN)")
    @CacheEvict(value = "bairros", allEntries = true)
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid NeighborhoodRequestDTO dto,
            @AuthenticationPrincipal User userAuth) {

        if (!userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Somente administradores podem atualizar bairros");
        }

        var bairro = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bairro não encontrado"));

        var cidade = cityRepository.findById(dto.getIdCidade())
                .orElseThrow(() -> new RuntimeException("Cidade não encontrada"));

        bairro.setNome(dto.getNome());
        bairro.setCidade(cidade);

        Neighborhood updatedBairro = repository.save(bairro);
        return ResponseEntity.ok(toResponseDTO(updatedBairro));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar bairro", description = "Remove um bairro do sistema (Apenas ADMIN)")
    @CacheEvict(value = "bairros", allEntries = true)
    public ResponseEntity<?> deletar(
            @PathVariable Long id,
            @AuthenticationPrincipal User userAuth) {

        if (!userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Somente administradores podem remover bairros");
        }

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
