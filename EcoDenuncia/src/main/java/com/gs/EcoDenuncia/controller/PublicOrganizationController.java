package com.gs.EcoDenuncia.controller;

import com.gs.EcoDenuncia.dto.PublicOrganization.PublicOrganizationRequestDTO;
import com.gs.EcoDenuncia.dto.PublicOrganization.PublicOrganizationResponseDTO;
import com.gs.EcoDenuncia.model.PublicOrganization;
import com.gs.EcoDenuncia.model.RoleType;
import com.gs.EcoDenuncia.model.User;
import com.gs.EcoDenuncia.repository.PublicOrganizationRepository;
import com.gs.EcoDenuncia.specification.PublicOrganizationSpecification;
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
@RequestMapping("/organizations")
@RequiredArgsConstructor
@Slf4j
public class PublicOrganizationController {

    @Autowired
    private PublicOrganizationRepository repository;

    public record PublicOrganizationFilters(String nome, String areaAtuacao) {}

    @PostMapping
    @Operation(summary = "Criar órgão público", description = "Cadastra um novo órgão público (Apenas ADMIN), Areas 'Urbana', 'Ambiental', 'Saude'")
    @CacheEvict(value = "organizations", allEntries = true)
    public ResponseEntity<?> criar(
            @RequestBody @Valid PublicOrganizationRequestDTO dto,
            @AuthenticationPrincipal User userAuth) {

        if (!userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado: Somente administradores podem criar órgãos públicos");
        }

        PublicOrganization orgao = PublicOrganization.builder()
                .nome(dto.getNome())
                .areaAtuacao(dto.getAreaAtuacao())
                .build();

        PublicOrganization saved = repository.save(orgao);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PublicOrganizationResponseDTO(saved));
    }

    @GetMapping
    @Operation(summary = "Listar órgãos públicos", description = "Retorna todos os órgãos públicos cadastrados")
    @Cacheable("organizations")
    public Page<PublicOrganizationResponseDTO> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String areaAtuacao,
            @ParameterObject @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        var filters = new PublicOrganizationFilters(nome, areaAtuacao);
        var specification = PublicOrganizationSpecification.withFilters(filters);
        return repository.findAll(specification, pageable)
                .map(PublicOrganizationResponseDTO::new);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar órgão por ID", description = "Retorna um órgão público específico pelo ID")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(orgao -> ResponseEntity.ok(new PublicOrganizationResponseDTO(orgao)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar órgão", description = "Atualiza os dados de um órgão público (Apenas ADMIN)")
    @CacheEvict(value = "organizations", allEntries = true)
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid PublicOrganizationRequestDTO dto,
            @AuthenticationPrincipal User userAuth) {

        if (!userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado: Somente administradores podem atualizar órgãos públicos");
        }

        return repository.findById(id)
                .map(orgao -> {
                    orgao.setNome(dto.getNome());
                    orgao.setAreaAtuacao(dto.getAreaAtuacao());
                    PublicOrganization updated = repository.save(orgao);
                    return ResponseEntity.ok(new PublicOrganizationResponseDTO(updated));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar órgão", description = "Remove um órgão público do sistema (Apenas ADMIN)")
    @CacheEvict(value = "organizations", allEntries = true)
    public ResponseEntity<?> deletar(
            @PathVariable Long id,
            @AuthenticationPrincipal User userAuth) {

        if (!userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado: Somente administradores podem remover órgãos públicos");
        }

        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
