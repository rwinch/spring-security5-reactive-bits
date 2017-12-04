package sample.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Rob Winch
 * @since 5.0
 */
@Configuration
public class SecurityConfig {
	// @formatter:off
	@Bean
	MapReactiveUserDetailsService userDetailsService() {
		UserDetails rob = User.withUsername("rob@example.com")
				.password("password")
				.roles("USER")
				.build();
		return new MapReactiveUserDetailsService(rob);
	}
	// @formatter:on

	@Bean
	PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}
}
