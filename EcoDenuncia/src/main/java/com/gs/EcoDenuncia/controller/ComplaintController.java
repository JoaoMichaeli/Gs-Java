package com.gs.EcoDenuncia.controller;

import com.gs.EcoDenuncia.dto.Complaint.ComplaintRequestDTO;
import com.gs.EcoDenuncia.dto.Complaint.ComplaintResponseDTO;
import com.gs.EcoDenuncia.model.Complaint;
import com.gs.EcoDenuncia.model.RoleType;
import com.gs.EcoDenuncia.model.User;
import com.gs.EcoDenuncia.repository.ComplaintRepository;
import com.gs.EcoDenuncia.repository.LocationRepository;
import com.gs.EcoDenuncia.repository.PublicOrganizationRepository;
import com.gs.EcoDenuncia.repository.UserRepository;
import com.gs.EcoDenuncia.specification.ComplaintSpecification;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/complaints")
@RequiredArgsConstructor
@Slf4j
public class ComplaintController {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private PublicOrganizationRepository publicOrganizationRepository;

    public record ComplaintFilters(String descricao, String orgaoNome, String localizacaoCidade) {}

    @PostMapping
    @Operation(summary = "Criar denúncia", description = "Cadastra uma nova denúncia no sistema")
    @CacheEvict(value = "complaints", allEntries = true)
    public ResponseEntity<?> createComplaint(
            @RequestBody @Valid ComplaintRequestDTO dto,
            @AuthenticationPrincipal User userAuth) {

        if (!userAuth.getId().equals(dto.getIdUsuario()) && !userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Você só pode criar denúncias para seu próprio usuário");
        }
        var usuario = userRepository.findById(dto.getIdUsuario())
                .orElseGet(() -> {
                    log.error("Usuário não encontrado com ID: {}", dto.getIdUsuario());
                    return null;
                });
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário não encontrado");
        }
        var localizacao = locationRepository.findById(dto.getIdLocalizacao())
                .orElseGet(() -> {
                    log.error("Localização não encontrada com ID: {}", dto.getIdLocalizacao());
                    return null;
                });
        if (localizacao == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Localização não encontrada");
        }
        var orgao = publicOrganizationRepository.findById(dto.getIdOrgao())
                .orElseGet(() -> {
                    log.error("Órgão não encontrado com ID: {}", dto.getIdOrgao());
                    return null;
                });
        if (orgao == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Órgão não encontrado");
        }
        var denuncia = Complaint.builder()
                .usuario(usuario)
                .localizacao(localizacao)
                .orgao(orgao)
                .dataHora(dto.getDataHora())
                .descricao(dto.getDescricao())
                .build();
        Complaint savedComplaint = complaintRepository.save(denuncia);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(savedComplaint));
    }

    @GetMapping
    @Operation(summary = "Listar denúncias", description = "Retorna uma lista paginada de denúncias (somente ADMIN)")
    @Cacheable("complaints")
    public ResponseEntity<Page<ComplaintResponseDTO>> index(
            @AuthenticationPrincipal User userAuth,
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false) String orgaoNome,
            @RequestParam(required = false) String localizacaoCidade,
            @ParameterObject @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        if (!userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var filters = new ComplaintFilters(descricao, orgaoNome, localizacaoCidade);
        var specification = ComplaintSpecification.withFilters(filters);

        Page<Complaint> complaintsPage = complaintRepository.findAll(specification, (org.springframework.data.domain.Pageable) pageable);
        Page<ComplaintResponseDTO> dtoPage = complaintsPage.map(this::toResponseDTO);

        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Listar denúncias do usuário", description = "Retorna as denúncias de um usuário específico (ADMIN ou o próprio usuário)")
    public ResponseEntity<?> listUserComplaints(
            @PathVariable Long userId,
            @AuthenticationPrincipal User userAuth) {

        if (!userAuth.getId().equals(userId) && !userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Você só pode visualizar suas próprias denúncias");
        }

        List<ComplaintResponseDTO> dtos = complaintRepository.findByUsuarioId(userId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar denúncia por ID", description = "Busca uma denúncia específica (ADMIN ou dono da denúncia)")
    public ResponseEntity<?> getComplaintById(
            @PathVariable Long id,
            @AuthenticationPrincipal User userAuth) {

        Optional<Complaint> optionalComplaint = complaintRepository.findById(id);

        if (optionalComplaint.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Denúncia não encontrada");
        }

        Complaint complaint = optionalComplaint.get();

        if (!complaint.getUsuario().getId().equals(userAuth.getId()) &&
                !userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Você não tem permissão para acessar esta denúncia");
        }

        return ResponseEntity.ok(toResponseDTO(complaint));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar denúncia", description = "Atualiza uma denúncia (ADMIN ou dono da denúncia)")
    @CacheEvict(value = "complaints", allEntries = true)
    public ResponseEntity<?> updateComplaint(
            @PathVariable Long id,
            @RequestBody @Valid ComplaintRequestDTO dto,
            @AuthenticationPrincipal User userAuth) {

        Optional<Complaint> optionalComplaint = complaintRepository.findById(id);

        if (optionalComplaint.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Complaint complaint = optionalComplaint.get();

        if (!complaint.getUsuario().getId().equals(userAuth.getId()) &&
                !userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado.");
        }

        var usuario = userRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        var localizacao = locationRepository.findById(dto.getIdLocalizacao())
                .orElseThrow(() -> new RuntimeException("Localização não encontrada"));
        var orgao = publicOrganizationRepository.findById(dto.getIdOrgao())
                .orElseThrow(() -> new RuntimeException("Órgão não encontrado"));

        complaint.setUsuario(usuario);
        complaint.setLocalizacao(localizacao);
        complaint.setOrgao(orgao);
        complaint.setDataHora(dto.getDataHora());
        complaint.setDescricao(dto.getDescricao());

        Complaint updatedComplaint = complaintRepository.save(complaint);

        return ResponseEntity.ok(toResponseDTO(updatedComplaint));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar denúncia", description = "Remove uma denúncia (ADMIN ou dono da denúncia)")
    @CacheEvict(value = "complaints", allEntries = true)
    public ResponseEntity<?> deleteComplaint(
            @PathVariable Long id,
            @AuthenticationPrincipal User userAuth) {

        Optional<Complaint> optionalComplaint = complaintRepository.findById(id);

        if (optionalComplaint.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Complaint complaint = optionalComplaint.get();

        if (!complaint.getUsuario().getId().equals(userAuth.getId()) &&
                !userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado.");
        }

        complaintRepository.delete(complaint);
        return ResponseEntity.noContent().build();
    }

    private ComplaintResponseDTO toResponseDTO(Complaint complaint) {
        return ComplaintResponseDTO.builder()
                .id(complaint.getId())
                .nomeUsuario(complaint.getUsuario().getNome())
                .descricao(complaint.getDescricao())
                .dataHora(complaint.getDataHora())
                .nomeOrgao(complaint.getOrgao().getNome())
                .logradouro(complaint.getLocalizacao().getLogradouro())
                .numero(complaint.getLocalizacao().getNumero())
                .bairro(complaint.getLocalizacao().getBairro().getNome())
                .cidade(complaint.getLocalizacao().getBairro().getCidade().getNome())
                .estado(complaint.getLocalizacao().getBairro().getCidade().getEstado().getNome())
                .build();
    }
}
