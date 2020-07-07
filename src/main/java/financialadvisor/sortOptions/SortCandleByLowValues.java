package financialadvisor.sortOptions;

import financialadvisor.model.Candle;

import java.util.Comparator;

public class SortCandleByLowValues implements Comparator<Candle> {

    @Override
    public int compare(Candle o1, Candle o2) {
        if (o1.getDailyLow() < o2.getDailyLow())
            return -1;
        else if (o1.getDailyLow() == o2.getDailyLow())
            return 0;
        else return 1;
    }
}