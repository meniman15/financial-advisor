package financialadvisor.services;

import financialadvisor.model.Candle;
import financialadvisor.model.strategies.CombinedStrategy;
import financialadvisor.model.strategies.StrategyResult;
import financialadvisor.model.strategies.candleStrategies.exit.SellAtLastExitStrategy;
import financialadvisor.model.strategies.entry.EntryStrategy;
import financialadvisor.model.strategies.exit.ExitStrategy;
import financialadvisor.model.strategies.stopTriggers.StopTrigger;
import financialadvisor.model.transactions.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StrategyMgr {

    @Autowired
    StockTradeMgr stockMgr;

    public StrategyResult executeStrategies(CombinedStrategy combinedStrategy, StockTradeMgr mgr){
        StrategyResult strategyData = new StrategyResult();
        strategyData.setEntryStrategyName(combinedStrategy.getEntryStrategies().stream().map(EntryStrategy::getName).collect(Collectors.joining()));
        strategyData.setExitStrategyName(combinedStrategy.getExitStrategies().stream().map(ExitStrategy::getName).collect(Collectors.joining()));
        List<Candle> candles = mgr.getStock().getCandles();
        StopTrigger stopLoss = combinedStrategy.getStopLoss();
        StopTrigger takeProfit = combinedStrategy.getTakeProfit();

        initEntryStrategies(combinedStrategy, stopLoss, takeProfit);

        for(Candle c: candles){
            //try to close transactions using exit strategy or stop triggers
            for(Transaction transaction: strategyData.getOpenTransactions()){
                if (isStopLossTriggered(transaction,c,strategyData)){
                    transaction.setExitPrice(transaction.getStopLoss());
                    transaction.setDiffPercentage(((transaction.getExitPrice()-transaction.getEntryPrice())/transaction.getEntryPrice())*100);
                    transaction.setOpenTransaction(false);
                    transaction.setCloseDate(c.getDate());
                    transaction.setCloseReason(Transaction.STOPLOSS_REASON);
                }
                else if (isTakeProfitTriggered(transaction,c,strategyData)){
                    transaction.setExitPrice(transaction.getTakeProfit());
                    transaction.setDiffPercentage(((transaction.getExitPrice()-transaction.getEntryPrice())/transaction.getEntryPrice())*100);
                    transaction.setOpenTransaction(false);
                    transaction.setCloseDate(c.getDate());
                    transaction.setCloseReason(Transaction.TAKEPROFIT_REASON);
                }
                else{
                    for (ExitStrategy strategy : combinedStrategy.getExitStrategies()) {
                        strategyData = strategy.execute(c, strategyData,mgr);
                    }
                }
            }

            //try to open transactions using entry strategy
            for(EntryStrategy strategy: combinedStrategy.getEntryStrategies()){
                strategyData = strategy.execute(c,strategyData,mgr);
            }
        }
        summarizeResult(strategyData);
        return strategyData;
    }

    public StrategyResult executeStrategies(CombinedStrategy combinedStrategy){
        return executeStrategies(combinedStrategy,stockMgr);
    }

    private void initEntryStrategies(CombinedStrategy combinedStrategy, StopTrigger stopLoss, StopTrigger takeProfit) {
        for(EntryStrategy strategy: combinedStrategy.getEntryStrategies()) {
            strategy.setStopLoss(stopLoss);
            strategy.setTakeProfit(takeProfit);
        }
    }

    private boolean isStopLossTriggered(Transaction t, Candle c, StrategyResult strategyData){
        return (t.getStopLoss() > c.getDailyLow() ) && !strategyData.getExitStrategyName().equals(SellAtLastExitStrategy.SELL_AT_LAST_DAY);
    }

    private boolean isTakeProfitTriggered(Transaction t, Candle c, StrategyResult strategyData){
        return (t.getTakeProfit() < c.getDailyHigh()) && !strategyData.getExitStrategyName().equals(SellAtLastExitStrategy.SELL_AT_LAST_DAY);
    }

    private void summarizeResult(StrategyResult result){
        List<Transaction> closedTransactions = result.getClosedTransactions();
        List<Transaction> winningTransactions = closedTransactions.stream().filter(t-> t.getExitPrice() > t.getEntryPrice()).collect(Collectors.toList());
        List<Transaction> losingTransactions = closedTransactions.stream().filter(t-> t.getExitPrice() < t.getEntryPrice()).collect(Collectors.toList());
        result.setNumberOfWinningTransactions(winningTransactions.size());
        result.setNumberOfLosingTransactions(losingTransactions.size());
        int totalTransactions = closedTransactions.size() > 0 ? closedTransactions.size() : 1;
        result.setSuccessRatePercentage(((float)result.getNumberOfWinningTransactions()/totalTransactions)*100);
        double winningTransactionsPercentageProfit = winningTransactions.stream()
                .mapToDouble(transaction-> (transaction.getExitPrice()-transaction.getEntryPrice())/transaction.getEntryPrice()).sum();
        double losingTransactionsPercentageLoss = losingTransactions.stream().mapToDouble(transaction ->(transaction.getEntryPrice()-transaction.getExitPrice())/transaction.getEntryPrice()).sum();
        result.setTotalPercentageProfit((winningTransactionsPercentageProfit-losingTransactionsPercentageLoss)*100);
    }
}
