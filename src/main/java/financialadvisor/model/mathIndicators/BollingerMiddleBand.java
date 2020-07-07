package financialadvisor.model.mathIndicators;

import financialadvisor.model.Candle;
import financialadvisor.services.StockTradeMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BollingerMiddleBand implements MathIndicator {

    @Autowired
    SMAIndicator sma;

    @Autowired
    StockTradeMgr stockTradeMgr;

    int period;

    public BollingerMiddleBand() {}

    public BollingerMiddleBand(int period) {
        this.period = period;
    }

    @Override
    public double calculateIndicator(Candle candle) {
        if (period != 0){
            sma.setPeriod(period);
        }
        return sma.calculateIndicator(candle);
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }
}