package financialadvisor.model;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class Stock {
    int id;
    String name;
    boolean currentlyTrading;
    Candle lastCandle;
    List<Candle> candles;

    public Stock() {
        candles = new ArrayList<>();
    }

    public Stock(int id) {
        this.id = id;
        candles = new ArrayList<>();
    }

    public Stock(int id, String name) {
        this.id = id;
        this.name = name;
        candles = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public List<Candle> getCandles() {
        return candles;
    }

    public void setCandles(List<Candle> candles) {
        this.candles = candles;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCurrentlyTrading() {
        return currentlyTrading;
    }

    public void setCurrentlyTrading(boolean currentlyTrading) {
        this.currentlyTrading = currentlyTrading;
    }

    public Candle getLastCandle() {
        return lastCandle;
    }

    public void setLastCandle(Candle lastCandle) {
        this.lastCandle = lastCandle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return id == stock.id &&
                Objects.equals(name, stock.name) &&
                Objects.equals(candles, stock.candles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, candles);
    }
}
