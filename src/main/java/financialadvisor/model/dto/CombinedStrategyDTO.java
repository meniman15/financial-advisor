package financialadvisor.model.dto;

import java.util.ArrayList;
import java.util.List;

public class CombinedStrategyDTO {
    List<String> entryStrategyNames = new ArrayList<>();
    List<String> exitStrategyNames = new ArrayList<>();

    float stopLoss;
    float takeProfit;

    public List<String> getEntryStrategyNames() {
        return entryStrategyNames;
    }

    public void setEntryStrategyNames(List<String> entryStrategyNames) {
        this.entryStrategyNames = entryStrategyNames;
    }

    public List<String> getExitStrategyNames() {
        return exitStrategyNames;
    }

    public void setExitStrategyNames(List<String> exitStrategyNames) {
        this.exitStrategyNames = exitStrategyNames;
    }

    public float getStopLoss() {
        return stopLoss;
    }

    public void setStopLoss(float stopLoss) {
        this.stopLoss = stopLoss;
    }

    public float getTakeProfit() {
        return takeProfit;
    }

    public void setTakeProfit(float takeProfit) {
        this.takeProfit = takeProfit;
    }
}
