package financialadvisor.model.strategies.candleStrategies.entry.multiCandleReversal;

import financialadvisor.model.Candle;
import financialadvisor.model.strategies.candleStrategies.CandleTransactionEntryStrategy;
import financialadvisor.services.StockTradeMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RisingSunEntryStrategy extends CandleTransactionEntryStrategy {

    @Autowired
    StockTradeMgr mgr;

    public RisingSunEntryStrategy() {
        super("נר ירוק לאחר נר אדום ומכסה את רובו (Rising Sun)");
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

        //Rising sun exists when big blue candle closes above half of the previous big red candle.
        return (candleBefore.getBodyLength() > candleBefore.getTotalWickLength()*2 && candle.getBodyLength() > candle.getTotalWickLength() * 2
                && candle.getOpenRate() <= candleBefore.getDailyLow() && candle.getClosingRate() > (candleBefore.getDailyHigh() - candleBefore.getCandleLength()/2)
                && candle.isBullish() && candleBefore.isBearish());
    }
}