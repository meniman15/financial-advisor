package financialadvisor.model.strategies.entry;

import financialadvisor.model.Candle;
import financialadvisor.model.strategies.Strategy;
import financialadvisor.model.strategies.StrategyResult;
import financialadvisor.model.strategies.stopTriggers.StopTrigger;
import financialadvisor.model.transactions.Transaction;
import financialadvisor.services.StockTradeMgr;
import financialadvisor.sortOptions.SortCandlesByDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public abstract class EntryStrategy implements Strategy {

    protected String name;
    protected StopTrigger stopLoss;
    protected StopTrigger takeProfit;

    @Autowired
    protected StockTradeMgr mgr;

    public EntryStrategy(){}
    public EntryStrategy(String name){
        this.name = name;
    }

    public EntryStrategy(StopTrigger stopLoss, StopTrigger takeProfit) {
        this.stopLoss = stopLoss;
        this.takeProfit = takeProfit;
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
    @Override
    public String getName() {
        return name;
    }

    public StopTrigger getStopLoss() {
        return stopLoss;
    }

    public void setStopLoss(StopTrigger stopLoss) {
        this.stopLoss = stopLoss;
    }

    public StopTrigger getTakeProfit() {
        return takeProfit;
    }

    public void setTakeProfit(StopTrigger takeProfit) {
        this.takeProfit = takeProfit;
    }

    protected void addTransaction(Candle candle, List<Transaction> transactions){
        addTransaction(candle, candle.getClosingRate(), transactions);
    }

    protected void addTransaction(Candle candle, float entryPrice, List<Transaction> transactions){
        Transaction openTransaction = new Transaction();
        openTransaction.setEntryPrice(entryPrice);
        openTransaction.setOpenTransaction(true);
        openTransaction.setStopLoss(stopLoss.getStopPrice(openTransaction.getEntryPrice()));
        openTransaction.setTakeProfit(takeProfit.getStopPrice(openTransaction.getEntryPrice()));
        openTransaction.setOpenDate(candle.getDate());
        transactions.add(openTransaction);
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
}
