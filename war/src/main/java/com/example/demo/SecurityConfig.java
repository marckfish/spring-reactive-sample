/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.web.server.HttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.MapUserDetailsRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

/**
 *
 * @author hantsy
 */
@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

	@Bean
	SecurityWebFilterChain springWebFilterChain(HttpSecurity http) throws Exception {
		return http
			.authorizeExchange()
				.pathMatchers(HttpMethod.GET, "/posts/**").permitAll()
                .pathMatchers(HttpMethod.DELETE, "/posts/**").hasRole("ADMIN")
				//.pathMatchers("/users/{user}/**").access(this::currentUserMatchesPath)
				.anyExchange().authenticated()
				.and()
			.build();
	}

	private Mono<AuthorizationDecision> currentUserMatchesPath(Mono<Authentication> authentication, AuthorizationContext context) {
		return authentication
			.map( a -> context.getVariables().get("user").equals(a.getName()))
			.map( granted -> new AuthorizationDecision(granted));
	}

	@Bean
	public MapUserDetailsRepository userDetailsRepository() {
		UserDetails user = User.withUsername("user").password("password").roles("USER").build();
		UserDetails admin = User.withUsername("admin").password("password").roles("USER","ADMIN").build();
		return new MapUserDetailsRepository(user, admin);
	}

}
