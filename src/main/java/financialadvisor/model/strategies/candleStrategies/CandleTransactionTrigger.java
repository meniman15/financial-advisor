package financialadvisor.model.strategies.candleStrategies;

import financialadvisor.model.Candle;
import financialadvisor.services.StockTradeMgr;

public interface CandleTransactionTrigger {
    boolean shouldTriggerTransaction(Candle candle);
    boolean shouldTriggerTransaction(Candle candle, StockTradeMgr mgr);
}
