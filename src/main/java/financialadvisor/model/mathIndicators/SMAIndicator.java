package financialadvisor.model.mathIndicators;

import financialadvisor.model.Candle;
import financialadvisor.model.mathIndicators.utils.MathUtils;
import financialadvisor.services.StockTradeMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SMAIndicator implements MathIndicator {

    private int period;

    @Autowired
    StockTradeMgr stockTradeMgr;

    public SMAIndicator() {this.period = 20;}

    public SMAIndicator(int period) {
        this.period = period;
    }

    @Override
    public double calculateIndicator(Candle candle) {
       return MathUtils.getAvg(stockTradeMgr.getStock().getCandles(), candle, period);
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }
}
