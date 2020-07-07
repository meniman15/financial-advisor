package financialadvisor.model.strategies.mathStrategies;

import financialadvisor.model.strategies.candleStrategies.CandleTransactionEntryStrategy;

public abstract class MathTransactionEntryStrategy extends CandleTransactionEntryStrategy {
    public MathTransactionEntryStrategy(String name) {
        super(name);
    }
}
