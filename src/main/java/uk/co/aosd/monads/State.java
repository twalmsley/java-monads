package uk.co.aosd.monads;

import java.util.function.Function;

import io.vavr.Tuple;
import io.vavr.Tuple2;

/**
 * State monad.
 *
 * @author Tony Walmsley.
 */
public class State<S, A> {

    // The state function.
    public Function<S, Tuple2<A, S>> stateF;

    /**
     * Constructor accepting a state function.
     *
     * @param fsa Function S -> Tuple2 of A and S.
     */
    public State(final Function<S, Tuple2<A, S>> fsa) {
        this.stateF = fsa;
    }

    /**
     * Apply the state function and return the result.
     *
     * @param s object of type S
     * @return object of type A
     */
    public A evalState(final S s) {
        return stateF.apply(s)._1();
    }

    /**
     * Apply the state function and return the new state.
     *
     * @param s object of type S
     * @return object of type S
     */
    public S execState(final S s) {
        return stateF.apply(s)._2();
    }

    /**
     * The Haskell return function.
     *
     * @param <S> the state type.
     * @param <A> the result type.
     * @param a an A
     * @return a State of S and A.
     */
    public static <S, A> State<S, A> ret(final A a) {
        return new State<>(s -> Tuple.of(a, s));
    }

    /**
     * Compose a function with the State monad.
     *
     * @param <B> the new result type.
     * @param f a Function A -> B.
     * @return a new State monad.
     */
    public <B> State<S, B> map(final Function<A, B> f) {
        return new State<>(s -> {
            final var pair = stateF.apply(s);
            final var b = f.apply(pair._1());
            return Tuple.of(b, pair._2());
        });
    }

    /**
     * Equivalent of Haskell bind >>=.
     *
     * @param <B> the new result type.
     * @param f a state function.
     * @return a new State.
     */
    public <B> State<S, B> compose(final Function<A, State<S, B>> f) {
        final Function<S, Tuple2<B, S>> result = s0 -> {
            final var pp = stateF.apply(s0);
            return f
                .apply(pp._1())
                .stateF
                .apply(pp._2());
        };
        return new State<S, B>(result);
    }
}
