package financialadvisor.model.strategies.candleStrategies;

import financialadvisor.model.Candle;
import financialadvisor.model.strategies.StrategyResult;
import financialadvisor.model.strategies.exit.ExitStrategy;
import financialadvisor.services.StockTradeMgr;

public abstract class CandleTransactionExitStrategy extends ExitStrategy implements CandleTransactionTrigger {

    public CandleTransactionExitStrategy(String name){
        super(name);
    }

    public StrategyResult execute(Candle candle, StrategyResult result, StockTradeMgr mgr) {
        StrategyResult stratResult = new StrategyResult(result);
        if(shouldTriggerTransaction(candle,mgr)) {
            closeTransaction(candle, stratResult);
        }
        return stratResult;
    }

    @Override
    public StrategyResult execute(Candle candle, StrategyResult result) {
        StrategyResult stratResult = new StrategyResult(result);
        if(shouldTriggerTransaction(candle)) {
            closeTransaction(candle, stratResult);
        }
        return stratResult;
    }

}
