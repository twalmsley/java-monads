package uk.co.aosd.monads;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Tracer class, basically a Writer monad.
 */
public class Tracer<A> {
    public final List<String> ss;
    public final A aa;

    private Tracer(final List<String> ss, final A a) {
        this.ss = ss;
        this.aa = a;
    }

    public static <A> Tracer<A> ret(final A a) {
        return new Tracer<A>(new ArrayList<>(), a);
    }

    public String toString() {
        return "Tracer {a:" + aa + ", ss:" + ss + "}";
    }

    /**
     * fmap.
     *
     * @param <B> type parameter.
     * @param f Function
     * @return Tracer
     */
    public <B> Tracer<B> fmap(final Function<A, B> f) {
        final Function<A, Tracer<B>> f2 = x -> {
            final var b = f.apply(x);

            final var ss = new ArrayList<String>(this.ss);
            ss.add("f: " + aa + " -> " + b + "\n");
            return new Tracer<>(ss, b);
        };
        return f2.apply(aa);
    }

    /**
     * >>= operator.
     *
     * @param <B> type parameter.
     * @param f Function A -> Tracer B
     * @return Function
     */
    public <B> Tracer<B> compose(final Function<A, Tracer<B>> f) {
        final var p1 = f.apply(aa);

        final var ss = new ArrayList<>(this.ss);
        ss.add("f: " + aa + " -> " + p1.aa + "\n");
        ss.addAll(p1.ss);
        return new Tracer<B>(ss, p1.aa);
    }

    /**
     * >> operator.
     *
     * @param <B> type parameter.
     * @param  tb Tracer B
     * @return Function
     */
    public <B> Tracer<B> compose(final Tracer<B> tb) {
        final var ss = new ArrayList<>(this.ss);
        ss.addAll(tb.ss);
        // Discard this.a but pass along the log trace.
        return  new Tracer<>(ss, tb.aa);
    }
}

