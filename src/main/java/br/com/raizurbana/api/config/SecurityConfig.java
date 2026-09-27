package br.com.raizurbana.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .cors(cors -> {})

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // ==========================================
                        // AUTENTICAÇÃO - ROTAS PÚBLICAS
                        // ==========================================

                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/cadastro"
                        ).permitAll()


                        // ==========================================
                        // CONSULTA PÚBLICA POR PROTOCOLO
                        // ==========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/solicitacoes/protocolo/**"
                        ).permitAll()


                        // ==========================================
                        // CRIAR SOLICITAÇÃO
                        // SOMENTE USUARIO
                        // ==========================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/solicitacoes"
                        ).hasRole("USUARIO")


                        // ==========================================
                        // SOLICITAÇÕES DO USUÁRIO
                        // ==========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/solicitacoes/usuario/**"
                        ).hasAnyRole(
                                "USUARIO",
                                "GESTOR",
                                "BOSS"
                        )


                        // ==========================================
                        // ÁREA DO GESTOR
                        // ==========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/solicitacoes/gestor/**"
                        ).hasAnyRole(
                                "GESTOR",
                                "BOSS"
                        )


                        // ==========================================
                        // ALTERAR STATUS
                        // ==========================================

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/solicitacoes/*/status"
                        ).hasAnyRole(
                                "GESTOR",
                                "BOSS"
                        )


                        // ==========================================
                        // AGENDAR VISITA TÉCNICA
                        // ==========================================

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/solicitacoes/*/agendar-visita"
                        ).hasAnyRole(
                                "GESTOR",
                                "BOSS"
                        )


                        // ==========================================
                        // LISTAR TODAS AS SOLICITAÇÕES
                        // SOMENTE BOSS
                        // ==========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/solicitacoes"
                        ).hasRole("BOSS")


                        // ==========================================
                        // ACESSAR FOTO
                        // ==========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/solicitacoes/*/foto"
                        ).hasAnyRole(
                                "USUARIO",
                                "GESTOR",
                                "BOSS"
                        )


                        // ==========================================
                        // BUSCAR SOLICITAÇÃO POR ID
                        // ==========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/solicitacoes/*"
                        ).hasAnyRole(
                                "USUARIO",
                                "GESTOR",
                                "BOSS"
                        )


                        // ==========================================
                        // LISTAR TODOS OS USUÁRIOS
                        // SOMENTE BOSS
                        // ==========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/usuarios"
                        ).hasRole("BOSS")


                        // ==========================================
                        // BUSCAR USUÁRIO POR ID
                        // SOMENTE BOSS
                        // ==========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/usuarios/*"
                        ).hasRole("BOSS")


                        // ==========================================
                        // TRANSFORMAR USUÁRIO EM GESTOR
                        // SOMENTE BOSS
                        // ==========================================

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/usuarios/*/gestor"
                        ).hasRole("BOSS")


                        // ==========================================
                        // TRANSFORMAR GESTOR EM USUÁRIO
                        // SOMENTE BOSS
                        // ==========================================

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/usuarios/*/usuario"
                        ).hasRole("BOSS")


                        // ==========================================
                        // DEMAIS ENDPOINTS
                        // ==========================================

                        .anyRequest().permitAll()
                )


                // ==========================================
                // FILTRO JWT
                // ==========================================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}