package financialadvisor.sortOptions;

import financialadvisor.model.Candle;

import java.util.Comparator;

public class SortCandleByMaxHighMinLow implements Comparator<Candle> {

    @Override
    public int compare(Candle o1, Candle o2) {
        if (o1.getMinMax().equals(Candle.MinMaxStatus.MAX)){
            if (o2.getMinMax().equals(Candle.MinMaxStatus.MAX)){
                return (o1.getDailyHigh() - o2.getDailyHigh()) < 0 ? -1 : (o1.getDailyHigh() - o2.getDailyHigh()) == 0 ? 0 : 1;
            }
            else { // o2 is Min or none
                return (o1.getDailyHigh() - o2.getDailyLow())  < 0 ? -1 : (o1.getDailyHigh() - o2.getDailyLow()) == 0 ? 0 : 1;
            }
        }
        else if (o1.getMinMax().equals(Candle.MinMaxStatus.MIN)){
            if (o2.getMinMax().equals(Candle.MinMaxStatus.MIN)){
                return (o1.getDailyLow() - o2.getDailyLow()) < 0 ? -1 : (o1.getDailyLow() - o2.getDailyLow()) == 0 ? 0 : 1;
            }
            else{
                return (o1.getDailyLow() - o2.getDailyHigh()) < 0 ? -1 : (o1.getDailyLow() - o2.getDailyHigh()) == 0 ? 0 : 1;
            }
        }
        return 0;
    }
}
