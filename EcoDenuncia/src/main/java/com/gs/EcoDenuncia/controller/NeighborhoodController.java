package com.gs.EcoDenuncia.controller;

import com.gs.EcoDenuncia.dto.Neighborhood.NeighborhoodRequestDTO;
import com.gs.EcoDenuncia.dto.Neighborhood.NeighborhoodResponseDTO;
import com.gs.EcoDenuncia.model.Neighborhood;
import com.gs.EcoDenuncia.model.RoleType;
import com.gs.EcoDenuncia.model.User;
import com.gs.EcoDenuncia.repository.CityRepository;
import com.gs.EcoDenuncia.repository.NeighborhoodRepository;
import com.gs.EcoDenuncia.specification.NeighborhoodSpecification;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/neighborhood")
@RequiredArgsConstructor
@Slf4j
public class NeighborhoodController {

    @Autowired
    private NeighborhoodRepository repository;

    @Autowired
    private CityRepository cityRepository;

    public record NeighborhoodFilters(String nome, String cidade) {}

    @PostMapping
    @Operation(summary = "Criar bairro", description = "Cadastra um novo bairro no sistema (Apenas ADMIN)")
    @CacheEvict(value = "neighborhood", allEntries = true)
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
    @Cacheable("neighborhood")
    public Page<NeighborhoodResponseDTO> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cidade,
            @ParameterObject @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        var filters = new NeighborhoodFilters(nome, cidade);
        var specification = NeighborhoodSpecification.withFilters(filters);
        return repository.findAll(specification, pageable)
                .map(this::toResponseDTO);
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
    @CacheEvict(value = "neighborhood", allEntries = true)
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
    @CacheEvict(value = "neighborhood", allEntries = true)
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
