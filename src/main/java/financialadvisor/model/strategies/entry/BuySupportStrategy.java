package financialadvisor.model.strategies.entry;

import financialadvisor.model.Candle;
import financialadvisor.model.TechnicalLine;
import financialadvisor.model.strategies.StrategyResult;
import financialadvisor.model.strategies.StrategyUtils;
import financialadvisor.model.strategies.stopTriggers.StopTrigger;
import financialadvisor.services.StockTradeMgr;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BuySupportStrategy extends EntryStrategy {

    public BuySupportStrategy(){
        super( "קנה בתמיכה");
    }
    public BuySupportStrategy(StopTrigger stopLoss, StopTrigger takeProfit){
        super(stopLoss,takeProfit);
    }

    @Override
    public StrategyResult execute(Candle candle, StrategyResult result) {
        return execute(candle,result,mgr);
    }

    @Override
    public StrategyResult execute(Candle candle, StrategyResult result, StockTradeMgr mgr) {
        StrategyResult stratResult = new StrategyResult(result);
        //filter only support lines
        List<TechnicalLine> technicalLines = mgr.getTechnicalLines().stream().
                filter((line)-> line.getType().equals(TechnicalLine.LineType.SUPPORT)).collect(Collectors.toList());

        for(TechnicalLine tech: technicalLines){
            boolean closedAboveAndInRange = candle.getClosingRate() > tech.getHighRangePrice() && StrategyUtils.inRangeFromTechnicalLine(candle,tech);
            //open transaction only if tech line was already exist when candle crossed it.
            //Allow only 1 open transaction at a time.
            if (closedAboveAndInRange && StrategyUtils.didCandleComeAfterLineFirstEncountered(candle,tech) && stratResult.getOpenTransactions().size() == 0){
                addTransaction(candle, tech.getHighRangePrice() ,stratResult.getTransactions());
            }
        }
        return stratResult;
    }
}
