package financialadvisor.model.strategies;

import financialadvisor.model.Candle;
import financialadvisor.model.TechnicalLine;

public class StrategyUtils {

    public static double MAX_DISTANCE_FROM_LINE_FACTOR = 0.015;

    //get distance between 2 most close-by points of technical line and candle.
    public static double getLowestDistance(TechnicalLine tech, Candle candle){
        double distanceBetweenLows = Math.abs(tech.getLowRangePrice() - candle.getDailyLow());
        double distanceBetweenHighs = Math.abs(tech.getHighRangePrice() - candle.getDailyHigh());
        double distanceBetweenHighLow = Math.abs(tech.getHighRangePrice() - candle.getDailyLow());
        double distanceBetweenLowHigh = Math.abs(tech.getLowRangePrice() - candle.getDailyHigh());
        double result = Math.min(distanceBetweenLows,distanceBetweenHighs);
        result = Math.min(result,distanceBetweenHighLow);
        result = Math.min(result,distanceBetweenLowHigh);
        return result;
    }

    public static boolean crossedTechnicalLine(Candle candle, TechnicalLine line){
        return (candle.getDailyLow() < line.getLowRangePrice() && candle.getDailyHigh() > line.getLowRangePrice());
    }

    public static boolean inRangeFromTechnicalLine(Candle candle, TechnicalLine line){
        return (getLowestDistance(line,candle) < candle.getClosingRate() * MAX_DISTANCE_FROM_LINE_FACTOR);
    }

    public static boolean candleAboveTechnicalLine(Candle candle, TechnicalLine line){
        return candle.getDailyLow() > line.getHighRangePrice();
    }

    public static boolean didCandleComeAfterLineFirstEncountered(Candle candle, TechnicalLine line){
        return candle.getDate().getTime() >= line.getFirstEncounteredDate().getTime();
    }
}
