package financialadvisor.model;

import java.util.Date;
import java.util.Objects;

/*
 * Represents a price which performed as a support / resistance
 */
public class TechnicalLine {

    public enum LineType {
        SUPPORT,RESISTANCE,BOTH;
    }

    //to define how reliable a line is, we count how many points intersect it.
    public enum Reliability {
        TWO_POINTS_RELIABLE(2) , THREE_POINTS_RELIABLE(3),FOUR_POINTS_RELIABLE (4) ,EXTREMELY_RELIABLE(5);

        private int value;

        Reliability(int value){
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    float lowRangePrice;
    float highRangePrice;
    LineType type;
    Reliability reliability;
    Date firstEncounteredDate;

    public TechnicalLine(float lowRangePrice, float highRangePrice, LineType type, Reliability reliability, Date date) {
        this.lowRangePrice = lowRangePrice;
        this.highRangePrice = highRangePrice;
        this.type = type;
        this.reliability = reliability;
        this.firstEncounteredDate = date;
    }

    public float getLowRangePrice() {
        return lowRangePrice;
    }

    public float getHighRangePrice() {
        return highRangePrice;
    }

    public void setLowRangePrice(float lowRangePrice) {
        this.lowRangePrice = lowRangePrice;
    }

    public void setHighRangePrice(float highRangePrice) {
        this.highRangePrice = highRangePrice;
    }

    public LineType getType() {
        return type;
    }

    public void setType(LineType type) {
        this.type = type;
    }

    public Reliability getReliability() {
        return reliability;
    }

    public void setReliability(Reliability reliability) {
        this.reliability = reliability;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TechnicalLine that = (TechnicalLine) o;
        return Float.compare(that.lowRangePrice, lowRangePrice) == 0 &&
                Float.compare(that.highRangePrice, highRangePrice) == 0 &&
                type == that.type &&
                reliability == that.reliability;
    }

    public Date getFirstEncounteredDate() {
        return firstEncounteredDate;
    }

    public void setFirstEncounteredDate(Date firstEncounteredDate) {
        this.firstEncounteredDate = firstEncounteredDate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lowRangePrice, highRangePrice, type, reliability);
    }

    @Override
    public String toString() {
        return "TechnicalLine{" +
                "lowRangePrice=" + lowRangePrice +
                ", highRangePrice=" + highRangePrice +
                ", type=" + type +
                ", reliability=" + reliability +
                '}';
    }
}
