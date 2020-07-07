package financialadvisor.model.strategies.exit;

import financialadvisor.model.Candle;
import financialadvisor.model.strategies.Strategy;
import financialadvisor.model.strategies.StrategyResult;
import financialadvisor.model.transactions.Transaction;
import financialadvisor.services.StockTradeMgr;
import financialadvisor.sortOptions.SortCandlesByDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public abstract class ExitStrategy implements Strategy {

    protected String name;

    @Autowired
    protected StockTradeMgr mgr;

    public ExitStrategy(){}
    public ExitStrategy(String name){
        this.name = name;
    }

    @Override
    public StrategyResult executeOnAll() {
        StrategyResult result = new StrategyResult();

        for(Candle c: mgr.getStock().getCandles()){
            execute(c,result);
        }
        return result;
    }

    public abstract StrategyResult execute(Candle candle, StrategyResult result);
    public abstract StrategyResult execute(Candle candle, StrategyResult result, StockTradeMgr mgr);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected List<Candle> getCandlesBefore(Candle candle, int numberOfCandles){
        return getCandlesBefore(candle,numberOfCandles,mgr);
    }

    protected List<Candle> getCandlesBefore(Candle candle, int numberOfCandles,StockTradeMgr mgr){
        List<Candle> candles = mgr.getStock().getCandles();
        int indexOfCurrentCandle = candles.indexOf(candle);
        if (indexOfCurrentCandle < numberOfCandles){
            return null;
        }
        List<Candle> returnedList = new ArrayList<>();
        for(int i=1; i<=numberOfCandles ; i++){
            returnedList.add(candles.get(indexOfCurrentCandle - i));
        }
        returnedList.sort(new SortCandlesByDate());
        return returnedList;
    }

    protected void closeTransaction(Candle candle, StrategyResult stratResult) {
        for (Transaction t : stratResult.getOpenTransactions()) {
            t.setExitPrice(candle.getClosingRate());
            t.setDiffPercentage(((t.getExitPrice() - t.getEntryPrice()) / t.getEntryPrice()) * 100);
            t.setOpenTransaction(false);
            t.setCloseDate(candle.getDate());
            t.setCloseReason(Transaction.INDICATOR_REASON);
        }
    }
}
