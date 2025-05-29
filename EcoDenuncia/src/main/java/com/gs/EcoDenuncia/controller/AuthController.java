package com.gs.EcoDenuncia.controller;

import com.gs.EcoDenuncia.model.Credentials;
import com.gs.EcoDenuncia.model.Token;
import com.gs.EcoDenuncia.model.User;
import com.gs.EcoDenuncia.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public Token login(@RequestBody Credentials credentials){
        var auth = new UsernamePasswordAuthenticationToken(credentials.email(), credentials.senha());
        var user = (User) authenticationManager.authenticate(auth).getPrincipal();

        return tokenService.createToken(user);
    }
}
