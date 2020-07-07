package financialadvisor.model.strategies.candleStrategies.entry.multiCandleReversal;

import financialadvisor.model.Candle;
import financialadvisor.model.strategies.candleStrategies.CandleTransactionExitStrategy;
import financialadvisor.services.StockTradeMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

//Three full red candles which close one below the other, after a green candle
@Component
public class ThreeBlackCrowsExitStrategy extends CandleTransactionExitStrategy {

    @Autowired
    StockTradeMgr mgr;

    public ThreeBlackCrowsExitStrategy() {
        super("שלושה נרות מלאים אדומים לאחר נר ירוק");
    }

    @Override
    public boolean shouldTriggerTransaction(Candle thirdCandle) {
        return shouldTriggerTransaction(thirdCandle,mgr);
    }

    @Override
    public boolean shouldTriggerTransaction(Candle thirdCandle, StockTradeMgr mgr) {
        List<Candle> candlesBefore = getCandlesBefore(thirdCandle,3,mgr);
        if (candlesBefore == null){
            return false;
        }

        Candle expectedGreenCandle = candlesBefore.remove(0);
        Candle first = candlesBefore.remove(0);
        Candle second = candlesBefore.remove(0);
        //creating a shape of: ^ (up-trend transforms to a down trend)
        return thirdCandle.getClosingRate() < second.getDailyLow() && second.getClosingRate() < first.getDailyLow() &&
                first.getClosingRate() < expectedGreenCandle.getClosingRate() && expectedGreenCandle.isBullish();
    }
}
