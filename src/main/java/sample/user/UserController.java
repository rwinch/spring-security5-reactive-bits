package sample.user;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author Rob Winch
 */
@RestController
@RequestMapping(path="/users", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UserController {
	private final UserRepository users;

	public UserController(UserRepository users) {
		this.users = users;
	}

	@GetMapping
	public Flux<User> users() {
		return this.users.findAll();
	}
}
