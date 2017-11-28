package sample.user;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * @author Rob Winch
 * @since 5.0
 */
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
	Mono<User> findByEmail(String email);
}
