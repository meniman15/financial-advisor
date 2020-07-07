package financialadvisor.controller;

import financialadvisor.model.dto.CombinedStrategyDTO;
import financialadvisor.model.dto.StockNameIdDTO;
import financialadvisor.model.dto.StrategyDTO;
import financialadvisor.model.strategies.CombinedStrategy;
import financialadvisor.model.strategies.Strategy;
import financialadvisor.model.strategies.StrategyResult;
import financialadvisor.model.strategies.entry.EntryStrategy;
import financialadvisor.model.strategies.exit.ExitStrategy;
import financialadvisor.model.strategies.stopTriggers.FixedStopLoss;
import financialadvisor.model.strategies.stopTriggers.FixedTakeProfit;
import financialadvisor.services.StockTradeMgr;
import financialadvisor.services.StrategyMgr;
import financialadvisor.tasks.CalculateStockStrategiesTask;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
public class StrategyRestController {

    @Autowired
    private List<EntryStrategy> entryStrategies;

    @Autowired
    private List<ExitStrategy> exitStrategies;

    @Autowired
    StockTradeMgr StockMgr;

    @Autowired
    StrategyMgr strategyMgr;

    @Autowired
    StockRestController stockController;

    public static final int N_TOP_STRATEGIES = 3;

    @GetMapping("/entryStrategies")
    public List<String> getEntryStrategiesNames(){
        return entryStrategies.stream().map((Strategy::getName)).collect(Collectors.toList());
    }

    @GetMapping("/exitStrategies")
    public List<String> getExitStrategiesNames(){
        return exitStrategies.stream().map((Strategy::getName)).collect(Collectors.toList());
    }

    @PostMapping("/strategy")
    public StrategyResult executeStrategy(@RequestBody StrategyDTO request){
        int stockIntId = Integer.parseInt(request.getStockId());
        String strategyName = request.getStrategyName();
        if (CollectionUtils.isEmpty(StockMgr.getStock().getCandles()) || StockMgr.getStock().getId() != stockIntId){
            stockController.init(stockIntId);
        }
        List<Strategy> strategies = new ArrayList<>(entryStrategies);
        strategies.addAll(exitStrategies);
        Strategy strategy = strategies.stream().filter((strat -> strat.getName().equals(strategyName))).findFirst()
                .orElseThrow(()->  new ResponseStatusException(HttpStatus.BAD_REQUEST,strategyName+": is not a valid strategy name"));
        return strategy.executeOnAll();
    }

    @PostMapping("/calculateStrategyResults")
    public StrategyResult calculateStrategyResults(@RequestBody CombinedStrategyDTO dto){
        List<EntryStrategy> entryStrategies = this.entryStrategies.stream()
                .filter(strategy-> dto.getEntryStrategyNames().contains(strategy.getName())).collect(Collectors.toList());
        List<ExitStrategy> exitStrategies = this.exitStrategies.stream()
                .filter(strategy-> dto.getExitStrategyNames().contains(strategy.getName())).collect(Collectors.toList());
        CombinedStrategy combinedStrategy = new CombinedStrategy(entryStrategies,exitStrategies,new FixedStopLoss(dto.getStopLoss()),new FixedTakeProfit(dto.getTakeProfit()));
        return strategyMgr.executeStrategies(combinedStrategy);
    }

    /***
     * Input: stock ID (by stock service), stop loss and take profit fixed percentages.
     * Calculate the combination which gives the best result between: 1 entry strategy and 1 exit strategy.
     */
    @PostMapping("/bestStrategy")
    public StrategyResult getBestStrategy(@RequestBody CombinedStrategyDTO dto){
        List<EntryStrategy> entryStrategies = new ArrayList<>();
        List<ExitStrategy> exitStrategies = new ArrayList<>();
        StrategyResult strategyResult = null;
        for (EntryStrategy e: this.entryStrategies){
            for (ExitStrategy ex: this.exitStrategies){
                StrategyResult currentStrategy;
                entryStrategies.add(e);
                exitStrategies.add(ex);
                CombinedStrategy combinedStrategy = new CombinedStrategy(entryStrategies,exitStrategies,
                        new FixedStopLoss(dto.getStopLoss()),new FixedTakeProfit(dto.getTakeProfit()));
                currentStrategy = strategyMgr.executeStrategies(combinedStrategy);
                if (strategyResult == null || strategyResult.getTotalPercentageProfit() < currentStrategy.getTotalPercentageProfit())
                {
                    strategyResult = currentStrategy;
                }
                entryStrategies.clear();
                exitStrategies.clear();
            }
        }
        return strategyResult;
    }

    //Input - DTO of stop-loss and take-profit percentage
    //Output - N (default = 3) most profitable strategies over all Israeli stock market
    @PostMapping("/bestAndWorstStrategies")
    public List<Pair<String,Double>> getThreeBestAndWorstStrategies(@RequestBody CombinedStrategyDTO dto){
        List<StockNameIdDTO> stocksMetadata = stockController.getAllStocks();
        ExecutorService service = null;
        List<Callable<List<StrategyResult>>> tasks = new ArrayList<>();
        Map<String,Double> strategiesToProfitPercentage = new HashMap<>();
        try {
            service = Executors.newFixedThreadPool(stocksMetadata.size());
            for (StockNameIdDTO stock : stocksMetadata){
                tasks.add(new CalculateStockStrategiesTask(stock.getStockId(),stock.getStockName(),
                        StockRestController.TimeFrame.DAILY,stockController,this,dto,strategiesToProfitPercentage));
            }
            service.invokeAll(tasks);
            service.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Pair<String,Double>> minMaxStrategies = new ArrayList<>();
        for(int i=0; i<N_TOP_STRATEGIES; i++){
            String strategy =Collections.max(strategiesToProfitPercentage.entrySet(),Comparator.comparingDouble(Map.Entry::getValue)).getKey();
            Double profitPercentage = strategiesToProfitPercentage.get(strategy);
            strategiesToProfitPercentage.remove(strategy);
            minMaxStrategies.add(new Pair<>(strategy,profitPercentage));
        }
        for(int i=0; i<N_TOP_STRATEGIES; i++){
            String strategy =Collections.min(strategiesToProfitPercentage.entrySet(),Comparator.comparingDouble(Map.Entry::getValue)).getKey();
            Double profitPercentage = strategiesToProfitPercentage.get(strategy);
            strategiesToProfitPercentage.remove(strategy);
            minMaxStrategies.add(new Pair<>(strategy,profitPercentage));
        }
        return minMaxStrategies;
    }

    public void calcAndUpdateStrategiesCount(CombinedStrategyDTO dto, Map<String, Double> strategiesToProfitPercentage, StockTradeMgr mgr) {
        List<StrategyResult> results = getAllStrategyResults(dto,mgr);
        for(StrategyResult result : results){
            String strategiesString = result.getEntryStrategyName()+","+result.getExitStrategyName();
            synchronized (strategiesToProfitPercentage){
                strategiesToProfitPercentage.put(
                        strategiesString,strategiesToProfitPercentage.getOrDefault(strategiesString, (double) 0)+result.getTotalPercentageProfit());
            }
        }
    }

    private List<StrategyResult> getAllStrategyResults(CombinedStrategyDTO dto,StockTradeMgr mgr){
        List<StrategyResult> results = new ArrayList<>();
        List<EntryStrategy> entryStrategies = new ArrayList<>();
        List<ExitStrategy> exitStrategies = new ArrayList<>();
        for (EntryStrategy e: this.entryStrategies){
            for (ExitStrategy ex: this.exitStrategies){
                StrategyResult currentStrategy;
                entryStrategies.add(e);
                exitStrategies.add(ex);
                CombinedStrategy combinedStrategy = new CombinedStrategy(entryStrategies,exitStrategies,
                        new FixedStopLoss(dto.getStopLoss()),new FixedTakeProfit(dto.getTakeProfit()));
                currentStrategy = strategyMgr.executeStrategies(combinedStrategy,mgr);
                results.add(currentStrategy);
                entryStrategies.clear();
                exitStrategies.clear();
            }
        }
        return results;
    }
}
