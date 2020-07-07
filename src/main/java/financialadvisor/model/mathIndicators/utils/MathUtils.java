package financialadvisor.model.mathIndicators.utils;

import financialadvisor.model.Candle;

import java.util.List;

public class MathUtils {

    public static double getVariance(List<Candle> candleList, Candle lastCandle, int period){
        double avg = getAvg(candleList,lastCandle,period);
        if (avg == 0){
            return 0;
        }
        double variance = 0;
        int indexOfCandle = candleList.indexOf(lastCandle);
        for (int i=indexOfCandle-period; i<indexOfCandle; i++){
            Candle currentCandle = candleList.get(i);
            variance+=Math.pow(currentCandle.getClosingRate()-avg,2);
        }
        return variance/period;
    }

    //returns the avg closing rate of #period candles before lastCandle.
    public static double getAvg(List<Candle> candleList, Candle lastCandle, int period){
        int indexOfCandle = candleList.indexOf(lastCandle);
        if (indexOfCandle < period){
            return 0;
        }
        else{
            float count = 0;
            for (int i=indexOfCandle-period; i<indexOfCandle; i++){
                Candle currentCandle = candleList.get(i);
                count+=currentCandle.getClosingRate();
            }
            return count/period;
        }
    }

    public static double getStandardDeviation(List<Candle> candleList, Candle lastCandle, int period){
        return Math.sqrt(getVariance(candleList, lastCandle, period));
    }
}
