package financialadvisor.model.strategies.candleStrategies.entry.multiCandleReversal;

import financialadvisor.model.Candle;
import financialadvisor.model.strategies.candleStrategies.CandleTransactionEntryStrategy;
import financialadvisor.services.StockTradeMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EngulfingEntryStrategy extends CandleTransactionEntryStrategy {

    @Autowired
    StockTradeMgr mgr;

    public EngulfingEntryStrategy() {
        super("נר ירוק מכסה נר אדום(Engulfing)");
    }

    @Override
    public boolean shouldTriggerTransaction(Candle candle) {
        return shouldTriggerTransaction(candle,mgr);
    }

    @Override
    public boolean shouldTriggerTransaction(Candle candle, StockTradeMgr mgr) {
        List<Candle> candlesBefore = getCandlesBefore(candle,1,mgr);
        if (candlesBefore == null){
            return false;
        }
        Candle candleBefore = candlesBefore.get(0);
        return (candle.getOpenRate() < candleBefore.getClosingRate() && candle.getClosingRate() > candleBefore.getOpenRate() &&
                candle.isBullish() && candleBefore.isBearish());
    }
}
