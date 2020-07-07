package financialadvisor.model.strategies.exit;

import financialadvisor.model.Candle;
import financialadvisor.model.TechnicalLine;
import financialadvisor.model.strategies.StrategyResult;
import financialadvisor.model.strategies.StrategyUtils;
import financialadvisor.model.transactions.Transaction;
import financialadvisor.services.StockTradeMgr;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SellOnResistanceStrategy extends ExitStrategy {

    public SellOnResistanceStrategy(){
        super( "מכור בהתנגדות");
    }

    @Override
    public StrategyResult execute(Candle candle, StrategyResult result) {
        return execute(candle,result,mgr);
    }

    @Override
    public StrategyResult execute(Candle candle, StrategyResult result, StockTradeMgr mgr) {
        List<Transaction> activeTransactions = new ArrayList<>(result.getOpenTransactions());
        //nothing to do
        if (activeTransactions.isEmpty()){
            return result;
        }
        //filter out support lines, keep resistance and "both"
        List<TechnicalLine>technicalLines = mgr.getTechnicalLines().stream().
                filter((line)-> !line.getType().equals(TechnicalLine.LineType.SUPPORT)).collect(Collectors.toList());

        for(TechnicalLine tech: technicalLines){
            if (StrategyUtils.crossedTechnicalLine(candle,tech) /*|| StrategyUtils.inRangeFromTechnicalLine(candle,tech)*/){
                //close all active transactions
                for (Transaction t:activeTransactions){
                    t.setExitPrice(tech.getLowRangePrice());
                    t.setDiffPercentage(((t.getExitPrice()-t.getEntryPrice())/t.getEntryPrice())*100);
                    t.setCloseReason(Transaction.INDICATOR_REASON);
                    t.setOpenTransaction(false);
                    t.setCloseDate(candle.getDate());
                }
            }
        }
        return result;
    }

}
