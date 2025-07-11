package com.financas.security;

import com.financas.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;

    public JwtFilter(JwtUtil jwtUtil, UsuarioService usuarioService) {
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        logger.debug("Processando filtro JWT para: {}", requestPath);

        // Pular filtro para endpoints públicos
        if (isPublicEndpoint(requestPath)) {
            logger.debug("Endpoint público detectado, pulando filtro JWT: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = getTokenFromRequest(request);

            if (token != null) {
                logger.debug("Token JWT encontrado, validando...");
                String email = jwtUtil.extrairEmail(token);

                if (email != null && jwtUtil.validarToken(token) &&
                        SecurityContextHolder.getContext().getAuthentication() == null) {

                    logger.debug("Token válido para usuário: {}", email);

                    UserDetails userDetails = usuarioService.loadUserByUsername(email);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    logger.debug("Autenticação configurada para usuário: {}", email);
                }
            } else {
                logger.debug("Nenhum token JWT encontrado na requisição");
            }

        } catch (Exception e) {
            logger.error("Erro ao processar token JWT: {}", e.getMessage());
            // Não definir autenticação em caso de erro
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/auth/") ||
                path.equals("/error") ||
                path.equals("/ping") ||
                path.startsWith("/actuator/") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs/") ||
                path.startsWith("/swagger-resources/") ||
                path.startsWith("/webjars/");
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}