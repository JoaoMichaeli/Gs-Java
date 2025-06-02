package com.gs.EcoDenuncia.controller;

import com.gs.EcoDenuncia.dto.Location.LocationRequestDTO;
import com.gs.EcoDenuncia.dto.Location.LocationResponseDTO;
import com.gs.EcoDenuncia.model.Location;
import com.gs.EcoDenuncia.model.RoleType;
import com.gs.EcoDenuncia.model.User;
import com.gs.EcoDenuncia.repository.LocationRepository;
import com.gs.EcoDenuncia.repository.NeighborhoodRepository;
import com.gs.EcoDenuncia.specification.LocationSpecification;
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
@RequestMapping("/location")
@RequiredArgsConstructor
@Slf4j

public class LocationController {

    @Autowired
    private LocationRepository repository;

    @Autowired
    private NeighborhoodRepository neighborhoodRepository;

    public record LocationFilters(String logradouro, String cep, String bairro) {}


    @PostMapping
    @Operation(summary = "Criar localização", description = "Cadastra uma nova localização no sistema (Apenas ADMIN)")
    @CacheEvict(value = "location", allEntries = true)
    public ResponseEntity<?> criar(
            @RequestBody @Valid LocationRequestDTO dto,
            @AuthenticationPrincipal User userAuth) {

        if (!userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Somente administradores podem criar localizações");
        }

        var bairro = neighborhoodRepository.findById(dto.getIdBairro())
                .orElseThrow(() -> new RuntimeException("Bairro não encontrado"));

        var localizacao = Location.builder()
                .logradouro(dto.getLogradouro())
                .numero(dto.getNumero())
                .complemento(dto.getComplemento())
                .cep(dto.getCep())
                .bairro(bairro)
                .build();

        repository.save(localizacao);

        return ResponseEntity.ok(toResponseDTO(localizacao));
    }

    @GetMapping
    @Operation(summary = "Listar localizações", description = "Retorna uma lista com todas as localizações cadastradas")
    @Cacheable("location")
    public Page<LocationResponseDTO> listar(
            @RequestParam(required = false) String logradouro,
            @RequestParam(required = false) String cep,
            @RequestParam(required = false) String bairro,
            @ParameterObject @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        var filters = new LocationFilters(logradouro, cep, bairro);
        var specification = LocationSpecification.withFilters(filters);
        return repository.findAll(specification, pageable)
                .map(this::toResponseDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar localização por ID", description = "Retorna os dados de uma localização específica pelo ID")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        var localizacao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Localização não encontrada"));

        return ResponseEntity.ok(toResponseDTO(localizacao));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar localização", description = "Atualiza os dados de uma localização existente (Apenas ADMIN)")
    @CacheEvict(value = "location", allEntries = true)
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid LocationRequestDTO dto,
            @AuthenticationPrincipal User userAuth) {

        if (!userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Somente administradores podem atualizar localizações");
        }

        var localizacao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Localização não encontrada"));

        var bairro = neighborhoodRepository.findById(dto.getIdBairro())
                .orElseThrow(() -> new RuntimeException("Bairro não encontrado"));

        localizacao.setLogradouro(dto.getLogradouro());
        localizacao.setNumero(dto.getNumero());
        localizacao.setComplemento(dto.getComplemento());
        localizacao.setCep(dto.getCep());
        localizacao.setBairro(bairro);

        repository.save(localizacao);

        return ResponseEntity.ok(toResponseDTO(localizacao));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar localização", description = "Remove uma localização do sistema (Apenas ADMIN)")
    @CacheEvict(value = "location", allEntries = true)
    public ResponseEntity<?> deletar(
            @PathVariable Long id,
            @AuthenticationPrincipal User userAuth) {

        if (!userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Somente administradores podem remover localizações");
        }

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
                .bairro(l.getBairro().getNome())
                .cidade(l.getBairro().getCidade().getNome())
                .estado(l.getBairro().getCidade().getEstado().getNome())
                .build();
    }
}
