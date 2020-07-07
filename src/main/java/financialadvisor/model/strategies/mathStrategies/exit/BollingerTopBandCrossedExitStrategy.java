package financialadvisor.model.strategies.mathStrategies.exit;

import financialadvisor.model.Candle;
import financialadvisor.model.mathIndicators.BollingerUpperBand;
import financialadvisor.model.strategies.mathStrategies.MathTransactionExitStrategy;
import financialadvisor.services.StockTradeMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BollingerTopBandCrossedExitStrategy extends MathTransactionExitStrategy {

    public BollingerTopBandCrossedExitStrategy() {
        super("חציית רצועת בולינגר עליונה");
    }

    @Autowired
    BollingerUpperBand bollingerUpperBand;

    @Override
    public boolean shouldTriggerTransaction(Candle candle) {
       double bollingerUpperBandValue = bollingerUpperBand.calculateIndicator(candle);

        return (bollingerUpperBandValue != 0 && bollingerUpperBandValue < candle.getClosingRate());
    }

    @Override
    public boolean shouldTriggerTransaction(Candle candle, StockTradeMgr mgr) {
        return shouldTriggerTransaction(candle);
    }
}

