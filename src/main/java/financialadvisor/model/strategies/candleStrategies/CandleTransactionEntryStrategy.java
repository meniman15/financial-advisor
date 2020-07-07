package financialadvisor.model.strategies.candleStrategies;

import financialadvisor.model.Candle;
import financialadvisor.model.strategies.StrategyResult;
import financialadvisor.model.strategies.entry.EntryStrategy;
import financialadvisor.services.StockTradeMgr;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class CandleTransactionEntryStrategy extends EntryStrategy implements CandleTransactionTrigger {

    public CandleTransactionEntryStrategy(String name){
        super(name);
    }

    @Override
    public StrategyResult execute(Candle candle, StrategyResult result) {
        StrategyResult stratResult = new StrategyResult(result);
        if(shouldTriggerTransaction(candle) && result.getOpenTransactions().size() == 0){
            addTransaction(candle,stratResult.getTransactions());
        }
        return stratResult;
    }

    public StrategyResult execute(Candle candle, StrategyResult result, StockTradeMgr mgr) {
        StrategyResult stratResult = new StrategyResult(result);
        if(shouldTriggerTransaction(candle,mgr) && result.getOpenTransactions().size() == 0){
            addTransaction(candle,stratResult.getTransactions());
        }
        return stratResult;
    }


}
