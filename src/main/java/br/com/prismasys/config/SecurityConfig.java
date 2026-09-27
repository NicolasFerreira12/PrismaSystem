package br.com.prismasys.config;

import br.com.prismasys.service.AutenticacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyAuthoritiesMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AutenticacaoService autenticacaoService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Espelha a "Hierarquia de atores" do diagrama UC:
     * o Administrador generaliza (herda o acesso de) todos os outros perfis.
     * Aplicada na autenticação: ao logar, um Administrador recebe automaticamente
     * também as authorities dos demais perfis.
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role("ADMINISTRADOR").implies("VENDEDOR")
                .role("ADMINISTRADOR").implies("FINANCEIRO")
                .role("ADMINISTRADOR").implies("ESTOQUISTA")
                .role("ADMINISTRADOR").implies("PRODUCAO")
                .build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(autenticacaoService);
        provider.setPasswordEncoder(passwordEncoder());
        provider.setAuthoritiesMapper(new RoleHierarchyAuthoritiesMapper(roleHierarchy()));
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/login?error", "/login?logout", "/css/**", "/js/**").permitAll()
                        // Cadastrar Funcionários / Controlar Acesso -> uso exclusivo do Administrador
                        .requestMatchers("/funcionarios/**").hasRole("ADMINISTRADOR")
                        // módulos futuros, já mapeados por perfil conforme o UC
                        .requestMatchers("/estoque/**").hasRole("ESTOQUISTA")
                        .requestMatchers("/producao/**").hasRole("PRODUCAO")
                        .requestMatchers("/pedidos/**", "/vendas/**", "/clientes/**").hasRole("VENDEDOR")
                        .requestMatchers("/financeiro/**", "/relatorios/**").hasRole("FINANCEIRO")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}