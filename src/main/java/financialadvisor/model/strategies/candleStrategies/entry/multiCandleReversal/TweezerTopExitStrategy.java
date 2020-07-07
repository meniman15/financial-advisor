package financialadvisor.model.strategies.candleStrategies.entry.multiCandleReversal;

import financialadvisor.model.Candle;
import financialadvisor.model.strategies.candleStrategies.CandleTransactionExitStrategy;
import financialadvisor.services.StockTradeMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TweezerTopExitStrategy extends CandleTransactionExitStrategy {

    final static double THRESHOLD_MULTIPLY = 0.001; //0.1%

    @Autowired
    StockTradeMgr mgr;

    public TweezerTopExitStrategy() {
        super("פסגה כפולה של נר אדום לאחר נר ירוק(Tweezer Top) ");
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

        return (Math.abs(candleBefore.getDailyHigh() - candle.getDailyHigh()) < candle.getClosingRate() * THRESHOLD_MULTIPLY
                && candleBefore.isBullish() && candle.isBearish());
    }
}