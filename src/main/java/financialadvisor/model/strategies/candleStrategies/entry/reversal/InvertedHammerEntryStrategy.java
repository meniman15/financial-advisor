package financialadvisor.model.strategies.candleStrategies.entry.reversal;

import financialadvisor.model.Candle;
import financialadvisor.model.strategies.candleStrategies.CandleTransactionEntryStrategy;
import financialadvisor.services.StockTradeMgr;
import org.springframework.stereotype.Component;

@Component
public class InvertedHammerEntryStrategy extends CandleTransactionEntryStrategy {

    public InvertedHammerEntryStrategy() {
        super("נר פטיש הפוך");
    }

    @Override
    public boolean shouldTriggerTransaction(Candle candle) {
        float wickLength = candle.getBottomWickLength();
        float bodyLength = candle.getBodyLength();

        //closing is on the bottom third of the daily trade && green candle
        return (bodyLength*2 < wickLength && candle.getClosingRate() > candle.getOpenRate() && candle.isCloseOnBottomThird());
    }

    @Override
    public boolean shouldTriggerTransaction(Candle candle, StockTradeMgr mgr) {
        return shouldTriggerTransaction(candle);
    }
}

