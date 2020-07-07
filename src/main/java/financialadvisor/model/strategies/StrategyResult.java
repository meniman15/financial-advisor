package financialadvisor.model.strategies;

import com.fasterxml.jackson.annotation.JsonIgnore;
import financialadvisor.model.transactions.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StrategyResult {
    int numberOfLosingTransactions;
    int numberOfWinningTransactions;
    float successRatePercentage;
    double totalPercentageProfit;

    List<Transaction> transactions = new ArrayList<>();
    String entryStrategyName;
    String exitStrategyName;

    public StrategyResult(){}

    public StrategyResult(StrategyResult result){
        this.transactions = new ArrayList<>(result.getTransactions());
        this.numberOfLosingTransactions = result.numberOfLosingTransactions;
        this.numberOfWinningTransactions = result.numberOfWinningTransactions;
        this.successRatePercentage = result.successRatePercentage;
        this.totalPercentageProfit = result.totalPercentageProfit;
        this.entryStrategyName = result.entryStrategyName;
        this.exitStrategyName = result.exitStrategyName;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public int getNumberOfLosingTransactions() {
        return numberOfLosingTransactions;
    }

    public void setNumberOfLosingTransactions(int numberOfLosingTransactions) {
        this.numberOfLosingTransactions = numberOfLosingTransactions;
    }

    public int getNumberOfWinningTransactions() {
        return numberOfWinningTransactions;
    }

    public void setNumberOfWinningTransactions(int numberOfWinningTransactions) {
        this.numberOfWinningTransactions = numberOfWinningTransactions;
    }

    public float getSuccessRatePercentage() {
        return successRatePercentage;
    }

    public void setSuccessRatePercentage(float successRatePercentage) {
        this.successRatePercentage = successRatePercentage;
    }

    @JsonIgnore
    public List<Transaction> getOpenTransactions(){
        return transactions.stream().filter((Transaction::isOpenTransaction)).collect(Collectors.toList());
    }

    @JsonIgnore
    public List<Transaction> getClosedTransactions(){
        return transactions.stream().filter((transaction)-> (!transaction.isOpenTransaction())).collect(Collectors.toList());
    }

    public double getTotalPercentageProfit() {
        return totalPercentageProfit;
    }

    public void setTotalPercentageProfit(double totalPercentageProfit) {
        this.totalPercentageProfit = totalPercentageProfit;
    }

    public String getEntryStrategyName() {
        return entryStrategyName;
    }

    public void setEntryStrategyName(String entryStrategyName) {
        this.entryStrategyName = entryStrategyName;
    }

    public String getExitStrategyName() {
        return exitStrategyName;
    }

    public void setExitStrategyName(String exitStrategyName) {
        this.exitStrategyName = exitStrategyName;
    }
}
