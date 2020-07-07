package financialadvisor.model.strategies.candleStrategies.entry;

import financialadvisor.model.Candle;
import financialadvisor.model.strategies.candleStrategies.CandleTransactionEntryStrategy;
import financialadvisor.services.StockTradeMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class BuyAtFirstEntryStrategy extends CandleTransactionEntryStrategy {

    @Autowired
    StockTradeMgr mgr;

    public BuyAtFirstEntryStrategy() {
        super("קנה ביום הראשון");
    }

    @Override
    public boolean shouldTriggerTransaction(Candle candle) {
        return shouldTriggerTransaction(candle,mgr);
    }

    @Override
    public boolean shouldTriggerTransaction(Candle candle, StockTradeMgr mgr) {
        return (mgr.getStock().getCandles().get(0).equals(candle));
    }
}
