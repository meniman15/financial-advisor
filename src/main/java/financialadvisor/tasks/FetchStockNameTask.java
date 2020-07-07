package financialadvisor.tasks;

import financialadvisor.controller.StockRestController;
import financialadvisor.services.RestClientService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.Callable;

public class FetchStockNameTask implements Callable<String> {

    StockRestController controller;
    int stockId;

    public FetchStockNameTask(int stockId , StockRestController controller){
        this.stockId = stockId;
        this.controller = controller;
    }

    @Override
    public String call(){
        String candlesDataJson = null;
        try {
            candlesDataJson = RestClientService.getDataByAPI(StockRestController.BURSA_STOCK_METADATA_URL_PREFIX + stockId);
            controller.parseStockName(candlesDataJson);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ohh ohh, something bad happened: ", e);
        }
        return "Success";
    }
}
