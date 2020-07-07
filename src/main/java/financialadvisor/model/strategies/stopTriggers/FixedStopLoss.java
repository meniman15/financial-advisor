package financialadvisor.model.strategies.stopTriggers;

public class FixedStopLoss implements StopTrigger {
    float percentage;

    public FixedStopLoss(float percentage){
        this.percentage = percentage;
    }

    @Override
    public float getStopPrice(float entryPrice) {
        return entryPrice - entryPrice * percentage/100;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }
}
