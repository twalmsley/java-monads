package uk.co.aosd.monads;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

/**
 * Test the Writer Monad.
 */
public class WriterTest {
    private static record Log(Instant t, String s) {}

    private static final Function<String, String> space = s -> s + " ";
    private static final Function<String, String> hello = s -> s + "Hello";
    private static final Function<String, String> world = s -> s + "World";
    private static final Function<String, String> bang = s -> s + "!";
    private static final Function<String, String> newline = s -> s + "\n";
    private static final Function<String, Writer<Log, String>> spaceK = 
        s -> new Writer<>(space.apply(s), List.of(new Log(Instant.now(), "Added a space.")));
    private static final Function<String, Writer<Log, String>> helloK = 
        s -> new Writer<>(hello.apply(s), List.of(new Log(Instant.now(), "Added 'Hello'.")));
    private static final Function<String, Writer<Log, String>> worldK = 
        s -> new Writer<>(world.apply(s), List.of(new Log(Instant.now(), "Added 'World'.")));
    private static final Function<String, Writer<Log, String>> bangK = 
        s -> new Writer<>(bang.apply(s), List.of(new Log(Instant.now(), "Added '!'.")));
    private static final Function<String, Writer<Log, String>> newlineK = 
        s -> new Writer<>(newline.apply(s), List.of(new Log(Instant.now(), "Added '\\n'.")));
    private static final Writer<Log, String> blank = new Writer<>("");

    @Test
    public void test1() {

        final var result = blank
            .fmap(hello)
            .fmap(space)
            .fmap(world)
            .fmap(bang)
            .fmap(space)
            .fmap(hello)
            .fmap(space)
            .fmap(world)
            .fmap(bang)
            .fmap(bang)
            .fmap(newline);

        assertEquals("Hello World! Hello World!!\n", result.a);
    }

    @Test
    public void test2() {
        final var result = blank
            .compose(helloK)
            .compose(spaceK)
            .compose(worldK)
            .compose(bangK)
            .compose(newlineK);
        assertEquals("Hello World!\n", result.a);
        System.out.println(result.w);
    }

    @Test
    public void test3() {
        final var result = blank
            .compose(helloK)
            .compose(spaceK)
            .compose(worldK)
            .compose(bangK)
            .compose(newlineK)
            .compose(s -> new Writer<>(""))
            .compose(bangK);
        assertEquals("!", result.a);
        System.out.println(result.w);
    }
}

