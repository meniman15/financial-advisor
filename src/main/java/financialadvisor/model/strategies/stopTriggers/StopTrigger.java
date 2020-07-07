package financialadvisor.model.strategies.stopTriggers;

import org.springframework.stereotype.Component;

@Component
public interface StopTrigger {
    float getStopPrice(float entryPrice);
}
