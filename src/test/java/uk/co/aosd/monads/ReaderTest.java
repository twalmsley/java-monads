package uk.co.aosd.monads;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

/**
 * Test the Reader Monad.
 */
public class ReaderTest {

    @Test
    public void test1() {
        final Reader<String, String> ra = Reader.ret("Hello ");
        final Function<String, Reader<String, String>> f1 = s -> Reader.ret(env -> s + env);
        final Function<String, Reader<String, String>> f2
            = s -> Reader.ret(env -> s + " This is the end of the " + env);

        final var rb = ra
            .compose(f1)
            .compose(f2);
        assertEquals("Hello World! This is the end of the World!", Reader.runReader(rb, "World!"));
    }

    @Test
    public void test2() {
        final Reader<Map<String, String>, String> blank = Reader.ret("");
        final Function<String, Reader<Map<String, String>, String>> one = s -> Reader.ret(env -> s + env.get("one"));
        final Function<String, Reader<Map<String, String>, String>> two = s -> Reader.ret(env -> s + env.get("two"));
        final Function<String, Reader<Map<String, String>, String>> thr = s -> Reader.ret(env -> s + env.get("three"));
        final Function<String, Reader<Map<String, String>, String>> comma = s -> Reader.ret(env -> s + ", ");
        final Function<String, Reader<Map<String, String>, String>> stop = s -> Reader.ret(env -> s + ".");

        final var program = blank
            .compose(one)
            .compose(comma)
            .compose(two)
            .compose(comma)
            .compose(thr)
            .compose(stop);

        // Run rb with two different environments.
        final var env1 = Map.of(
                "one", "first",
                "two", "second",
                "three", "third"
                );

        final var env2 = Map.of(
                "one", "A",
                "two", "B",
                "three", "C"
                );

        assertEquals("first, second, third.", Reader.runReader(program, env1));
        assertEquals("A, B, C.", Reader.runReader(program, env2));
    }

    @Test
    public void test3() {
        final Reader<String, String> ra = Reader.ret("Hello ");
        final Function<String, Reader<String, String>> appendEnv = s -> Reader.ret(env -> s + env);
        final Reader<String, String> ignoreParamJustReturnEnvString = Reader.ret(s -> {
            System.out.println("2:" + s); // A side-effect
            return s;
        });

        // Compose f1 and rc - rc discards the first result of f1.
        // The second f1 uses the result of rc.
        final var rd = ra
            .compose(appendEnv)  // result is discarded.
            .compose(ignoreParamJustReturnEnvString)  // rc returns "World!" from the environment.
            .compose(appendEnv); // f1 now appends "World!" to "World!".
        final var env = "World!";
        assertEquals("World!World!", Reader.runReader(rd, env));
    }

}
