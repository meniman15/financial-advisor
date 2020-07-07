package financialadvisor.tasks;

import financialadvisor.controller.StockRestController;
import financialadvisor.controller.StrategyRestController;
import financialadvisor.model.dto.CombinedStrategyDTO;
import financialadvisor.model.strategies.StrategyResult;
import financialadvisor.services.RestClientService;
import financialadvisor.services.StockTradeMgr;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class CalculateStockStrategiesTask implements Callable<List<StrategyResult>> {

    StrategyRestController strategyController;
    StockRestController stockController;
    CombinedStrategyDTO dto;
    StockRestController.TimeFrame timeFrame;
    Map<String,Double> strategiesToProfit;
    int stockId;
    String stockName;

    public CalculateStockStrategiesTask(int stockId, String name, StockRestController.TimeFrame timeFrame,
                                        StockRestController controller, StrategyRestController strategyController, CombinedStrategyDTO dto,
                                        Map<String, Double> strategiesToProfitPercentage){
        this.stockId = stockId;
        this.stockController = controller;
        this.timeFrame = timeFrame;
        this.stockName = name;
        this.strategyController = strategyController;
        this.dto = dto;
        this.strategiesToProfit = strategiesToProfitPercentage;
    }

    @Override
    public List<StrategyResult> call(){
        StockTradeMgr mgr = new StockTradeMgr(stockId,stockName);
        List<StrategyResult> results = new ArrayList<>();
        String candlesDataJson = null;
        String urlForFetch = timeFrame == StockRestController.TimeFrame.DAILY ? StockRestController.BURSA_DAILY_CHART_DATA_URL_PREFIX :
                timeFrame == StockRestController.TimeFrame.WEEKLY ? StockRestController.BURSA_WEEKLY_CHART_DATA_URL_PREFIX :
                        StockRestController.BURSA_YEARLY_CHART_DATA_URL_PREFIX;
        try {
            candlesDataJson = RestClientService.getDataByAPI(urlForFetch + stockId);
            stockController.parseStockCandles(candlesDataJson,timeFrame,mgr);
            mgr.init();
            strategyController.calcAndUpdateStrategiesCount(dto, strategiesToProfit, mgr);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ohh ohh, something bad happened: ", e);
        }
        return results;
    }
}
