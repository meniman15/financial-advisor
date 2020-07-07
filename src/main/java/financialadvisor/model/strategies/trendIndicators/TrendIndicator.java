package financialadvisor.model.strategies.trendIndicators;

import financialadvisor.model.Candle;
import org.springframework.stereotype.Component;

@Component
public interface TrendIndicator {

    boolean triggerTransaction(Candle currentCandle);
}
