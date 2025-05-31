package com.gs.EcoDenuncia.controller;

import com.gs.EcoDenuncia.dto.User.UserRequestDTO;
import com.gs.EcoDenuncia.dto.User.UserResponseDTO;
import com.gs.EcoDenuncia.model.RoleType;
import com.gs.EcoDenuncia.model.User;
import com.gs.EcoDenuncia.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder encoder;

    // ✅ Cadastro público
    @PostMapping
    @Operation(summary = "Cadastrar usuário", description = "Cadastra um novo usuário com role USER ou ADMIN")
    @CacheEvict(value = "users", allEntries = true)
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserRequestDTO userDTO) {
        User user = new User();
        user.setNome(userDTO.getNome());
        user.setEmail(userDTO.getEmail());
        user.setSenha(encoder.encode(userDTO.getSenha()));

        // ✅ Converter String para Enum com tratamento
        try {
            user.setRole(userDTO.getRole());
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }

        User savedUser = repository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponseDTO(savedUser));
    }

    // ✅ Apenas ADMIN pode listar todos
    @GetMapping
    @Operation(summary = "Listar usuários cadastrados", description = "Retorna todos os usuários cadastrados (Apenas ADMIN)")
    @Cacheable("users")
    public ResponseEntity<?> listAllUsers(@AuthenticationPrincipal User userAuth) {
        if (!userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado.");
        }
        List<UserResponseDTO> dtos = repository.findAll()
                .stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ✅ Consultar próprio perfil ou ADMIN
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por Id", description = "Retorna um usuário pelo id (ADMIN ou dono do perfil)")
    public ResponseEntity<?> getUserById(@PathVariable Long id, @AuthenticationPrincipal User userAuth) {
        if (!userAuth.getId().equals(id) && !userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado.");
        }

        Optional<User> optionalUser = repository.findById(id);
        return optionalUser
                .map(user -> ResponseEntity.ok(new UserResponseDTO(user)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Atualizar próprio perfil ou ADMIN
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = """
        Atualiza os dados de um usuário existente. 
        Apenas usuários com role ADMIN podem alterar o campo 'role'.
        Usuários comuns (USER) podem atualizar apenas nome, email e senha.
        """
    )
    @CacheEvict(value = "users", allEntries = true)
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserRequestDTO userDTO,
            @AuthenticationPrincipal User userAuth
    ) {
        if (!userAuth.getId().equals(id) && !userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado.");
        }

        Optional<User> optionalUser = repository.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setNome(userDTO.getNome());
            user.setEmail(userDTO.getEmail());
            user.setSenha(encoder.encode(userDTO.getSenha()));

            if (userAuth.getRole().equals(RoleType.ADMIN)) {
                try {
                    user.setRole(userDTO.getRole());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body("Role inválido. Use ADMIN ou USER.");
                }
            }

            User updatedUser = repository.save(user);
            return ResponseEntity.ok(new UserResponseDTO(updatedUser));
        }

        return ResponseEntity.notFound().build();
    }

    // ✅ Deletar próprio perfil ou ADMIN
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar usuário", description = "Remove um usuário do sistema (ADMIN ou dono do perfil)")
    @CacheEvict(value = "users", allEntries = true)
    public ResponseEntity<?> deleteUser(@PathVariable Long id, @AuthenticationPrincipal User userAuth) {
        if (!userAuth.getId().equals(id) && !userAuth.getRole().equals(RoleType.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado.");
        }

        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
