package financialadvisor.model.strategies.candleStrategies.exit;

import financialadvisor.model.Candle;
import financialadvisor.model.strategies.candleStrategies.CandleTransactionExitStrategy;
import financialadvisor.services.StockTradeMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SellAtLastExitStrategy extends CandleTransactionExitStrategy {

    public static String SELL_AT_LAST_DAY = "מכור ביום האחרון";

    @Autowired
    StockTradeMgr mgr;

    public SellAtLastExitStrategy() {
        super("מכור ביום האחרון");
    }

    @Override
    public boolean shouldTriggerTransaction(Candle candle) {
        return shouldTriggerTransaction(candle,mgr);
    }

    @Override
    public boolean shouldTriggerTransaction(Candle candle, StockTradeMgr mgr) {
        return (mgr.getStock().getLastCandle().equals(candle));
    }
}


