package financialadvisor.model.strategies.candleStrategies.entry.momentum;

import financialadvisor.model.Candle;
import financialadvisor.model.strategies.candleStrategies.CandleTransactionEntryStrategy;
import financialadvisor.model.strategies.constants.Constants;
import financialadvisor.services.StockTradeMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GapUpEntryStrategy extends CandleTransactionEntryStrategy {

    @Autowired
    StockTradeMgr mgr;

    public GapUpEntryStrategy() {
        super(Constants.GAP_UP_NAME);
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

        //candle opens higher than last candle high, and closes above the started gap (gap-up)
        return (candle.getOpenRate() > candleBefore.getDailyHigh() && candle.isBullish());
    }
}
