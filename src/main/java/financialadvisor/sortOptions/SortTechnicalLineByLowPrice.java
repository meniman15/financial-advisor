package financialadvisor.sortOptions;

import financialadvisor.model.TechnicalLine;

import java.util.Comparator;

public class SortTechnicalLineByLowPrice implements Comparator<TechnicalLine> {
    @Override
    public int compare(TechnicalLine o1, TechnicalLine o2) {
        return o1.getLowRangePrice() - o2.getLowRangePrice() < 0 ? -1 : o1.getLowRangePrice() - o2.getLowRangePrice() > 0 ? 1 : 0;
    }
}
