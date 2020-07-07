package financialadvisor.model;

import java.util.Date;
import java.util.Objects;

public class Candle {
    Date date;
    float openRate;
    float closingRate;
    float dailyHigh;
    float dailyLow;
    float volume;
    float percentage;
    public enum MinMaxStatus {
        MIN, MAX, NONE;
    }

    MinMaxStatus minMax;

    public Candle(Date date, float openRate, float closingRate, float dailyHigh, float dailyLow, float volume, float percentage) {
        this.date = date;
        this.openRate = openRate;
        this.closingRate = closingRate;
        this.dailyHigh = dailyHigh;
        this.dailyLow = dailyLow;
        this.volume = volume;
        this.percentage = percentage;
        minMax = MinMaxStatus.NONE;
    }

    public Date getDate() {
        return date;
    }

    public float getOpenRate() {
        return openRate;
    }

    public float getClosingRate() {
        return closingRate;
    }

    public float getDailyHigh() {
        return dailyHigh;
    }

    public float getDailyLow() {
        return dailyLow;
    }

    public float getVolume() {
        return volume;
    }

    public void setOpenRate(float openRate) {
        this.openRate = openRate;
    }

    public void setClosingRate(float closingRate) {
        this.closingRate = closingRate;
    }

    public void setDailyHigh(float dailyHigh) {
        this.dailyHigh = dailyHigh;
    }

    public void setDailyLow(float dailyLow) {
        this.dailyLow = dailyLow;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public MinMaxStatus getMinMax() {
        return minMax;
    }

    public void setMinMax(MinMaxStatus minMax) {
        this.minMax = minMax;
    }

    public float getBodyLength(){
        return Math.abs(this.closingRate - this.openRate);
    }

    public float getBottomWickLength(){
        //green candle
        if (this.closingRate > this.openRate){
            return this.openRate - this.dailyLow;
        }
        return this.closingRate - this.dailyLow;
    }

    public float getTopWickLength(){
        //green candle
        if (this.closingRate > this.openRate){
            return this.dailyHigh - this.closingRate;
        }
        return this.dailyHigh - this.openRate;
    }

    public float getTotalWickLength(){
        return getBottomWickLength() + getTopWickLength();
    }

    public float getCandleLength(){
        return dailyHigh-dailyLow;
    }

    public boolean isCloseOnUpperThird(){
        return this.closingRate > this.dailyHigh - ((getCandleLength())/3);
    }

    public boolean isCloseOnBottomThird(){
        return this.closingRate < this.dailyLow + ((getCandleLength())/3);
    }

    public boolean isBullish() { return this.closingRate > this.openRate;}

    public boolean isBearish() { return this.closingRate < this.openRate;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Candle candle = (Candle) o;
        return Float.compare(candle.openRate, openRate) == 0 &&
                Float.compare(candle.closingRate, closingRate) == 0 &&
                Float.compare(candle.dailyHigh, dailyHigh) == 0 &&
                Float.compare(candle.dailyLow, dailyLow) == 0 &&
                volume == candle.volume &&
                Objects.equals(date, candle.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, openRate, closingRate, dailyHigh, dailyLow, volume);
    }

    @Override
    public String toString() {
        return "Candle{" +
                "date=" + date +
                ", openRate=" + openRate +
                ", closingRate=" + closingRate +
                ", dailyHigh=" + dailyHigh +
                ", dailyLow=" + dailyLow +
                ", volume=" + volume +
                ", minMax=" + minMax +
                '}';
    }
}
