package sample.security;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import sample.user.User;
import sample.user.UserRepository;

/**
 * @author Rob Winch
 * @since 5.0
 */
@Component
public class PasswordUpgradeReactiveAuthenticationManager
		implements ReactiveAuthenticationManager {
	private final UserRepository users;
	private final ReactiveAuthenticationManager delegate;
	private final PasswordEncoder encoder;

	PasswordUpgradeReactiveAuthenticationManager(UserRepository users,
			ReactiveUserDetailsService userDetailsService, PasswordEncoder encoder) {
		this.users = users;
		this.delegate = createDelegate(userDetailsService, encoder);
		this.encoder = encoder;
	}

	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		return this.delegate.authenticate(authentication)
				.delayUntil(a -> updatePassword(authentication));
	}

	private Mono<User> updatePassword(Authentication authentication) {
		return this.users.findByEmail(authentication.getName())
				.publishOn(Schedulers.parallel()).doOnSuccess(u -> u.setPassword(
						this.encoder.encode(authentication.getCredentials().toString())))
				.flatMap(this.users::save);
	}

	private static ReactiveAuthenticationManager createDelegate(
			ReactiveUserDetailsService userDetailsService, PasswordEncoder encoder) {
		UserDetailsRepositoryReactiveAuthenticationManager result = new UserDetailsRepositoryReactiveAuthenticationManager(
				userDetailsService);
		result.setPasswordEncoder(encoder);
		return result;
	}
}
