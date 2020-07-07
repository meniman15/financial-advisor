package financialadvisor.model.strategies.candleStrategies.entry.multiCandleReversal;

import financialadvisor.model.Candle;
import financialadvisor.model.strategies.candleStrategies.CandleTransactionEntryStrategy;
import financialadvisor.services.StockTradeMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

//three long green candles which closes each above the other, after down trend
@Component
public class ThreeWhiteSoldiersEntryStrategy extends CandleTransactionEntryStrategy {

    @Autowired
    StockTradeMgr mgr;

    public ThreeWhiteSoldiersEntryStrategy() {
        super("שלושה נרות ירוקים מלאים לאחר נר אדום");
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

        Candle expectedRedCandle = candlesBefore.remove(0);
        Candle first = candlesBefore.remove(0);
        Candle second = candlesBefore.remove(0);
        return thirdCandle.getClosingRate() > second.getDailyHigh() && second.getClosingRate() > first.getDailyHigh()
                && first.getClosingRate() > expectedRedCandle.getClosingRate() && expectedRedCandle.isBearish();
    }
}
