package uk.co.aosd.monads;

import java.util.function.Function;

/**
 * Reader Monad.
 */
public class Reader<E, A> {
    private final Function<E, A> rf;

    private Reader(final Function<E, A> rf) {
        this.rf = rf;
    }

    public static <E, A> A runReader(final Reader<E, A> ra, final E e) {
        return ra.rf.apply(e);
    }

    /**
     * Monadic return function.
     *
     * @param <A> type parameter.
     * @param <E> type parameter.
     * @param a A
     * @return Reader
     */
    public static <A, E> Reader<E, A> ret(final A a) {
        final Function<E, A> f = e -> a;
        return new Reader<>(f);
    }

    public static <A, E> Reader<E, A> ret(final Function<E, A> f) {
        return new Reader<>(f);
    }

    /**
     * Same as >>=.
     */
    public <B> Reader<E, B> compose(
            final Function<A, Reader<E, B>> f) {
        final Function<E, B> feb = e -> {
            final var a = runReader(this, e);
            final var rb = f.apply(a);
            return runReader(rb, e);
        };
        return new Reader<>(feb);
    }

    /**
     * Same as >>.
     */
    public <B> Reader<E, B> compose(final Reader<E, B> rb) {
        final Function<E, B> feb = e -> {
            runReader(this, e);
            return runReader(rb, e);
        };
        return new Reader<>(feb);
    }
}
