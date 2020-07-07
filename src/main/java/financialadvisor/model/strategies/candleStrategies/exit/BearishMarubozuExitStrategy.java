package financialadvisor.model.strategies.candleStrategies.exit;

import financialadvisor.model.Candle;
import financialadvisor.model.strategies.candleStrategies.CandleTransactionExitStrategy;
import financialadvisor.services.StockTradeMgr;
import org.springframework.stereotype.Component;

@Component
public class BearishMarubozuExitStrategy extends CandleTransactionExitStrategy {

    public BearishMarubozuExitStrategy() {
        super("נר אדום מלא");
    }

    @Override
    public boolean shouldTriggerTransaction(Candle candle) {
        float bodyLength = candle.getBodyLength();

        //full red candle, body is at least 80% of the candle
        return (bodyLength > candle.getCandleLength() * 95/100 && candle.getClosingRate() < candle.getOpenRate());
    }

    @Override
    public boolean shouldTriggerTransaction(Candle candle, StockTradeMgr mgr) {
        return shouldTriggerTransaction(candle);
    }
}

