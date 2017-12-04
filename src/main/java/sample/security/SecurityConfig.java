package sample.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

/**
 * @author Rob Winch
 * @since 5.0
 */
@Configuration
public class SecurityConfig {
	@Bean
	SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		http
			.authorizeExchange()
				.pathMatchers("/users").access(this::isRob)
				.pathMatchers("/login", "/signup", "/webjars/**").permitAll()
				.anyExchange().authenticated()
				.and()
			.httpBasic().and()
			.formLogin()
				.loginPage("/login");
		return http.build();
	}

	private Mono<AuthorizationDecision> isRob(Mono<Authentication> authentication,
			AuthorizationContext authorizationContext) {
		return authentication
				.map(Authentication::getName)
				.map(username -> username.startsWith("rob@"))
				.map(AuthorizationDecision::new);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}
