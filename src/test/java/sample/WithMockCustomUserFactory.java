package sample;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import reactor.core.publisher.Mono;
import sample.security.RepositoryReactiveUserDetailsService;
import sample.user.User;
import sample.user.UserRepository;

import java.lang.annotation.Annotation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Rob Winch
 * @since 5.0
 */
public class WithMockCustomUserFactory implements WithSecurityContextFactory<WithMockCustomUser> {
	@Override
	public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
		UserRepository users = mock(UserRepository.class);
		when(users.findByEmail(any())).thenReturn(createUser(annotation));

		RepositoryReactiveUserDetailsService userDetailsService =
				new RepositoryReactiveUserDetailsService(users);
		UserDetails principal =
				userDetailsService.findByUsername(annotation.value()).block();

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(new UsernamePasswordAuthenticationToken(principal,
				principal.getPassword(), principal.getAuthorities()));
		return context;
	}

	private Mono<User> createUser(WithMockCustomUser annotation) {
		return Mono
				.just(new User(annotation.id(), annotation.value(), "notused", "First",
						"Last"));
	}
}
