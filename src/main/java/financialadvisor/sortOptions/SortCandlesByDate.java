package financialadvisor.sortOptions;

import financialadvisor.model.Candle;

import java.util.Comparator;

public class SortCandlesByDate implements Comparator<Candle>{

    @Override
    public int compare(Candle o1, Candle o2) {
        if(o1.getDate().getTime() < o2.getDate().getTime()){
            return -1;
        }
        else if (o1.getDate().getTime() == o2.getDate().getTime()){
            return 0;
        }
        else return 1;
    }
}
