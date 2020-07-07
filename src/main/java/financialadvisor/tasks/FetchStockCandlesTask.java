package financialadvisor.tasks;

import financialadvisor.controller.StockRestController;
import financialadvisor.services.RestClientService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.Callable;

public class FetchStockCandlesTask implements Callable<String> {

    StockRestController controller;

    StockRestController.TimeFrame timeFrame;
    int stockId;

    public FetchStockCandlesTask(int stockId ,  StockRestController.TimeFrame timeFrame, StockRestController controller){
        this.stockId = stockId;
        this.controller = controller;
        this.timeFrame = timeFrame;
    }

    @Override
    public String call(){
        String candlesDataJson = null;
        String urlForFetch = timeFrame == StockRestController.TimeFrame.DAILY ? StockRestController.BURSA_DAILY_CHART_DATA_URL_PREFIX :
                timeFrame == StockRestController.TimeFrame.WEEKLY ? StockRestController.BURSA_WEEKLY_CHART_DATA_URL_PREFIX :
                        StockRestController.BURSA_YEARLY_CHART_DATA_URL_PREFIX;
        try {
            candlesDataJson = RestClientService.getDataByAPI(urlForFetch + stockId);
            controller.getMgr().getStock().getCandles().clear();
            controller.parseStockCandles(candlesDataJson,timeFrame);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ohh ohh, something bad happened: ", e);
        }
        return "Success";
    }
}
