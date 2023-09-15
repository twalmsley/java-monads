package uk.co.aosd.monads;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;

/**
 * A Writer Monad. Not as generic as it could be since it can't use any old Monoid.
 */
@RequiredArgsConstructor
public class Writer<W, A> {
    public final A a;
    public final List<W> w;

    /**
     * Constructor.
     *
     * @param a A
     */
    public Writer(final A a) {
        this.a = a;
        this.w = List.of();
    }

    /**
     * Compose this Writer with a function that produces a Writer.
     *
     * @param <B> The result type of the function.
     * @param k Function.
     * @return Writer.
     */
    public <B> Writer<W, B> compose(final Function<A, Writer<W, B>> k) {
        final var result = k.apply(a);
        final var newW = new ArrayList<W>();
        newW.addAll(w);
        newW.addAll(result.w);
        return new Writer<W, B>(result.a, newW);
    }

    /**
     * fmap.
     *
     * @param <B> type parameter.
     * @param f Function
     * @return Tracer
     */
    public <B> Writer<W, B> fmap(final Function<A, B> f) {
        final Function<A, Writer<W, B>> f2 = x -> {
            final var b = f.apply(x);

            return new Writer<>(b, w);
        };
        return f2.apply(a);
    }

}
