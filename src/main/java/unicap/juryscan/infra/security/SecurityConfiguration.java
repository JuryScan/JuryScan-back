package unicap.juryscan.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import unicap.juryscan.config.CorsConfig;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final CorsConfigurationSource corsConfigurationSource;
    private final SecurityFilter securityFilter;

    public SecurityConfiguration(CorsConfig corsConfig, SecurityFilter securityFilter) {
        this.corsConfigurationSource = corsConfig.corsConfigurationSource();
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // Redirect pages (payment success)
                        .requestMatchers("/redirect/**").permitAll()

                        // Swagger endpoints
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()

                        // Actuator endpoints
                        .requestMatchers("/actuator/health/**").permitAll()
                        .requestMatchers("/actuator/info").permitAll()

                        // Api client endpoints
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/comum/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/advogado/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()

                        // Avatar público (carregado via tag <img>, sem header de Authorization)
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/avatar/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/comum/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/advogado/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/analyses/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/failures/**").authenticated()

                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/comum/**").hasAnyRole("COMUM", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/comum/**").hasAnyRole("COMUM", "ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/advogado/**").hasAnyRole("ADVOGADO", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/advogado/**").hasAnyRole("ADVOGADO", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/product-checkout/**").hasAnyRole("COMUM", "ADVOGADO", "ADMIN")

                        .requestMatchers("/api/v1/addresses/**").authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/v1/analyses/**").authenticated()

                        // Validar padrão de segurança em webhook
                        .requestMatchers(HttpMethod.POST, "/api/v1/webhook/**").permitAll()

                        .anyRequest().authenticated())
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}