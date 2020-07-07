package financialadvisor.model.mathIndicators;

import financialadvisor.model.Candle;

public interface MathIndicator {
    double calculateIndicator(Candle candle);
}
