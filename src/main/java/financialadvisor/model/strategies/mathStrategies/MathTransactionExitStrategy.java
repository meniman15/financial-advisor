package financialadvisor.model.strategies.mathStrategies;

import financialadvisor.model.strategies.candleStrategies.CandleTransactionExitStrategy;

public abstract class MathTransactionExitStrategy extends CandleTransactionExitStrategy {
    public MathTransactionExitStrategy(String name) {
        super(name);
    }
}
