package financialadvisor.model.transactions;

import java.util.Date;

public class Transaction {
    float entryPrice;
    float exitPrice;

    float stopLoss;
    float takeProfit;

    float diffPercentage;
    boolean openTransaction;

    String closeReason;

    Date openDate;
    Date closeDate;

    public static final String INDICATOR_REASON = "Exit";
    public static final String STOPLOSS_REASON = "Stop";
    public static final String TAKEPROFIT_REASON = "Profit";

    public Transaction(){
        openTransaction = true;
    }

    public float getEntryPrice() {
        return entryPrice;
    }

    public void setEntryPrice(float entryPrice) {
        this.entryPrice = entryPrice;
    }

    public float getExitPrice() {
        return exitPrice;
    }

    public void setExitPrice(float exitPrice) {
        this.exitPrice = exitPrice;
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

    public float getDiffPercentage() {
        return diffPercentage;
    }

    public void setDiffPercentage(float diffPercentage) {
        this.diffPercentage = diffPercentage;
    }

    public boolean isOpenTransaction() {
        return openTransaction;
    }

    public void setOpenTransaction(boolean openTransaction) {
        this.openTransaction = openTransaction;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public String getCloseReason() {
        return closeReason;
    }

    public void setCloseReason(String closeReason) {
        this.closeReason = closeReason;
    }
}
