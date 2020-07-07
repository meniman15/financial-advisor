package financialadvisor.model.dto;

import java.util.Objects;

public class StockNameIdDTO {
    String stockName;
    int stockId;

    public StockNameIdDTO(String stockName, int stockId) {
        this.stockName = stockName;
        this.stockId = stockId;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockNameIdDTO that = (StockNameIdDTO) o;
        return stockId == that.stockId &&
                Objects.equals(stockName, that.stockName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stockName, stockId);
    }
}
