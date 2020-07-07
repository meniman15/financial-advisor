package financialadvisor.model.strategies.candleStrategies.entry.multiCandleReversal;

import financialadvisor.model.Candle;
import financialadvisor.model.strategies.candleStrategies.CandleTransactionEntryStrategy;
import financialadvisor.services.StockTradeMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

//Reversal pattern: first red candle and second green candle mark the same daily low
@Component
public class TweezerBottomEntryStrategy extends CandleTransactionEntryStrategy {

    final static double THRESHOLD_MULTIPLY = 0.001; //0.1%

    @Autowired
    StockTradeMgr mgr;

    public TweezerBottomEntryStrategy() {
        super("תחתית כפולה של נר ירוק לאחר נר אדום (Tweezer Bottom)");
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

        return (Math.abs(candleBefore.getDailyLow() - candle.getDailyLow()) < candle.getClosingRate() * THRESHOLD_MULTIPLY
                && candleBefore.isBearish() && candle.isBullish());
    }
}
