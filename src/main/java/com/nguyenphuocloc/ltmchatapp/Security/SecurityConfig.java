package com.nguyenphuocloc.ltmchatapp.Security;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.nguyenphuocloc.ltmchatapp.JWT.JwtFilterToken;

import io.micrometer.common.lang.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired JwtFilterToken jwtFilter;

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(){
        return new CustomUserDetailsService();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(userDetailsService());
        return authProvider;
    }

    @Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

    @Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth.requestMatchers("/api/auth/**").permitAll()
				// .requestMatchers(HttpMethod.GET, "/api/students").hasAnyAuthority("read", "write")
				.requestMatchers(HttpMethod.PATCH, "/api/admin/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.POST, "/api/admin/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.POST, "/api/chat/**").hasRole("USER")
				.requestMatchers(HttpMethod.GET, "/api/chat/**").hasRole("USER")
				// .requestMatchers(HttpMethod.PUT, "/api/students").hasRole("write")
				.anyRequest().authenticated())
		.csrf(csrf -> csrf.disable())
		.exceptionHandling(exh -> exh.authenticationEntryPoint(
				(request, response, exception) -> {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
				}
				))
		.addFilterBefore(jwtFilter, AuthorizationFilter.class)
		.csrf(csrf -> csrf.disable())
		.cors(cors -> cors.configurationSource(corsConfigurationSource()));

		;
		
		return http.build();
	}


	private CorsConfigurationSource corsConfigurationSource() {

		return new CorsConfigurationSource() {

			@Override
			@Nullable
			public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

				CorsConfiguration cfg = new CorsConfiguration();

				cfg.setAllowedOrigins(Arrays.asList(
						"http://localhost:5173/"));

				cfg.setAllowedMethods(Collections.singletonList("*"));
				cfg.setAllowCredentials(true);
				cfg.setAllowedHeaders(Collections.singletonList("*"));
				cfg.setExposedHeaders(Arrays.asList(
						"Authorization"));
				cfg.setMaxAge(3600L);

				return cfg;
			}
		};

	}

    
}
