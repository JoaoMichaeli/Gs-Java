package com.gs.EcoDenuncia.service;

import com.gs.EcoDenuncia.model.User;
import com.gs.EcoDenuncia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(User user){
        return userRepository.save(user);
    }

    public User getUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id " + id));
    }

    public List<User> listAllUsers(){
        return userRepository.findAll();
    }

    public User updateUser(Long id, User userDetails){
        User user = getUserById(id);

        user.setNome(userDetails.getNome());
        user.setEmail(userDetails.getEmail());
        user.setSenha(userDetails.getSenha());

        return userRepository.save(user);
    }

    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }
}
