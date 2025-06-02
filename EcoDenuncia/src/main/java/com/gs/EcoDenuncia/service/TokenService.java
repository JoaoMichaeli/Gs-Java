package com.gs.EcoDenuncia.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.gs.EcoDenuncia.model.RoleType;
import com.gs.EcoDenuncia.model.Token;
import com.gs.EcoDenuncia.model.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    private final Algorithm algorithm = Algorithm.HMAC256("secret");

    private Instant generateExpirationDate() {
        return LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.of("-03:00"));
    }

    public Token createToken(User user) {
        Instant expiresAt = LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.ofHours(-3));

        var jwt = JWT.create()
                .withSubject(user.getId().toString())
                .withClaim("email", user.getEmail())
                .withClaim("role", user.getRole().name())
                .withExpiresAt(expiresAt)
                .sign(algorithm);

        return new Token(jwt, user.getEmail());
    }


    public User getUserFromToken(String token) {
        var verifiedToken = JWT.require(algorithm).build().verify(token);

        return User.builder()
                .id(Long.valueOf(verifiedToken.getSubject()))
                .email(verifiedToken.getClaim("email").asString())
                .role(RoleType.valueOf(verifiedToken.getClaim("role").asString()))
                .build();
    }
}
