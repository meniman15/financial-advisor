package financialadvisor.services;

import financialadvisor.model.Candle;
import financialadvisor.model.Stock;
import financialadvisor.model.TechnicalLine;
import financialadvisor.sortOptions.SortCandleByHighValues;
import financialadvisor.sortOptions.SortCandleByLowValues;
import financialadvisor.sortOptions.SortTechnicalLineByLowPrice;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
/*@SessionScope*/
public class StockTradeMgr {
    //will be set by RestController
    private Stock stock;

    public static final int STOCK_ID = 604611;

    List<Candle> minCandles = new ArrayList<>();
    List<Candle> maxCandles = new ArrayList<>();

    List<TechnicalLine> technicalLines = new ArrayList<>();

    private static final double MIN_DIST_BETWEEN_CANDLES_FACTOR = 0.005;
    private static final double MIN_DIST_BETWEEN_LINES_FACTOR = 0.003;
    private static final double MIN_DIST_BETWEEN_LINES = 0.01;

    public StockTradeMgr(){
        stock = new Stock();
        stock.setId(STOCK_ID);
        stock.setName("לאומי");
    }

    public StockTradeMgr(int id, String name){
        stock = new Stock();
        stock.setId(id);
        stock.setName(name);
    }
    public List<Candle> getMinCandles() {
        return minCandles;
    }

    public void setMinCandles(List<Candle> minCandles) {
        this.minCandles = minCandles;
    }

    public List<Candle> getMaxCandles() {
        return maxCandles;
    }

    public void setMaxCandles(List<Candle> maxCandles) {
        this.maxCandles = maxCandles;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public List<TechnicalLine> getTechnicalLines() {
        return technicalLines;
    }

    private void initMinMaxCandlesData(){
        int index = 0;
        Candle lastCandle = null;
        for(Candle candle : stock.getCandles()){
            boolean priceHigherThanLastCandle = lastCandle == null || candle.getDailyHigh() > lastCandle.getDailyHigh();
            int nextCandleIndex = index + 1;
            Candle nextCandle = nextCandleIndex < stock.getCandles().size()-1 ? stock.getCandles().get(nextCandleIndex) : null ;
            boolean priceHigherThanNextCandle = nextCandle == null || candle.getDailyHigh() > nextCandle.getDailyHigh();
            boolean priceLowerThanLastCandle = lastCandle == null || candle.getDailyLow() < lastCandle.getDailyLow();
            boolean priceLowerThanNextCandle = nextCandle == null || candle.getDailyLow() < nextCandle.getDailyLow();

            if (priceHigherThanLastCandle && priceHigherThanNextCandle){
                candle.setMinMax(Candle.MinMaxStatus.MAX);
                maxCandles.add(candle);
            }
            else if (priceLowerThanLastCandle && priceLowerThanNextCandle){
                candle.setMinMax(Candle.MinMaxStatus.MIN);
                minCandles.add(candle);
            }

            lastCandle = candle;
            index++;
        }
    }

    /**
     * 1) take all min-max candles into a list.
     * 2) sort the list, so the candle prices (high for max, low for min) are in order.
     * 3) for each two close candles, if price is in range, create a technical line (support / resistance or both)
     * 4) look for merge of the new technical line with existent, if they are in range.
     */
    private void initTechnicalLines(){
        List<TechnicalLine> techLines = findTechLines();
        techLines = removeWidelyCrossedLines(techLines);
        technicalLines = filterOutCloseLine(techLines);
    }

    private List<TechnicalLine> filterOutCloseLine(List<TechnicalLine> techLines) {
        if (techLines.size() < 2){
            return techLines;
        }
        TechnicalLine prevLine = techLines.get(0);
        ListIterator<TechnicalLine> iter = techLines.listIterator();
        iter.next();
        for (; iter.hasNext();){
            TechnicalLine currentLine = iter.next();
            if (Math.abs(prevLine.getHighRangePrice() - currentLine.getHighRangePrice()) < currentLine.getHighRangePrice()*MIN_DIST_BETWEEN_LINES){
                Date earlierEncounter = prevLine.getFirstEncounteredDate().before(currentLine.getFirstEncounteredDate()) ? prevLine.getFirstEncounteredDate() : currentLine.getFirstEncounteredDate();
                if (prevLine.getReliability().getValue() > currentLine.getReliability().getValue()){
                    prevLine.setFirstEncounteredDate(earlierEncounter);
                    iter.remove();
                }
                else {
                    currentLine.setFirstEncounteredDate(earlierEncounter);
                    iter.previous();
                    iter.remove();
                }
            }

            prevLine = currentLine;
        }
        return techLines;
    }

    private List<TechnicalLine> findTechLines(){
        List<TechnicalLine> techLines = new ArrayList<>();
        techLines = findTechLines(stock.getCandles(), techLines,true);
        return findTechLines(stock.getCandles(), techLines, false);
    }

    private List<TechnicalLine> findTechLines(List<Candle> origCandles , List<TechnicalLine> currentLines, boolean byLowPrice){
        if (origCandles.isEmpty()){
            return new ArrayList<>();
        }
        List<TechnicalLine> lines = new ArrayList<>(currentLines);
        List<Candle> candles = new ArrayList<>(origCandles);

        if (byLowPrice){
            candles.sort(new SortCandleByLowValues());
        }
        else{
            candles.sort(new SortCandleByHighValues());
        }

        Candle lastCandle = candles.get(0);
        candles.remove(0);

        for (Candle candle : candles){
            final double priceMaxRange = candle.getClosingRate() * MIN_DIST_BETWEEN_CANDLES_FACTOR; //half of percent
            float determiningPrice =  byLowPrice ? candle.getDailyLow() : candle.getDailyHigh();
            float lastCandleDeterminingPrice = byLowPrice ? lastCandle.getDailyLow() : lastCandle.getDailyHigh();

            if (Math.abs(determiningPrice - lastCandleDeterminingPrice) < priceMaxRange){
                TechnicalLine.LineType type = byLowPrice ? TechnicalLine.LineType.SUPPORT : TechnicalLine.LineType.RESISTANCE;
                TechnicalLine line = new TechnicalLine(Math.min(determiningPrice,lastCandleDeterminingPrice), Math.max(determiningPrice,lastCandleDeterminingPrice),
                        type, TechnicalLine.Reliability.TWO_POINTS_RELIABLE, candle.getDate());

                //if new technical line is in range of an existing one, merge them
                boolean foundExistentMatchingLine = false;
                for (TechnicalLine existingLine : lines){
                    final double lineDistance = existingLine.getHighRangePrice() * MIN_DIST_BETWEEN_LINES_FACTOR;
                    if (Math.abs(existingLine.getLowRangePrice() - line.getHighRangePrice()) < lineDistance){
                        mergeTechLines(line, existingLine);
                        foundExistentMatchingLine = true;
                        break;
                    }
                }

                if (!foundExistentMatchingLine){
                    lines.add(line);
                }
            }
            lastCandle = candle;

        }
        return lines;
    }

    private void mergeTechLines(TechnicalLine line, TechnicalLine existingLine) {
        existingLine.setHighRangePrice(Math.max(existingLine.getHighRangePrice(),line.getHighRangePrice()));
        existingLine.setLowRangePrice(Math.min(existingLine.getLowRangePrice(),line.getLowRangePrice()));
        //if we are not in max reliability, increase it.
        if (existingLine.getReliability() != TechnicalLine.Reliability.EXTREMELY_RELIABLE){
            existingLine.setReliability(TechnicalLine.Reliability.values()[existingLine.getReliability().ordinal()+1]);
        }
        if (!(existingLine.getType().equals(line.getType()))){
            existingLine.setType(TechnicalLine.LineType.BOTH);
        }
        //since we created a tech line from the lowest price first(not chronologically), we need to take the earliest time when we merge lines.
        Date earlierEncounter = line.getFirstEncounteredDate().before(existingLine.getFirstEncounteredDate()) ?
                line.getFirstEncounteredDate() : existingLine.getFirstEncounteredDate();
        existingLine.setFirstEncounteredDate(earlierEncounter);
    }


    //assume lines list is sorted. (low to high prices)
    private List<TechnicalLine> removeWidelyCrossedLines(List<TechnicalLine> lines){
        List<TechnicalLine> resultList = new ArrayList<>(lines);
        Map<TechnicalLine,Integer> linesToCrossedCount = new HashMap<>();
        for (TechnicalLine line : lines){
            linesToCrossedCount.put(line,0);
        }

        List<Candle> sortedCandlesByDailyLow = new ArrayList<>(this.stock.getCandles());
        sortedCandlesByDailyLow.sort(new SortCandleByLowValues());
        resultList.sort(new SortTechnicalLineByLowPrice());

        //for each line, find how many candles cross it.
        for (int iLines = 0; iLines < lines.size()-1; iLines++){
            TechnicalLine currentLine = lines.get(iLines);
            for (Candle currentCandle : sortedCandlesByDailyLow) {
                //count how many crossing there are for this line
                boolean currentCandleIsInRangeOfLine = currentCandle.getDailyLow() >= currentLine.getLowRangePrice() &&
                        currentCandle.getDailyLow() < currentLine.getHighRangePrice();
                boolean currentCandleStartsBeforeEndAfter = currentCandle.getDailyLow() <= currentLine.getLowRangePrice() &&
                        currentCandle.getDailyHigh() >= currentLine.getHighRangePrice();
                boolean isCrossHappenedAfterLineCreated = currentCandle.getDate().getTime() >= currentLine.getFirstEncounteredDate().getTime();
                if (isCrossHappenedAfterLineCreated && (currentCandleIsInRangeOfLine || currentCandleStartsBeforeEndAfter)) {
                    linesToCrossedCount.put(currentLine, linesToCrossedCount.get(currentLine) + 1);
                }
                if (currentCandle.getDailyLow() > currentLine.getHighRangePrice()) {
                    break;
                }
            }
        }
        //number of candles crossed it > number of times it was found as technical line --> remove it. (un-reliable)
        for( Map.Entry<TechnicalLine,Integer> entry : linesToCrossedCount.entrySet()){
            if (entry.getValue() > entry.getKey().getReliability().getValue()){
                resultList.remove(entry.getKey());
            }
        }
        return resultList;
    }


    public void init(){
        minCandles.clear();
        maxCandles.clear();
        technicalLines.clear();

        initMinMaxCandlesData();
        initTechnicalLines();
    }


    public void liveSimulation(){
        final String DOWN_TREND = "downtrend";
        final String UP_TREND = "uptrend";

        List<Candle> candles = stock.getCandles();
        technicalLines = new ArrayList<>();
        minCandles = new ArrayList<>();
        maxCandles = new ArrayList<>();

        float lastCandlePrice = 0;
        Candle lastCandle = null;
        String trend = "";

        for(Candle c : candles){
            if (lastCandle == null){
                lastCandle = c;
                lastCandlePrice = c.getClosingRate();
            }
            else {
                if (trend.equals(DOWN_TREND) && c.getClosingRate() > lastCandlePrice){
                    minCandles.add(lastCandle);
                }
                else if (trend.equals(UP_TREND) && c.getClosingRate() < lastCandlePrice){
                    maxCandles.add(lastCandle);
                }
                if (c.getClosingRate() > lastCandlePrice){
                    trend = UP_TREND;
                }
                else if (c.getClosingRate() < lastCandlePrice){
                    trend = DOWN_TREND;
                }
                lastCandle = c;
                lastCandlePrice = c.getClosingRate();
            }
        }
    }

    /*
    * algorithm:
    * 1) for each candle:
    * 1.1) if first candle, set variables last price, last candle
    * 1.2) else:
    * 1.2.1)
    * */
}
