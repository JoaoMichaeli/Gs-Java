package com.gs.EcoDenuncia.controller;

import com.gs.EcoDenuncia.dto.City.CityRequestDTO;
import com.gs.EcoDenuncia.dto.City.CityResponseDTO;
import com.gs.EcoDenuncia.model.City;
import com.gs.EcoDenuncia.model.RoleType;
import com.gs.EcoDenuncia.model.User;
import com.gs.EcoDenuncia.repository.CityRepository;
import com.gs.EcoDenuncia.repository.StateRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/city")
@RequiredArgsConstructor
public class CityController {

    private final CityRepository repository;
    private final StateRepository stateRepository;

    @PostMapping
    @Operation(summary = "Criar cidade", description = "Cadastra uma nova cidade no sistema (Apenas ADMIN)")
    @CacheEvict(value = "cidades", allEntries = true)
    public ResponseEntity<?> criar(
            @RequestBody @Valid CityRequestDTO dto,
            @AuthenticationPrincipal User userAuth
    ) {
        if (!userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado.");
        }

        var estadoOpt = stateRepository.findById(dto.getIdEstado());
        if (estadoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Estado não encontrado.");
        }

        var cidade = City.builder()
                .nome(dto.getNome())
                .estado(estadoOpt.get())
                .build();

        var savedCity = repository.save(cidade);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(savedCity));
    }

    @GetMapping
    @Operation(summary = "Listar cidades", description = "Retorna uma lista com todas as cidades cadastradas")
    @Cacheable("cidades")
    public ResponseEntity<List<CityResponseDTO>> listar() {
        var lista = repository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cidade por ID", description = "Retorna os dados de uma cidade específica pelo ID")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<City> cidadeOpt = repository.findById(id);

        if (cidadeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cidade não encontrada.");
        }

        return ResponseEntity.ok(toResponseDTO(cidadeOpt.get()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cidade", description = "Atualiza os dados de uma cidade existente (Apenas ADMIN)")
    @CacheEvict(value = "cidades", allEntries = true)
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid CityRequestDTO dto,
            @AuthenticationPrincipal User userAuth
    ) {
        if (!userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado.");
        }

        Optional<City> cidadeOpt = repository.findById(id);
        if (cidadeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cidade não encontrada.");
        }

        Optional<?> estadoOpt = stateRepository.findById(dto.getIdEstado());
        if (estadoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Estado não encontrado.");
        }

        City cidade = cidadeOpt.get();
        cidade.setNome(dto.getNome());
        cidade.setEstado((com.gs.EcoDenuncia.model.State) estadoOpt.get());

        City updatedCity = repository.save(cidade);
        return ResponseEntity.ok(toResponseDTO(updatedCity));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar cidade", description = "Remove uma cidade do sistema (Apenas ADMIN)")
    @CacheEvict(value = "cidades", allEntries = true)
    public ResponseEntity<?> deletar(
            @PathVariable Long id,
            @AuthenticationPrincipal User userAuth
    ) {
        if (!userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado.");
        }

        Optional<City> cidadeOpt = repository.findById(id);
        if (cidadeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cidade não encontrada.");
        }

        repository.delete(cidadeOpt.get());
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
