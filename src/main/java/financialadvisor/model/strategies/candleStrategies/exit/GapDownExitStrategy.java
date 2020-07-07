package financialadvisor.model.strategies.candleStrategies.exit;

import financialadvisor.model.Candle;
import financialadvisor.model.strategies.candleStrategies.CandleTransactionExitStrategy;
import financialadvisor.services.StockTradeMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GapDownExitStrategy extends CandleTransactionExitStrategy {

    @Autowired
    StockTradeMgr mgr;

    public GapDownExitStrategy() {
        super("פער מחירים יורד (גאפ)");
    }

    @Override
    public boolean shouldTriggerTransaction(Candle candle) {
        Candle candleBefore = mgr.getStock().getCandles().get(mgr.getStock().getCandles().indexOf(candle)-1);

        //candle opens lower than last candle low, and closes below the started gap (gap-down which continue to fall)
        return (candle.getOpenRate() < candleBefore.getDailyLow() && candle.getClosingRate() < candle.getOpenRate());
    }

    @Override
    public boolean shouldTriggerTransaction(Candle candle, StockTradeMgr mgr) {
        Candle candleBefore = mgr.getStock().getCandles().get(mgr.getStock().getCandles().indexOf(candle)-1);

        //candle opens lower than last candle low, and closes below the started gap (gap-down which continue to fall)
        return (candle.getOpenRate() < candleBefore.getDailyLow() && candle.getClosingRate() < candle.getOpenRate());
    }
}
