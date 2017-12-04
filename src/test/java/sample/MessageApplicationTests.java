package sample;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class MessageApplicationTests {
	@Autowired
	WebTestClient client;

	@Test
	public void usersWhenNotAuthenticatedThenUnauthorized() {
		this.client.get()
				.uri("/users")
				.exchange()
				.expectStatus().isUnauthorized();
	}

	@Test
	@WithMockUser
	public void usersWhenUserThenOk() {
		this.client.get()
				.uri("/users")
				.exchange()
				.expectStatus().isOk();
	}
}
