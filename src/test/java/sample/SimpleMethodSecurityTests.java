package sample;

import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

import java.nio.file.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Rob Winch
 * @since 5.0
 */
public class SimpleMethodSecurityTests {

	interface MessageService {
		Mono<String> getMessage();
	}

	static class HelloMessageService implements MessageService {
		@Override
		public Mono<String> getMessage() {
			return Mono.just("Hello World");
		}
	}

	@Test
	public void helloMessage() {
		MessageService messages = new HelloMessageService();

		Mono<String> r = messages.getMessage();

		StepVerifier.create(r)
				.expectNext("Hello World")
				.verifyComplete();
	}

	static class SecuredMessageService implements MessageService {
		private final MessageService delegate;

		SecuredMessageService(MessageService delegate) {
			this.delegate = delegate;
		}

		@Override
		public Mono<String> getMessage() {
			return Mono.subscriberContext()
					.map(ctx -> ctx.getOrDefault("user", "anonymous"))
					.filter(user -> user.equals("rob"))
					.switchIfEmpty(Mono.error(new AccessDeniedException("Denied")))
					.flatMap(user -> this.delegate.getMessage());
		}
	}

	@Test
	public void helloSecuredMessageWhenGranted() {
		MessageService messages = new SecuredMessageService(new HelloMessageService());

		Mono<String> r = messages.getMessage()
			.subscriberContext(Context.of("user", "rob"));

		StepVerifier.create(r)
				.expectNext("Hello World")
				.verifyComplete();
	}

	@Test
	public void helloSecuredMessageWhenDenied() {
		MessageService messages = new SecuredMessageService(new HelloMessageService());

		Mono<String> r = messages.getMessage()
			.subscriberContext(Context.of("user", "evil"));

		StepVerifier.create(r)
				.expectError(AccessDeniedException.class);
	}

	// ReactorContextWebFilter

	@Test
	public void helloSecuredMessageWhenBlocked() {
		MessageService messages = new SecuredMessageService(new HelloMessageService());

		Mono<String> r = messages.getMessage()
				.subscriberContext(Context.of("user", "rob"));

		assertThat(r.block()).isEqualTo("Hello World");
	}

	static class BlockingService {
		private final MessageService messages = new SecuredMessageService(new HelloMessageService());

		String blockMessage() {
			return this.messages.getMessage().block();
		}
	}

	@Test
	public void blockingService() {
		BlockingService service = new BlockingService();

		assertThatThrownBy(() ->
				service.blockMessage()
				// Cannot use subscriberContext because not Mono
				// .subscriberContext(Context.of("user", "rob"))
			)
			.hasCauseInstanceOf(AccessDeniedException.class);
	}

	static class MessageController {
		BlockingService service = new BlockingService();

		Mono<String> getMessage() {
			return Mono.fromCallable(() -> this.service.blockMessage());
		}
	}

	@Test
	public void controllerThatBlocks() {
		MessageController controller = new MessageController();

		Mono<String> r = controller.getMessage()
				.subscriberContext(Context.of("user", "rob"));

		StepVerifier.create(r)
				.expectError(AccessDeniedException.class);
	}
}
