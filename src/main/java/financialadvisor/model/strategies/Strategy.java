package financialadvisor.model.strategies;

import financialadvisor.model.Candle;
import org.springframework.stereotype.Component;

@Component
public interface Strategy {
    //execute on all candles
    StrategyResult executeOnAll();
    StrategyResult execute(Candle candle, StrategyResult result);
    String getName();
}
