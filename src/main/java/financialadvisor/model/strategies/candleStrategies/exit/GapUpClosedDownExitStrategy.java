package financialadvisor.model.strategies.candleStrategies.exit;

import financialadvisor.model.Candle;
import financialadvisor.model.strategies.candleStrategies.CandleTransactionExitStrategy;
import financialadvisor.services.StockTradeMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GapUpClosedDownExitStrategy extends CandleTransactionExitStrategy {

    @Autowired
    StockTradeMgr mgr;

    public GapUpClosedDownExitStrategy() {
        super("פער מחירים עולה שנסגר בירידה (גאפ שנסגר)");
    }

    @Override
    public boolean shouldTriggerTransaction(Candle candle) {
        return shouldTriggerTransaction(candle,mgr);
    }

    @Override
    public boolean shouldTriggerTransaction(Candle candle, StockTradeMgr mgr) {
        Candle candleBefore = mgr.getStock().getCandles().get(mgr.getStock().getCandles().indexOf(candle)-1);

        //candle opens higher than last candle high, and closes below the started gap (gap-up which closed as red candle)
        return (candle.getOpenRate() > candleBefore.getDailyHigh() && candle.isBearish());
    }
}

