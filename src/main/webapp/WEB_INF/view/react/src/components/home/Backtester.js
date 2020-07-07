import React from "react";
import CandlestickChart from "./CandlestickChart";
import Checkbox from '@material-ui/core/Checkbox';
import VolumeChart from "./VolumeChart";

var CanvasJS = require('./canvasjs.min');
CanvasJS = CanvasJS.Chart ? CanvasJS : window.CanvasJS;

export default class Backtester extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            minmaxCandlesData: [],
            lowestPrice: 0,
            lastCandleData: undefined,
            isStillTrading: true,
            shouldShowTechLines: true
        };
        this.timeout = '';
    };

    /*TODO- implement session timeout - receive timeout in login or register request, and implement timeout js - call logout when finished.*/
    componentDidMount() {
        this.fetchChartData(this.props.stockId);
        this.fetchMinMaxChartData(this.props.stockId);
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        if (prevProps.stockId !== this.props.stockId){
            this.fetchChartData(this.props.stockId);
            this.fetchMinMaxChartData(this.props.stockId);
        }
        //in case timeframe has changed
        if (prevProps.candlesData !== this.props.candlesData){
            this.fetchTechnicalLines(this.props.stockId);
            let minCandlePrice = Math.min.apply(Math,this.props.candlesData.map(point => point.y[2]));
            this.setState({lowestPrice: minCandlePrice});
        }
    }

    componentWillUnmount() {
        if (this.timeout){
            clearInterval(this.timeout);
        }
    }

    fetchChartData = (stockId) => {
        fetch('http://localhost:8080/stockData/'+stockId)
            .then(res => res.json())
            .then((dataGiven) => {
                console.log(dataGiven);
                let minCandlePrice = Math.min.apply(Math,dataGiven.candles.map(point => point.dailyLow));
                this.setState({
                    lowestPrice: minCandlePrice,
                    isStillTrading: dataGiven.currentlyTrading
                });
                this.props.changeCandlesRawData(dataGiven);
                console.log("end fetchChartData")
            }).then(()=> this.fetchIntradayDataPeriodically(stockId)).then(() => this.fetchTechnicalLines(stockId))
            .catch(console.log)
    };

    fetchMinMaxChartData = (stockId) => {
        fetch('http://localhost:8080/stockMinMaxCandles/'+stockId)
            .then(res => res.json())
            .then((dataGiven) => {
                console.log(dataGiven);
                let minCandlePrice = Math.min.apply(Math,dataGiven.map(point => point.dailyLow));
                console.log("lowest point: "+minCandlePrice);
                this.setState({ minmaxCandlesData: dataGiven.map( point => ({
                        label:CanvasJS.formatDate(new Date(point.date),"DD MM YYYY"), y: [point.openRate,point.dailyHigh,point.dailyLow,point.closingRate]}))});
                console.log("end fetchChartData");
            })
            .catch(console.log)
    };

    fetchTechnicalLines = (stockId) => {
        fetch('http://localhost:8080/technicalLines/'+stockId)
            .then(res => res.json())
            .then((dataGiven) => {
                console.log(dataGiven);
                let maxLine = dataGiven.technicalLines.reduce(function (p, v) {
                    return ( p > v ? p : v );
                });
                let lineWidth = maxLine.lowRangePrice < 150 ? 0.1 : maxLine.lowRangePrice < 500 ? 1 : maxLine.lowRangePrice > 2500 ? maxLine.lowRangePrice/1000 : 2;
                this.props.changeTechnicalLines(
                    dataGiven.technicalLines.map( line => ({
                    startValue: line.type==='SUPPORT'?line.lowRangePrice:line.highRangePrice,
                    endValue: line.type==='SUPPORT'?line.lowRangePrice+lineWidth:line.highRangePrice+lineWidth,
                    color: line.type==='SUPPORT' ? "green" : line.type==='BOTH' ? "orange": "red",
                    lineDashType: "long dash" ,labelBackgroundColor:"none"}))
                );
                console.log("end fetchTechnicalLines")
            })
            .catch(console.log)
    };

    fetchIntradayData = (stockId) => {
        return fetch('http://localhost:8080/stockIntradayData/'+stockId).then((res)=>res.json()).then((data)=>{
            console.log("current stock candles length: "+this.state.candlesData.length);
            console.log("intraday: "+data);
            let newCandlesData = this.state.candlesData;
            //no news, abort
            if (!data.lastCandle){
                return;
            }
            let formattedDate =  CanvasJS.formatDate(new Date(data.lastCandle.date),"DD MM YYYY");

            //if an old intraday candle exist pop it out
            if (newCandlesData[this.state.candlesData.length-1].label === formattedDate){
                newCandlesData.pop();
            }
            let newCandle ={ label: formattedDate, y: [data.lastCandle.openRate,data.lastCandle.dailyHigh,data.lastCandle.dailyLow,data.lastCandle.closingRate]
                , percentage: data.lastCandle.percentage.toFixed(2) , percentageColor:data.lastCandle.openRate>data.lastCandle.closingRate? 'red':'green'};
            newCandlesData.push(newCandle);
            console.log(newCandlesData);

            let volumeData = this.props.volumeData;
            if (volumeData[this.props.volumeData.length-1].label === formattedDate){
                volumeData.pop();
                console.log(volumeData);
            }
            volumeData.push({label: formattedDate , y: data.lastCandle.volume});

            this.setState({lastCandleData: newCandle},
                ()=> this.props.changeCandlesData(newCandlesData,volumeData));

            if (!data.currentlyTrading){
                this.setState({isStillTrading: data.currentlyTrading});
            }
            console.log(this.state.candlesData);
        }).catch((e) => console.log(e));
    };

    fetchIntradayDataPeriodically = (stockId) => {
        console.log("periodic intraday data: "+JSON.stringify(this.state.lastCandleData));
        console.log("periodic intraday input: "+stockId);
        if (this.state.isStillTrading){
            this.fetchIntradayData(this.props.stockId).then(()=>{
                this.timeout = setInterval(() => {
                    this.fetchIntradayData(this.props.stockId).then(() => {
                        console.log("new periodic data received: "+JSON.stringify(this.state.lastCandleData));
                    });
                    if (!this.state.isStillTrading){
                        clearInterval(this.timeout);
                    }
                },60000);
            });
        }
    };

    render() {
        return (
            <div>
                <h3 style={{direction:"rtl", align:"right"}}> תצוגה על גבי הגרף</h3>
                <div style={{display:"flex",direction:"rtl"}}>
                    <label>
                        הצג קווי תמיכה והתנגדות:
                        <Checkbox checked={this.state.shouldShowTechLines} onChange={(event)=> {
                          this.setState({shouldShowTechLines: event.target.checked});
                        }}/>
                    </label>
                    <label>
                        קווי מגמה:
                        <Checkbox checked={false} disabled color="primary"/>
                    </label>
                    <label>
                        הצג תוצאות חישוב:
                        <Checkbox checked={this.props.showCalcResults} onChange={(event)=> {
                            this.props.changeShowCalcResults(event.target.checked);
                            }} color="primary"/>
                    </label>

                </div>

     {/*           <SearchBox searchChange={this.onSearchChange}/>*/}
                <CandlestickChart data={this.props.candlesData} stockId={this.props.stockId} stockName={this.props.stockName}
                                  shouldShowTechnicals={this.state.shouldShowTechLines} technicalLinesData={this.props.technicalLinesData}
                                  minimumY={this.state.lowestPrice}/>
                <VolumeChart volumeData={this.props.volumeData} candleData={this.state.candlesData}/>
{/*                <h2> Min Max candles: </h2>
                <CandlestickChart data={this.state.minmaxCandlesData} stockName={this.state.stockName} technicalLinesData={this.state.technicalLinesData}
                                  minimumY={this.state.lowestPrice}/>*/}
            </div>
        );
    }
}