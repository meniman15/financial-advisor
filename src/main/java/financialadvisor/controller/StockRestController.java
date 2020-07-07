package financialadvisor.controller;

import financialadvisor.model.Candle;
import financialadvisor.model.Stock;
import financialadvisor.model.TechnicalLinesAgg;
import financialadvisor.model.dto.StockNameIdDTO;
import financialadvisor.services.RestClientService;
import financialadvisor.services.StockTradeMgr;
import financialadvisor.sortOptions.SortCandlesByDate;
import financialadvisor.tasks.FetchStockCandlesTask;
import financialadvisor.tasks.FetchStockIntradayCandleTask;
import financialadvisor.tasks.FetchStockNameTask;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@CrossOrigin("*")
public class StockRestController {
    //public static final String BIZPORTAL_URL_PREFIX = "https://www.bizportal.co.il/forex/quote/ajaxrequests/paperdatagraphjson?period=yearly&paperID=";
    public static final String BURSA_DAILY_CHART_DATA_URL_PREFIX = "https://api.tase.co.il/api/ChartData/ChartData/?ct=1&ot=1&lang=1&cf=0&cv=0&cl=0&cgt=1&cp="+DateRange.TWO_YEARS.ordinal()+"&oid=";
    public static final String BURSA_WEEKLY_CHART_DATA_URL_PREFIX = "https://api.tase.co.il/api/ChartData/ChartData/?ct=1&ot=1&lang=1&cf=0&cv=0&cl=0&cgt=1&cp="+DateRange.FIVE_YEARS.ordinal()+"&oid=";
    public static final String BURSA_YEARLY_CHART_DATA_URL_PREFIX = "https://api.tase.co.il/api/ChartData/ChartData/?ct=1&ot=1&lang=1&cf=0&cv=0&cl=0&cgt=1&cp="+DateRange.MAX_YEARS.ordinal()+"&oid=";
    public static final String BURSA_STOCK_METADATA_URL_PREFIX = "https://api.tase.co.il/api/company/securitydata?lang=0&securityId=";
    public static final String BURSA_ALL_STOCKS_METADATA_URL = "https://api.tase.co.il/api/content/searchentities?lang=0";
    //public static final String BURSA_CHART_DATA_INTRADAY_URL_PREFIX = "https://api.tase.co.il/api/ChartData/ChartData/?ct=1&ot=1&lang=1&cf=0&cp="+DateRange.INTRA_DAY.ordinal()+"&cv=0&cl=0&cgt=1&oid=";

    public enum DateRange {
        INTRA_DAY,MONTH,THREE_MONTHS,HALF_YEAR,YEAR,TWO_YEARS,THREE_YEARS,FIVE_YEARS,FISCAL_FIVE_YEARS,WEEK,MAX_YEARS
    }

    public enum TimeFrame {
        DAILY, WEEKLY, MONTHLY
    }

    @Autowired
    StockTradeMgr mgr;

    public void init(int paperId, TimeFrame timeFrame){
        ExecutorService service;
        try{
            List<Callable<String>> tasks = new ArrayList<>();
            service = Executors.newFixedThreadPool(3);
            tasks.add(new FetchStockNameTask(paperId,this));
            tasks.add(Executors.privilegedCallable(new FetchStockCandlesTask(paperId, timeFrame,this)));
            service.invokeAll(tasks);
            Executors.privilegedCallable(new FetchStockIntradayCandleTask(paperId,this)).call();
            mgr.getStock().setId(paperId);
            mgr.init();

            service.shutdown();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void init(int paperId){
        init(paperId,TimeFrame.DAILY);
    }

    public void parseStockName(String json){
        JSONObject obj = new JSONObject(json);
        mgr.getStock().setName(obj.getString("CompanyName"));
    }

    //default timeframe is daily
    public void parseStockCandles(String json) {
        parseStockCandles(json,TimeFrame.DAILY);
    }

    public void parseStockCandles(String json,TimeFrame timeFrame) {
        parseStockCandles(json,timeFrame,mgr);
    }

    public void parseStockCandles(String json,TimeFrame timeFrame,StockTradeMgr mgr) {
        JSONObject obj = new JSONObject(json);
        List<Candle> candleList =  mgr.getStock().getCandles();

        final AtomicReference<Candle> lastCandle = new AtomicReference<>(null);
        AtomicReference<Integer> currentDayOfTheWeek = new AtomicReference<>(1);
        obj.getJSONArray("PointsForHistoryChart").forEach(row-> {
            Date date = null;
            Integer currentMonth = null;
            try {
                date = new SimpleDateFormat("dd/MM/yyyy").parse(((JSONObject)row).getString("TradeDate"));
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                currentMonth = c.get(Calendar.MONTH)+1;
                currentDayOfTheWeek.set(c.get(Calendar.DAY_OF_WEEK));

            } catch (ParseException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            //in case last candle should be updated
            if(lastCandle.get() !=null && ((timeFrame == TimeFrame.WEEKLY && currentDayOfTheWeek.get() != 5) ||
                    (timeFrame == TimeFrame.MONTHLY && currentMonth == lastCandle.get().getDate().getMonth()+1))){
                lastCandle.set(getUpdatedLastCandle(lastCandle,row));
            }
            else{
                float openRate = ((JSONObject)row).getFloat("OpenRate");
                float closingRate = ((JSONObject)row).getFloat("ClosingRate");
                float dailyHigh = ((JSONObject)row).getFloat("HighRate");
                float dailyLow = ((JSONObject)row).getFloat("LowRate");
                float baseRate = ((JSONObject)row).getFloat("BaseRate");
                float changePercentage = (closingRate-baseRate) / baseRate *100;
                float volume = ((JSONObject)row).getFloat("TurnOver1000");
                //A fix for the Bursa bug of days without transactions
                if (volume == 0 && dailyHigh == 0 && dailyLow == 0){
                    dailyHigh = closingRate;
                    dailyLow = closingRate;
                    if (openRate == 0 && baseRate != 0){
                        openRate = baseRate;
                    }
                }
                Candle candle = new Candle(date, openRate, closingRate, dailyHigh, dailyLow, volume,changePercentage);
                lastCandle.set(candle);
                candleList.add(candle);
            }
        });
        candleList.sort(new SortCandlesByDate());
        if (candleList.size()>0){
            mgr.getStock().setLastCandle(candleList.get(candleList.size()-1));
        }
    }

    private Candle getUpdatedLastCandle(AtomicReference<Candle> lastCandle, Object rowObj){
        lastCandle.get().setClosingRate(((JSONObject)rowObj).getFloat("ClosingRate"));
        lastCandle.get().setPercentage (((lastCandle.get().getClosingRate()-lastCandle.get().getOpenRate()) / lastCandle.get().getOpenRate()) *100);
        float volume = ((JSONObject)rowObj).getFloat("TurnOver1000");
        lastCandle.get().setVolume(lastCandle.get().getVolume() + volume);
        float dailyHigh = ((JSONObject)rowObj).getFloat("HighRate");
        lastCandle.get().setDailyHigh(Math.max(lastCandle.get().getDailyHigh(),dailyHigh));
        float dailyLow = ((JSONObject)rowObj).getFloat("LowRate");
        //no volume means no trades, so daily low is 0 by default, which should not be taken in account
        dailyLow = (dailyLow == 0 && volume == 0) ? lastCandle.get().getDailyLow(): dailyLow;
        lastCandle.get().setDailyLow(Math.min(lastCandle.get().getDailyLow(),dailyLow));
        return lastCandle.get();
    }

    public void parseIntradayStockCandle(String json){
        JSONObject obj = new JSONObject(json);
       {
            Date date = null;
            try {
                date = new SimpleDateFormat("dd/MM/yyyy").parse(obj.getString("TradeDate"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //means stock is not in trade yet
            if (obj.get("OpenRate").equals(null)){
                return;
            }
            float openRate = obj.getFloat("OpenRate");
            float lastRate = obj.getFloat("LastRate");
            float dailyHigh = obj.get("HighRate").equals(null) ? 0 : obj.getFloat("HighRate");
            float dailyLow = obj.get("LowRate").equals(null) ? 0 : obj.getFloat("LowRate");
            float volume = obj.get("TurnOverValueShekel").equals(null) ? 0 : obj.getFloat("TurnOverValueShekel")/1000;
            float changePercentage =  obj.getFloat("Change");
            //default is true, because inDay=1
            boolean isTrading = obj.get("LastDealTime").equals(null) || !obj.getString("LastDealTime").equals("סוף יום");
           if (volume == 0 && dailyHigh == 0 && dailyLow == 0){
               dailyHigh = lastRate;
               dailyLow = lastRate;
           }
            if (!mgr.getStock().getLastCandle().getDate().equals(date)){
                Candle newCandle = new Candle(date, openRate, lastRate, dailyHigh, dailyLow, volume,changePercentage);
                mgr.getStock().getCandles().add(newCandle);
                mgr.getStock().setLastCandle(newCandle);
            }
            else{
                mgr.getStock().getLastCandle().setOpenRate(openRate);
                mgr.getStock().getLastCandle().setDailyLow(dailyLow);
                mgr.getStock().getLastCandle().setDailyHigh(dailyHigh);
                mgr.getStock().getLastCandle().setClosingRate(lastRate);
                mgr.getStock().getLastCandle().setPercentage(changePercentage);
                mgr.getStock().getLastCandle().setVolume(volume);
            }
            mgr.getStock().setCurrentlyTrading(isTrading);
       };
    }

    @GetMapping("stockData/{id}")
    public Stock getStockData(@PathVariable int id){
        synchronized (mgr.getStock()){
            if(id != mgr.getStock().getId() || (CollectionUtils.isEmpty(mgr.getStock().getCandles()))) {
                init(id);
            }
        }
        return mgr.getStock();
    }

    @GetMapping("stockIntradayData/{id}")
    public Stock getStockIntradayData(@PathVariable int id) throws Exception {
        synchronized (mgr.getStock()){
            //stock has to be initiated with candles before fetching intraday candles
            if (CollectionUtils.isEmpty(mgr.getStock().getCandles())){
                getStockData(id);
            }
            new FetchStockIntradayCandleTask(id,this).call();
        }
        return mgr.getStock();
    }

    @GetMapping("stockMinMaxCandles/{id}")
    public List<Candle> getStockMinMaxCandles(@PathVariable int id){
        //if stock id is different, re-init the mgr current stock so that min-max candles will be calculated.
        synchronized (mgr.getStock()){
            if(id != mgr.getStock().getId() || (CollectionUtils.isEmpty(mgr.getMinCandles()) && CollectionUtils.isEmpty(mgr.getMaxCandles()))) {
                init(id);
            }
        }
        List<Candle> candles = new ArrayList<>(mgr.getMinCandles());
        candles.addAll(mgr.getMaxCandles());
        candles.sort(new SortCandlesByDate());
        return candles;
    }

    @GetMapping("technicalLines/{id}")
    public TechnicalLinesAgg getStockTechnicalLines(@PathVariable int id){
        synchronized (mgr.getStock()){
            if (id != mgr.getStock().getId() || CollectionUtils.isEmpty(mgr.getTechnicalLines())){
                init(id);
            }
        }
        return new TechnicalLinesAgg(mgr.getTechnicalLines());
    }

    @GetMapping("changeTimeFrame/{timeFrameString}")
    public Stock getStockAfterTimeFrameChange(@PathVariable String timeFrameString){
        TimeFrame timeFrame = TimeFrame.valueOf(timeFrameString.toUpperCase());
        init(getMgr().getStock().getId(), timeFrame);
        return getMgr().getStock();
    }


    public static void main(String[] args) {
 /*       StockRestController controller = new StockRestController();
        controller.init(StockTradeMgr.STOCK_ID);
        System.out.println(controller.mgr.getStock().getName());
        System.out.println(Arrays.toString(controller.mgr.getTechnicalLines().toArray()));*/
    }

    public StockTradeMgr getMgr() {
        return mgr;
    }

    @GetMapping("getAllStocks")
    public List<StockNameIdDTO> getAllStocks(){
        String candlesDataJson = null;
        List<StockNameIdDTO> stocksMetadata = new ArrayList<>();
        try {
            candlesDataJson = RestClientService.getDataByAPI(StockRestController.BURSA_ALL_STOCKS_METADATA_URL);
            JSONArray objs = new JSONArray(candlesDataJson);
            objs.forEach(row-> {
                JSONObject object = (JSONObject) row;
                if(object.get("SubId") != null && object.get("SubType").equals("0") && object.getInt("Type")==1 && object.get("ISIN") != null ){
                    StockNameIdDTO current = new StockNameIdDTO(object.getString("Name"),object.getInt("Id"));
                    stocksMetadata.add(current);
                }
            });
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ohh ohh, something bad happened: ", e);
        }
        return stocksMetadata;
    }
}
