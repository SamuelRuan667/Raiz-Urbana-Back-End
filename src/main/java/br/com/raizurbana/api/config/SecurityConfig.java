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

                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/cadastro"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/solicitacoes/protocolo/**"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/solicitacoes"
                        ).hasRole("USUARIO")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/solicitacoes/usuario/**"
                        ).hasAnyRole(
                                "USUARIO",
                                "GESTOR",
                                "BOSS"
                        )

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/solicitacoes/gestor/**"
                        ).hasAnyRole(
                                "GESTOR",
                                "BOSS"
                        )

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/solicitacoes/*/status"
                        ).hasAnyRole(
                                "GESTOR",
                                "BOSS"
                        )

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/solicitacoes/*/agendar-visita"
                        ).hasAnyRole(
                                "GESTOR",
                                "BOSS"
                        )

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/solicitacoes"
                        ).hasRole("BOSS")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/solicitacoes/*/foto"
                        ).hasAnyRole(
                                "USUARIO",
                                "GESTOR",
                                "BOSS"
                        )

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/solicitacoes/*"
                        ).hasAnyRole(
                                "USUARIO",
                                "GESTOR",
                                "BOSS"
                        )

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/usuarios"
                        ).hasRole("BOSS")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/usuarios/*"
                        ).hasRole("BOSS")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/usuarios/*/gestor"
                        ).hasRole("BOSS")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/usuarios/*/usuario"
                        ).hasRole("BOSS")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/carteiras-capiba/minha"
                        ).hasRole("USUARIO")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/carteiras-capiba/minha/movimentacoes"
                        ).hasRole("USUARIO")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/recompensas"
                        ).hasRole("USUARIO")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/recompensas/meus-resgates"
                        ).hasRole("USUARIO")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/recompensas/*/resgatar"
                        ).hasRole("USUARIO")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/recompensas/*"
                        ).hasRole("USUARIO")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}