package com.gs.EcoDenuncia.controller;

import com.gs.EcoDenuncia.dto.City.CityRequestDTO;
import com.gs.EcoDenuncia.dto.City.CityResponseDTO;
import com.gs.EcoDenuncia.model.City;
import com.gs.EcoDenuncia.model.RoleType;
import com.gs.EcoDenuncia.model.User;
import com.gs.EcoDenuncia.repository.CityRepository;
import com.gs.EcoDenuncia.repository.StateRepository;
import com.gs.EcoDenuncia.specification.CitySpecification;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/city")
@RequiredArgsConstructor
@Slf4j
public class CityController {

    @Autowired
    private CityRepository repository;

    @Autowired
    private StateRepository stateRepository;

    public record CityFilters (String nome, String estado){}

    @PostMapping
    @Operation(summary = "Criar cidade", description = "Cadastra uma nova cidade no sistema (Apenas ADMIN)")
    @CacheEvict(value = "city", allEntries = true)
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
    @Operation(summary = "Listar cidades", description = "Retorna uma lista paginada de cidades cadastradas")
    @Cacheable("city")
    public Page<City> index(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String estado,
            @ParameterObject @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        var filters = new CityFilters(nome, estado);
        var specification = CitySpecification.withFilters(filters);
        return repository.findAll(specification, pageable);
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
    @CacheEvict(value = "city", allEntries = true)
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
    @CacheEvict(value = "city", allEntries = true)
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

    private List<Sort.Order> parseSortOrders(String[] sort) {
        return Arrays.stream(sort)
                .map(s -> s.split(","))
                .map(arr -> {
                    String property = arr[0];
                    Sort.Direction direction = arr.length > 1 ?
                            Sort.Direction.fromString(arr[1]) : Sort.Direction.ASC;
                    return new Sort.Order(direction, property);
                })
                .collect(Collectors.toList());
    }
}
