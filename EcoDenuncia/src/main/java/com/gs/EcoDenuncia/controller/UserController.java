package com.gs.EcoDenuncia.controller;

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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("users")
@Slf4j
public class UserController {

    @Autowired
    private UserRepository repository;

    @PostMapping
    @Operation(summary = "Cadastrar usuário", description = "Cadastra um novo usuário")
    @CacheEvict(value = "users", allEntries = true)
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<User> createUser(@RequestBody @Valid User user, @AuthenticationPrincipal User authenticatedUser){
        log.info("Usuário {} está criando um novo usuário {}", authenticatedUser.getUsername(), user.getUsername());
        User savedUser = repository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping
    @Operation(summary = "Listar usuários cadastrados", description = "Retorna um array com todos os usuários cadastrados")
    @Cacheable("users")
    public ResponseEntity<List<User>> listAllUsers(){
        List<User> users = repository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("{id}")
    @Operation(summary = "Buscar usuário por Id", description = "Retorna um usuário com base no id")
    public ResponseEntity<User> getUserById(@PathVariable Long id, @AuthenticationPrincipal User authenticatedUser){
        Optional<User> optionalUser = repository.findById(id);
        if (optionalUser.isPresent()) {
            log.info("Usuário {} consultou o usuário {}", authenticatedUser.getUsername(), id);
            return ResponseEntity.ok(optionalUser.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    @CacheEvict(value = "users", allEntries = true)
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody @Valid User userDetails, @AuthenticationPrincipal User authenticatedUser) {
        Optional<User> optionalUser = repository.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setNome(userDetails.getUsername());
            user.setEmail(userDetails.getEmail());
            user.setSenha(userDetails.getPassword());

            User updatedUser = repository.save(user);
            log.info("Usuário {} atualizou o usuário {}", authenticatedUser.getUsername(), id);
            return ResponseEntity.ok(updatedUser);
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Deletar usuário", description = "Remove um usuário do sistema")
    @CacheEvict(value = "users", allEntries = true)
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, @AuthenticationPrincipal User authenticatedUser) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            log.info("Usuário {} deletou o usuário {}", authenticatedUser.getUsername(), id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Consultar usuário autenticado", description = "Retorna os dados do usuário autenticado")
    public ResponseEntity<User> getAuthenticatedUser(@AuthenticationPrincipal User authenticatedUser) {
        return ResponseEntity.ok(authenticatedUser);
    }
}
