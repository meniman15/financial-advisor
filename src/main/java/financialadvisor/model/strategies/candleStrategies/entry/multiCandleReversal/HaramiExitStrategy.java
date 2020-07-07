package financialadvisor.model.strategies.candleStrategies.entry.multiCandleReversal;

import financialadvisor.model.Candle;
import financialadvisor.model.strategies.candleStrategies.CandleTransactionExitStrategy;
import financialadvisor.services.StockTradeMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HaramiExitStrategy extends CandleTransactionExitStrategy {

    @Autowired
    StockTradeMgr mgr;

    public HaramiExitStrategy() {
        super("נר אדום מוכל בנר ירוק(Harami)");
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

        boolean openInGapDown = candle.getOpenRate() < candleBefore.getClosingRate();
        return (openInGapDown && candleBefore.getClosingRate() < candle.getOpenRate() &&
                candleBefore.isBullish() && candle.isBearish() && candle.getBodyLength() * 4 < candleBefore.getBodyLength());
    }
}