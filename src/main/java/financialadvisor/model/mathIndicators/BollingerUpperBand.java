package financialadvisor.model.mathIndicators;

import financialadvisor.model.Candle;
import financialadvisor.model.mathIndicators.utils.MathUtils;
import financialadvisor.services.StockTradeMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BollingerUpperBand implements MathIndicator {

    @Autowired
    SMAIndicator sma;

    @Autowired
    StockTradeMgr stockTradeMgr;

    int period;
    int multiplier = 2;

    public BollingerUpperBand() {}

    public BollingerUpperBand(int period) {
        this.period = period;
    }

    @Override
    public double calculateIndicator(Candle candle) {
        if (period != 0){
            sma.setPeriod(period);
        }
        double smaValue = sma.calculateIndicator(candle);
        return smaValue + MathUtils.getStandardDeviation(stockTradeMgr.getStock().getCandles(), candle , period)*multiplier;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }
}
