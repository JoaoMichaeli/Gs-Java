package com.gs.EcoDenuncia.controller;

import com.gs.EcoDenuncia.dto.ReportFollowUp.ReportFollowupRequestDTO;
import com.gs.EcoDenuncia.dto.ReportFollowUp.ReportFollowupResponseDTO;
import com.gs.EcoDenuncia.model.Complaint;
import com.gs.EcoDenuncia.model.ReportFollowup;
import com.gs.EcoDenuncia.model.RoleType;
import com.gs.EcoDenuncia.model.User;
import com.gs.EcoDenuncia.repository.ComplaintRepository;
import com.gs.EcoDenuncia.repository.ReportFollowupRepository;
import com.gs.EcoDenuncia.specification.ReportFollowUpSpecification;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/followup")
@RequiredArgsConstructor
@Slf4j
public class ReportFollowupController {

    @Autowired
    private ReportFollowupRepository repository;

    @Autowired
    private ComplaintRepository complaintRepository;

    public record ReportFollowupFilters(String status) {}

    @GetMapping("/denuncia/{denunciaId}")
    @Operation(summary = "Listar acompanhamentos por denúncia",
            description = "Retorna todos os acompanhamentos de uma denúncia (ADMIN ou dono da denúncia)")
    public ResponseEntity<?> getByDenunciaId(
            @PathVariable Long denunciaId,
            @AuthenticationPrincipal User userAuth) {

        Optional<Complaint> denunciaOpt = complaintRepository.findById(denunciaId);
        if (denunciaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Denúncia não encontrada");
        }

        Complaint denuncia = denunciaOpt.get();

        if (!userAuth.getRole().equals(RoleType.ADMIN) &&
                !denuncia.getUsuario().getId().equals(userAuth.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Você não tem permissão para ver estes acompanhamentos");
        }

        List<ReportFollowupResponseDTO> acompanhamentos = repository.findByDenunciaId(denunciaId)
                .stream()
                .map(ReportFollowupResponseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(acompanhamentos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar acompanhamento por ID",
            description = "Retorna um acompanhamento específico (ADMIN ou dono da denúncia relacionada)")
    public ResponseEntity<?> getAcompanhamentoById(
            @PathVariable Long id,
            @AuthenticationPrincipal User userAuth) {

        Optional<ReportFollowup> optionalAcompanhamento = repository.findById(id);
        if (optionalAcompanhamento.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Acompanhamento não encontrado");
        }

        ReportFollowup acompanhamento = optionalAcompanhamento.get();

        // Verifica se é ADMIN ou dono da denúncia relacionada
        if (!userAuth.getRole().equals(RoleType.ADMIN) &&
                !acompanhamento.getDenuncia().getUsuario().getId().equals(userAuth.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Você não tem permissão para ver este acompanhamento");
        }

        return ResponseEntity.ok(new ReportFollowupResponseDTO(acompanhamento));
    }

    @PostMapping
    @Operation(summary = "Criar acompanhamento",
            description = "Cria um novo acompanhamento de denúncia (Apenas ADMIN), Status ('Aberto', 'Em Andamento', 'Concluido')")
    @CacheEvict(value = "followup", allEntries = true)
    public ResponseEntity<?> createAcompanhamento(
            @RequestBody @Valid ReportFollowupRequestDTO dto,
            @AuthenticationPrincipal User userAuth) {

        if (!userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Somente administradores podem criar acompanhamentos");
        }

        Optional<Complaint> denunciaOpt = complaintRepository.findById(dto.getDenunciaId());
        if (denunciaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Denúncia não encontrada");
        }

        ReportFollowup acompanhamento = new ReportFollowup();
        acompanhamento.setStatus(dto.getStatus());
        acompanhamento.setDescricao(dto.getDescricao());
        acompanhamento.setDataAtualizacao(new Date());
        acompanhamento.setDenuncia(denunciaOpt.get());

        ReportFollowup saved = repository.save(acompanhamento);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ReportFollowupResponseDTO(saved));
    }

    @GetMapping
    @Operation(summary = "Listar todos os acompanhamentos",
            description = "Lista todos os acompanhamentos do sistema (Apenas ADMIN)")
    @Cacheable("followup")
    public ResponseEntity<?> listAll(
            @AuthenticationPrincipal User userAuth,
            @RequestParam(required = false) String status,
            @ParameterObject @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        if (!userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado: Somente administradores podem listar todos os acompanhamentos");
        }

        var filters = new ReportFollowupFilters(status);
        var specification = ReportFollowUpSpecification.withFilters(filters);
        var page = repository.findAll(specification, pageable)
                .map(ReportFollowupResponseDTO::new);

        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar acompanhamento",
            description = "Atualiza os dados de um acompanhamento existente (Apenas ADMIN)")
    @CacheEvict(value = "followup", allEntries = true)
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody @Valid ReportFollowupRequestDTO dto,
            @AuthenticationPrincipal User userAuth) {

        if (!userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Somente administradores podem atualizar acompanhamentos");
        }

        Optional<ReportFollowup> opt = repository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Acompanhamento não encontrado");
        }

        ReportFollowup acompanhamento = opt.get();
        acompanhamento.setStatus(dto.getStatus());
        acompanhamento.setDescricao(dto.getDescricao());
        acompanhamento.setDataAtualizacao(new Date());

        Optional<Complaint> denunciaOpt = complaintRepository.findById(dto.getDenunciaId());
        if (denunciaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Denúncia não encontrada");
        }
        acompanhamento.setDenuncia(denunciaOpt.get());

        ReportFollowup updated = repository.save(acompanhamento);
        return ResponseEntity.ok(new ReportFollowupResponseDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar acompanhamento",
            description = "Remove um acompanhamento do sistema (Apenas ADMIN)")
    @CacheEvict(value = "followup", allEntries = true)
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User userAuth) {

        if (!userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Somente administradores podem remover acompanhamentos");
        }

        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Acompanhamento não encontrado");
        }

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
