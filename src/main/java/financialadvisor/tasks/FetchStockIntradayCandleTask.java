package financialadvisor.tasks;

import financialadvisor.controller.StockRestController;
import financialadvisor.services.RestClientService;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.Callable;

public class FetchStockIntradayCandleTask implements Callable<String> {
    StockRestController controller;

    int stockId;

    public FetchStockIntradayCandleTask(int stockId , StockRestController controller){
        this.stockId = stockId;
        this.controller = controller;
    }

    @Override
    public String call() throws Exception {
        String candlesDataJson;
        try {
            candlesDataJson = RestClientService.getDataByAPI(StockRestController.BURSA_STOCK_METADATA_URL_PREFIX + stockId);
            //stock not in trade
            if (candlesDataJson.equals("null") || new JSONObject(candlesDataJson).getInt("InDay") == 0){
                controller.getMgr().getStock().setCurrentlyTrading(false);
                return JSONObject.valueToString(controller.getMgr().getStock().getLastCandle());
            }
            controller.parseIntradayStockCandle(candlesDataJson);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ohh ohh, something bad happened: ", e);
        }
        return JSONObject.valueToString(controller.getMgr().getStock().getLastCandle());
    }
}
