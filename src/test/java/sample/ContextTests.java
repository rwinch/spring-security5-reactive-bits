package sample;

import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * @author Rob Winch
 * @since 5.0
 */
public class ContextTests {

	@Test
	public void helloWorld() {
		String key = "message";
		Mono<String> r = Mono.subscriberContext()
			.map(ctx -> ctx.<String>get(key))
			.subscriberContext(ctx -> ctx.put(key, "Hello World"));

		StepVerifier.create(r)
			.expectNext("Hello World")
			.verifyComplete();
	}

	@Test
	public void helloWorldMapped() {
		String key = "message";
		Mono<String> r = Mono.just("Hello")
			.flatMap(m -> Mono.subscriberContext()
				.map(ctx -> m + " " + ctx.get(key))
			)
			.subscriberContext(ctx -> ctx.put(key, "World"));

		StepVerifier.create(r)
			.expectNext("Hello World")
			.verifyComplete();
	}

	@Test
	public void helloStranger() {
		String key = "message";
		Mono<String> r = Mono.just("Hello")
				.subscriberContext(ctx -> ctx.put(key, "World"))
				.flatMap(m -> Mono.subscriberContext()
						.map(ctx -> m + " " + ctx.getOrDefault(key, "Stranger"))
				);

		StepVerifier.create(r)
				.expectNext("Hello Stranger")
				.verifyComplete();
	}

	@Test
	public void helloImmutable() {
		String key = "message";

		Mono<String> r = Mono.subscriberContext()
			.doOnSuccess( ctx -> ctx.put(key, "Hello"))
			.map( ctx -> ctx.getOrDefault(key,"Immutable"));

		StepVerifier.create(r)
			.expectNext("Immutable")
			.verifyComplete();
	}

	@Test
	public void helloOrderMatters() {
		String key = "message";
		Mono<String> r = Mono.just("Hello")
			.flatMap( s -> Mono.subscriberContext()
				.map( ctx -> s + " " + ctx.get(key))
			)
			.subscriberContext(ctx -> ctx.put(key, "Reactor"))
			.subscriberContext(ctx -> ctx.put(key, "World"));

		StepVerifier.create(r)
			.expectNext("Hello Reactor")
			.verifyComplete();
	}

	@Test
	public void helloReactorWorld() {
		String key = "message";
		Mono<String> r = Mono.just("Hello")
			.flatMap( s -> Mono.subscriberContext()
				.map( ctx -> s + " " + ctx.get(key))
			)
			.subscriberContext(ctx -> ctx.put(key, "Reactor"))
			.flatMap( s -> Mono.subscriberContext()
				.map( ctx -> s + " " + ctx.get(key))
			)
			.subscriberContext(ctx -> ctx.put(key, "World"));

		StepVerifier.create(r)
				.expectNext("Hello Reactor World")
				.verifyComplete();
	}

}
