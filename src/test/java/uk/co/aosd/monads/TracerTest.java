package uk.co.aosd.monads;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Function;

import org.junit.jupiter.api.Test;

/**
 * Test the Tracer Monad.
 */
public class TracerTest {
    private static final Function<String, String> space = s -> s + " ";
    private static final Function<String, String> hello = s -> s + "Hello";
    private static final Function<String, String> world = s -> s + "World";
    private static final Function<String, String> bang = s -> s + "!";
    private static final Function<String, String> newline = s -> s + "\n";
    private static final Function<String, Tracer<String>> spaceK = s -> Tracer.ret(space.apply(s));
    private static final Function<String, Tracer<String>> helloK = s -> Tracer.ret(hello.apply(s));
    private static final Function<String, Tracer<String>> worldK = s -> Tracer.ret(world.apply(s));
    private static final Function<String, Tracer<String>> bangK = s -> Tracer.ret(bang.apply(s));
    private static final Function<String, Tracer<String>> newlineK = s -> Tracer.ret(newline.apply(s));
    private static final Tracer<String> blank = Tracer.ret("");

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

        assertEquals("Hello World! Hello World!!\n", result.aa);
    }

    @Test
    public void test2() {
        final var result = blank
            .compose(helloK)
            .compose(spaceK)
            .compose(worldK)
            .compose(bangK)
            .compose(newlineK);
        assertEquals("Hello World!\n", result.aa);
    }

    @Test
    public void test3() {
        final var result = blank
            .compose(helloK)
            .compose(spaceK)
            .compose(worldK)
            .compose(bangK)
            .compose(newlineK)
            .compose(Tracer.ret(""))
            .compose(bangK);
        assertEquals("!", result.aa);
    }
}
