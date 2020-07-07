package financialadvisor.model.strategies;

import financialadvisor.model.strategies.entry.EntryStrategy;
import financialadvisor.model.strategies.exit.ExitStrategy;
import financialadvisor.model.strategies.stopTriggers.StopTrigger;

import java.util.List;

public class CombinedStrategy {

    List<EntryStrategy> entryStrategies;
    List<ExitStrategy> exitStrategies;
    StopTrigger stopLoss;
    StopTrigger takeProfit;

    public CombinedStrategy(List<EntryStrategy> entryStrategies, List<ExitStrategy> exitStrategies, StopTrigger stopLoss, StopTrigger takeProfit) {
        this.entryStrategies = entryStrategies;
        this.exitStrategies = exitStrategies;
        this.stopLoss = stopLoss;
        this.takeProfit =takeProfit;
    }

    public List<EntryStrategy> getEntryStrategies() {
        return entryStrategies;
    }

    public void setEntryStrategies(List<EntryStrategy> entryStrategies) {
        this.entryStrategies = entryStrategies;
    }

    public List<ExitStrategy> getExitStrategies() {
        return exitStrategies;
    }

    public void setExitStrategies(List<ExitStrategy> exitStrategies) {
        this.exitStrategies = exitStrategies;
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
}
