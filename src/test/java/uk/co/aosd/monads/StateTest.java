package uk.co.aosd.monads;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.junit.jupiter.api.Test;


/**
 * Test the State monad.
 */
public class StateTest {
    private static final Tuple2<StileOutput, StileState> TUT_UNLCKD = Tuple.of(StileOutput.THANK, StileState.UNLCKD);
    private static final Tuple2<StileOutput, StileState> OPEN_LCKD = Tuple.of(StileOutput.OPEN, StileState.LCKD);
    private static final Tuple2<StileOutput, StileState> TUT_LCKD = Tuple.of(StileOutput.TUT, StileState.LCKD);
    private static final Tuple2<StileOutput, StileState> THANK_UNLCKD = Tuple.of(StileOutput.THANK, StileState.UNLCKD);

    private static final State<StileState, StileOutput> 
        insertCoin = new State<>(x -> {
            if (x == StileState.LCKD) {
                return THANK_UNLCKD;
            } else {
                return TUT_UNLCKD;
            }
        });

    private static final State<StileState, StileOutput> 
        pushTurnstile = new State<>(x -> {
            if (x == StileState.LCKD) {
                return TUT_LCKD;
            } else {
                return OPEN_LCKD;
            }
        });

    @Test
    public void test1() {
        final var steps = insertCoin
            .compose(a -> pushTurnstile)
            .compose(a -> pushTurnstile)
            .compose(a -> insertCoin)
            .compose(a -> pushTurnstile);
        assertEquals(OPEN_LCKD, steps.stateF.apply(StileState.UNLCKD));
    }

    @Test
    public void test2() {
        // Same as test1 except keeping track of intermediate outputs.
        final var steps = insertCoin
            .compose(a1 -> {
                return pushTurnstile.compose(a2 -> {
                    return pushTurnstile.compose(a3 -> {
                        return insertCoin.compose(a4 -> {
                            return pushTurnstile.compose(a5 -> {
                                return State.ret(List.of(a1, a2, a3, a4, a5));
                            });
                        });
                    });
                });
            });

        assertEquals(List.of(
                    StileOutput.THANK,
                    StileOutput.OPEN,
                    StileOutput.TUT,
                    StileOutput.THANK,
                    StileOutput.OPEN
                    ), steps.stateF.apply(StileState.UNLCKD)._1());
    }

    private static enum StileState {
        LCKD, UNLCKD
    }

    private static enum StileOutput {
        THANK, OPEN, TUT
    }

}
