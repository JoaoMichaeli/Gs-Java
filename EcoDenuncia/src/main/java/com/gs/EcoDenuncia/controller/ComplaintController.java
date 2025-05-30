package com.gs.EcoDenuncia.controller;

import com.gs.EcoDenuncia.dto.Complaint.ComplaintRequestDTO;
import com.gs.EcoDenuncia.dto.Complaint.ComplaintResponseDTO;
import com.gs.EcoDenuncia.model.Complaint;
import com.gs.EcoDenuncia.repository.ComplaintRepository;
import com.gs.EcoDenuncia.repository.LocationRepository;
import com.gs.EcoDenuncia.repository.PublicOrganizationRepository;
import com.gs.EcoDenuncia.repository.UserRepository;
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
@RequestMapping("/denuncias")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final PublicOrganizationRepository publicOrganizationRepository;

    @PostMapping
    @Operation(summary = "Criar denúncia", description = "Cadastra uma nova denúncia no sistema")
    @CacheEvict(value = "denuncias", allEntries = true)
    public ResponseEntity<ComplaintResponseDTO> criarDenuncia(@RequestBody @Valid ComplaintRequestDTO dto) {
        var usuario = userRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        var localizacao = locationRepository.findById(dto.getIdLocalizacao())
                .orElseThrow(() -> new RuntimeException("Localização não encontrada"));
        var orgao = publicOrganizationRepository.findById(dto.getIdOrgao())
                .orElseThrow(() -> new RuntimeException("Órgão não encontrado"));

        var denuncia = Complaint.builder()
                .usuario(usuario)
                .localizacao(localizacao)
                .orgao(orgao)
                .dataHora(dto.getDataHora())
                .descricao(dto.getDescricao())
                .build();

        complaintRepository.save(denuncia);

        return ResponseEntity.ok(toResponseDTO(denuncia));
    }

    @GetMapping
    @Operation(summary = "Listar denúncias", description = "Retorna uma lista com todas as denúncias cadastradas")
    @Cacheable("denuncias")
    public ResponseEntity<List<ComplaintResponseDTO>> listar() {
        var denuncias = complaintRepository.findAll();
        var dtos = denuncias.stream().map(this::toResponseDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar denúncia por ID", description = "Retorna os dados de uma denúncia específica pelo ID")
    public ResponseEntity<ComplaintResponseDTO> buscarPorId(@PathVariable Long id) {
        var denuncia = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Denúncia não encontrada"));
        return ResponseEntity.ok(toResponseDTO(denuncia));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar denúncia", description = "Atualiza os dados de uma denúncia existente")
    @CacheEvict(value = "denuncias", allEntries = true)
    public ResponseEntity<ComplaintResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid ComplaintRequestDTO dto) {
        var denuncia = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Denúncia não encontrada"));

        var usuario = userRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        var localizacao = locationRepository.findById(dto.getIdLocalizacao())
                .orElseThrow(() -> new RuntimeException("Localização não encontrada"));
        var orgao = publicOrganizationRepository.findById(dto.getIdOrgao())
                .orElseThrow(() -> new RuntimeException("Órgão não encontrado"));

        denuncia.setUsuario(usuario);
        denuncia.setLocalizacao(localizacao);
        denuncia.setOrgao(orgao);
        denuncia.setDataHora(dto.getDataHora());
        denuncia.setDescricao(dto.getDescricao());

        complaintRepository.save(denuncia);

        return ResponseEntity.ok(toResponseDTO(denuncia));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar denúncia", description = "Remove uma denúncia do sistema")
    @CacheEvict(value = "denuncias", allEntries = true)
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        var denuncia = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Denúncia não encontrada"));

        complaintRepository.delete(denuncia);
        return ResponseEntity.noContent().build();
    }

    private ComplaintResponseDTO toResponseDTO(Complaint denuncia) {
        return ComplaintResponseDTO.builder()
                .id(denuncia.getId())
                .nomeUsuario(denuncia.getUsuario().getNome())
                .descricao(denuncia.getDescricao())
                .dataHora(denuncia.getDataHora())
                .nomeOrgao(denuncia.getOrgao().getNome())
                .logradouro(denuncia.getLocalizacao().getLogradouro())
                .numero(denuncia.getLocalizacao().getNumero())
                .bairro(denuncia.getLocalizacao().getBairro().getNome())
                .cidade(denuncia.getLocalizacao().getBairro().getCidade().getNome())
                .estado(denuncia.getLocalizacao().getBairro().getCidade().getEstado().getNome())
                .build();
    }
}
