package financialadvisor.model.strategies.candleStrategies.exit;

import financialadvisor.model.Candle;
import financialadvisor.model.strategies.candleStrategies.CandleTransactionExitStrategy;
import financialadvisor.services.StockTradeMgr;
import org.springframework.stereotype.Component;

@Component
public class ShootingStarExitStrategy extends CandleTransactionExitStrategy {
    public ShootingStarExitStrategy() {
        super("כוכב נופל");
    }

    @Override
    public boolean shouldTriggerTransaction(Candle candle) {
        float wickLength = candle.getDailyHigh() - candle.getOpenRate();
        float bodyLength = candle.getOpenRate() - candle.getClosingRate();
        boolean closeOnBottomThird = candle.getClosingRate() < candle.getDailyLow() + ((candle.getDailyHigh()-candle.getDailyLow())/3);
        //closing is on the bottom third of the daily trade && red candle
        return (bodyLength*2 < wickLength && candle.getClosingRate() < candle.getOpenRate() && closeOnBottomThird);
    }

    @Override
    public boolean shouldTriggerTransaction(Candle candle, StockTradeMgr mgr) {
        return shouldTriggerTransaction(candle);
    }
}

