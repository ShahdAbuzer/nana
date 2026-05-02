package org.project.projectstep1zanix.Security;

import java.util.Base64;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    public SecurityConfig(
            CustomUserDetailsService customUserDetailsService,
            RestAuthenticationEntryPoint restAuthenticationEntryPoint,
            RestAccessDeniedHandler restAccessDeniedHandler
    ) {
        this.customUserDetailsService = customUserDetailsService;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.restAccessDeniedHandler = restAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/login",
                                "/oauth2/**",
                                "/error",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/api/availability/check",
                                "/api/pricing/quote"
                        ).permitAll()

                        .requestMatchers("/actuator/health").permitAll()

                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/hotels/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/hotels/*/rooms/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/hotels/*/nearby-places/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/hotels/*/reviews/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/room-types/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/chapters/me").hasRole("GUEST")
                        .requestMatchers(HttpMethod.GET, "/api/chapters/public").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/chapters/visible-sections").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/chapters/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/hotels/*/stories").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/hotels/*/temporary-stories").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/hotels/*/temporary-stories").hasRole("GUEST")
                        .requestMatchers(HttpMethod.GET, "/api/temporary-stories/me").hasRole("GUEST")
                        .requestMatchers(HttpMethod.DELETE, "/api/temporary-stories/*").authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/chapters").hasRole("GUEST")
                        .requestMatchers(HttpMethod.POST, "/api/chapters/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/chapters/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/chapters/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/chapters/**").authenticated()

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/manager/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/api/bookings/**").hasAnyRole("GUEST", "ADMIN", "MANAGER")
                        .requestMatchers("/api/guest/**").hasAnyRole("GUEST", "ADMIN")

                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .defaultSuccessUrl("/profile", true)
                )
                .logout(logout -> logout.logoutSuccessUrl("/login"));

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> roles = jwt.getClaimAsStringList("roles");

            if (roles == null) {
                return List.of();
            }

            return roles.stream()
                    .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role))
                    .toList();
        });

        return converter;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(getSecretKey())
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}