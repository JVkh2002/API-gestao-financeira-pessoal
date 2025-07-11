// JwtUtil.java
package com.financas.security;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret:segredo-muito-seguro-deve-ser-alterado-em-producao}")
    private String SECRET;

    @Value("${jwt.expiration:3600000}") // 1 hora em milissegundos
    private long EXPIRATION;

    public String gerarToken(String email) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + EXPIRATION);

            logger.debug("Gerando token para usuário: {}", email);

            return Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(SignatureAlgorithm.HS256, SECRET)
                    .compact();

        } catch (Exception e) {
            logger.error("Erro ao gerar token JWT para usuário {}: {}", email, e.getMessage());
            throw new RuntimeException("Erro ao gerar token JWT", e);
        }
    }

    public String extrairEmail(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject();
            logger.debug("Email extraído do token: {}", email);
            return email;

        } catch (ExpiredJwtException e) {
            logger.warn("Token JWT expirado: {}", e.getMessage());
            return null;
        } catch (UnsupportedJwtException e) {
            logger.warn("Token JWT não suportado: {}", e.getMessage());
            return null;
        } catch (MalformedJwtException e) {
            logger.warn("Token JWT malformado: {}", e.getMessage());
            return null;
        } catch (SignatureException e) {
            logger.warn("Assinatura do token JWT inválida: {}", e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            logger.warn("Token JWT vazio ou nulo: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Erro inesperado ao extrair email do token: {}", e.getMessage());
            return null;
        }
    }

    public boolean validarToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();

            boolean isValid = !claims.getExpiration().before(new Date());
            logger.debug("Token JWT validado: {}", isValid ? "válido" : "expirado");
            return isValid;

        } catch (ExpiredJwtException e) {
            logger.warn("Token JWT expirado: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            logger.warn("Token JWT não suportado: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            logger.warn("Token JWT malformado: {}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            logger.warn("Assinatura do token JWT inválida: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            logger.warn("Token JWT vazio ou nulo: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Erro inesperado ao validar token: {}", e.getMessage());
            return false;
        }
    }

    public Date extrairDataExpiracao(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration();
        } catch (Exception e) {
            logger.error("Erro ao extrair data de expiração do token: {}", e.getMessage());
            return null;
        }
    }

    public boolean isTokenExpirado(String token) {
        Date expiration = extrairDataExpiracao(token);
        return expiration != null && expiration.before(new Date());
    }
}