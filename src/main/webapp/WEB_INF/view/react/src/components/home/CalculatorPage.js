import React from "react";
import Calculator from "./Calculator";
import Backtester from "./Backtester";

var CanvasJS = require('./canvasjs.min');
CanvasJS = CanvasJS.Chart ? CanvasJS : window.CanvasJS;

export default class CalculatorPage extends React.Component{
    constructor(props) {
        super(props);
        this.state = {
            currentStockName: 'לאומי',
            currentStockId: '604611',
            candlesData: [],
            volumeData: [],
            technicalLinesData: [],
            showCalcResultsOnChart: true,
            calcResults:{
                numberOfTransactions:0,
                winningStrats:0,
                loosingStrats:0,
                successRate:0,
                totalPercentageGain:0
            }
        };
        this.changeStock = this.changeStock.bind(this);
        this.changeCandlesData = this.changeCandlesData.bind(this);
        this.markResultsOnGraph = this.markResultsOnGraph.bind(this);
        this.changeShowCalcResults = this.changeShowCalcResults.bind(this);
        this.changeCandlesRawData = this.changeCandlesRawData.bind(this);
        this.updateTechnicalLines = this.updateTechnicalLines.bind(this);
    }

    changeStock = (id,name) => {
        this.setState(
            { currentStockId: id,currentStockName: name}
            );
        console.log("changed stock to: "+id+", with name: "+name);
    };

    changeCandlesData = (candlesData,volumeData) => {
        this.setState({candlesData:candlesData, volumeData:volumeData},()=>console.log("data: "+JSON.stringify(this.state.candlesData.length)));
    };

    changeCandlesRawData = (rawData) => {
        this.setState({ candlesData: rawData.candles.map( point =>({
                label:CanvasJS.formatDate(new Date(point.date),"DD MM YYYY"), y: [point.openRate,point.dailyHigh,point.dailyLow,point.closingRate],
                percentage: point.percentage.toFixed(2), percentageColor:point.openRate>point.closingRate? 'red':'green'})),
            volumeData: rawData.candles.map( point =>({
                label:CanvasJS.formatDate(new Date(point.date), "DD MM YYYY"), y: point.volume, color: point.closingRate > point.openRate ? "green": "red"
            }))
        });
    };

    markResultsOnGraph = (results) => {
        let openDates = results.transactions.filter(trans => !trans.openTransaction).map((trans)=> {
            return {date:CanvasJS.formatDate(new Date(trans.openDate),"DD MM YYYY"), reason:trans.closeReason}
        });
        let closeDates = results.transactions.filter(trans => !trans.openTransaction).map((trans)=> {
            return {date:CanvasJS.formatDate(new Date(trans.closeDate),"DD MM YYYY"), reason: trans.closeReason}
        });
        console.log(results.transactions.filter(trans => !trans.openTransaction).map((trans)=> {
            return {
                open: CanvasJS.formatDate(new Date(trans.openDate), "DD MM YYYY"),
                close: CanvasJS.formatDate(new Date(trans.closeDate), "DD MM YYYY"),
                reason: trans.closeReason,
                percentage: trans.diffPercentage
            }
        }));
        let candlesWithTransDates = this.state.candlesData;
        //remove old labels
        candlesWithTransDates.forEach(candle=> candle.indexLabel = undefined);
        candlesWithTransDates.forEach((candle)=> {
            let openIndex = openDates.findIndex((trans)=> trans.date === candle.label);
            if(openIndex !== -1){
                candle.indexLabel = "Buy";
                candle.indexLabelFontSize = this.state.showCalcResultsOnChart ? 12 : 1;
                candle.indexLabelBackgroundColor = "green";
            }
            let closeIndex = closeDates.findIndex((trans)=> trans.date === candle.label);
            if(closeIndex !== -1){
                let closeReason = closeDates[closeIndex].reason;
                candle.indexLabel = candle.indexLabel === "Buy" ? closeReason+"&Buy" : closeReason;
                candle.indexLabelFontSize = this.state.showCalcResultsOnChart ? 12 : 1;
                candle.indexLabelBackgroundColor = candle.indexLabel === closeReason+"&Buy" ? "orange" : "red";
            }
        });

        this.setState({candlesData: candlesWithTransDates});
    };

    changeShowCalcResults = (shouldShowOnChart) =>{
        this.setState({candlesData: this.state.candlesData.map(candle=> {
            if (candle.indexLabel){
                candle.indexLabelFontSize = shouldShowOnChart ? 12 : 1;
            }
            return candle;
        }), showCalcResultsOnChart: shouldShowOnChart});
    };

    updateTechnicalLines = (techLinesData) => {
        this.setState({technicalLinesData: techLinesData},()=>console.log(this.state.technicalLinesData));
    };

    render() {
        return (
            <div>
                <div style={{padding:"50px"}}>
                    <h2>
                        הסבר כלשהו על המחשבון והבקטסר
                    </h2>
                </div>
                <div style={{display:"flex",flexDirection:"row-reverse"}}>
                    <div style={{padding:"100px 35px 0px 10px"}}>
                        <Calculator changeStock={this.changeStock} candlesData={this.state.candlesData}
                                    markResultsOnGraph={this.markResultsOnGraph} changeCandlesRawData={this.changeCandlesRawData}/>
                    </div>
                    <div style={{width:"60%",padding:"10px 10px 100px 100px"}}>
                        <Backtester stockId={this.state.currentStockId} stockName={this.state.currentStockName}
                                    changeCandlesData={this.changeCandlesData} changeCandlesRawData={this.changeCandlesRawData}
                                    candlesData={this.state.candlesData}
                                    showCalcResults={this.state.showCalcResultsOnChart} changeShowCalcResults={this.changeShowCalcResults}
                                    changeTechnicalLines={this.updateTechnicalLines} technicalLinesData={this.state.technicalLinesData}
                                    volumeData={this.state.volumeData}
                        />
                    </div>
                </div>
            </div>
        );
    }
}