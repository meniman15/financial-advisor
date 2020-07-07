package financialadvisor.model.strategies.mathStrategies.entry;

import financialadvisor.model.Candle;
import financialadvisor.model.mathIndicators.BollingerBottomBand;
import financialadvisor.model.strategies.mathStrategies.MathTransactionEntryStrategy;
import financialadvisor.services.StockTradeMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BollingerBottomBandCrossedEntryStrategy extends MathTransactionEntryStrategy {

    public BollingerBottomBandCrossedEntryStrategy() {
        super("חציית רצועת בולינגר תחתונה");
    }

    @Autowired
    BollingerBottomBand bollingerBottomBand;

    @Override
    public boolean shouldTriggerTransaction(Candle candle) {
        double bollingerBottomBandValue = bollingerBottomBand.calculateIndicator(candle);

        return (bollingerBottomBandValue != 0 && bollingerBottomBandValue > candle.getClosingRate());
    }

    @Override
    public boolean shouldTriggerTransaction(Candle candle, StockTradeMgr mgr) {
        return shouldTriggerTransaction(candle);
    }
}
