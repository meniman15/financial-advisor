package financialadvisor.model.strategies.candleStrategies.entry.multiCandleReversal;

import financialadvisor.model.Candle;
import financialadvisor.model.strategies.candleStrategies.CandleTransactionExitStrategy;
import financialadvisor.services.StockTradeMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EngulfingExitStrategy extends CandleTransactionExitStrategy {

    @Autowired
    StockTradeMgr mgr;

    public EngulfingExitStrategy() {
        super("נר אדום מכסה נר ירוק(Engulfing)");
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

        return (candle.getOpenRate() > candleBefore.getClosingRate() && candle.getClosingRate() < candleBefore.getOpenRate() &&
                candleBefore.isBullish() && candle.isBearish());
    }
}