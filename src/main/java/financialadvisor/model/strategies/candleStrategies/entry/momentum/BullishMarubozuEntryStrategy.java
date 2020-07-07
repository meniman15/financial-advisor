package financialadvisor.model.strategies.candleStrategies.entry.momentum;

import financialadvisor.model.Candle;
import financialadvisor.model.strategies.candleStrategies.CandleTransactionEntryStrategy;
import financialadvisor.services.StockTradeMgr;
import org.springframework.stereotype.Component;

//FUll candle pattern
@Component
public class BullishMarubozuEntryStrategy extends CandleTransactionEntryStrategy {

    public BullishMarubozuEntryStrategy() {
        super("נר ירוק מלא");
    }

    @Override
    public boolean shouldTriggerTransaction(Candle candle) {
        float bodyLength = candle.getBodyLength();

        //full green candle (more than 95% of the candle is body)
        return (bodyLength > candle.getCandleLength() * 95/100 && candle.getClosingRate() > candle.getOpenRate());
    }

    @Override
    public boolean shouldTriggerTransaction(Candle candle, StockTradeMgr mgr) {
        return shouldTriggerTransaction(candle);
    }
}
