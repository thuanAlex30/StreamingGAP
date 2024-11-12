package com.fpt.StreamGAP.config;

import com.fpt.StreamGAP.service.userDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private userDetailsService UserDetailsService;

    @Autowired
    private JWTAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(request ->
                        request
                                .requestMatchers("/auth/**", "/public/**", "/ws/**").permitAll() // Cho phép truy cập các đường dẫn này mà không cần xác thực
                                .requestMatchers("/songs/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.POST, "/songs/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/songs/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/songs/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/songs/**").hasAuthority("ADMIN")
                                .requestMatchers("/account-settings/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.POST, "/account-settings/**").hasAuthority("USER")
                                .requestMatchers(HttpMethod.GET, "/account-settings/").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.PUT, "/account-settings/**").hasAuthority("USER")
                                .requestMatchers(HttpMethod.DELETE, "/account-settings/**").hasAuthority("USER")
                                .requestMatchers("/albums/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.POST, "/albums/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/albums/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.PUT, "/albums/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/albums/**").hasAuthority("ADMIN")
                                .requestMatchers("/artists/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.POST, "/artists/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/artists/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.PUT, "/artists/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/artists/**").hasAuthority("ADMIN")
                                .requestMatchers("/playlists/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.POST, "/playlists/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/playlists/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.PUT, "/playlists/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/playlists/**").hasAuthority("ADMIN")
                                .requestMatchers("/playlistsongs/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.POST, "/playlistsongs/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/playlistsongs/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.PUT, "/playlistsongs/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/playlistsongs/**").hasAuthority("ADMIN")
                                .requestMatchers("/partysongs/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.POST, "/partysongs/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/partysongs/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/partysongs/**").hasAuthority("ADMIN")
                                .requestMatchers("/partymodes/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.POST, "/partymodes/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/partymodes/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.PUT, "/partymodes/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/partymodes/**").hasAuthority("ADMIN")
                                .requestMatchers("/musicgames/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.POST, "/musicgames/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/musicgames/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.PUT, "/musicgames/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/musicgames/**").hasAuthority("ADMIN")
                                .requestMatchers("/karaoke-sessions/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.POST, "/karaoke-sessions/**").hasAuthority("USER")
                                .requestMatchers(HttpMethod.GET, "/karaoke-sessions/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.PUT, "/karaoke-sessions/**").hasAuthority("USER")
                                .requestMatchers(HttpMethod.DELETE, "/karaoke-sessions/**").hasAuthority("ADMIN")
                                .requestMatchers("/favorite-songs/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.POST, "/favorite-songs/**").hasAuthority("USER")
                                .requestMatchers(HttpMethod.GET, "/favorite-songs/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.DELETE, "/favorite-songs/**").hasAuthority("ADMIN")
                                .requestMatchers("/songsStats/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.POST, "/songsStats/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/songsStats/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.PUT, "/songsStats/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/songsStats/**").hasAuthority("ADMIN")
                                .requestMatchers("/comments/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.DELETE, "/comments/**").hasAuthority("ADMIN")
                                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                                .requestMatchers("/user/**").hasAuthority("USER")
                                .requestMatchers("/adminuser/**").hasAnyAuthority("ADMIN", "USER")
                                .anyRequest().authenticated()
                )
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(UserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
