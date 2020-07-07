package financialadvisor.model.strategies.candleStrategies.entry.multiCandleReversal;

import financialadvisor.model.Candle;
import financialadvisor.model.strategies.candleStrategies.CandleTransactionExitStrategy;
import financialadvisor.services.StockTradeMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DarkCloudExitStrategy extends CandleTransactionExitStrategy {

    @Autowired
    StockTradeMgr mgr;

    public DarkCloudExitStrategy() {
        super(" (Dark Cloud) נר אדום לאחר נר ירוק ומכסה את רובו");
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

        //dark cloud exists when big red candle closes below half of the previous big green candle.
        return (candleBefore.getBodyLength() > candleBefore.getTotalWickLength()*2 && candle.getBodyLength() > candle.getTotalWickLength() * 2
                && candle.getOpenRate() >= candleBefore.getDailyHigh() && candle.getClosingRate() < (candleBefore.getDailyHigh() - candleBefore.getCandleLength()/2)
                && candleBefore.isBullish() && candle.isBearish());
    }
}
