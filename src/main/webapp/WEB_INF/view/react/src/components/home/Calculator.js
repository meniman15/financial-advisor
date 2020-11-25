import React from "react";
import * as Bootstrap from "react-bootstrap";
import TextField from '@material-ui/core/TextField';
import Autocomplete from '@material-ui/lab/Autocomplete';
import ReactSearchBox from 'react-search-box'
import './Calculator.css';
import RadioGroup from '@material-ui/core/RadioGroup';
import Radio from '@material-ui/core/Radio';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import {withStyles} from '@material-ui/core/styles';
/*import {create} from 'jss';
import rtl from 'jss-rtl';
import {StylesProvider} from "@material-ui/styles";*/

const useStyles = theme => ({
    inputRoot: {
        color: "blue"
}});

class Calculator extends React.Component{
    constructor(props) {
        super(props);
        this.state = {
            stopLossPercentage: '',
            takeProfitPercentage: '',
            stockName: 'לאומי',
            entryStrategy:null,
            exitStrategy:null,
            entryStrategies:[],
            exitStrategies:[],
            stockNamesToIds:[],
            candlesData: this.props.candlesData,
            buttonAction: '',
            timeFrame:'יומי',
            calcResults:{
                numberOfTransactions:0,
                winningStrats:0,
                loosingStrats:0,
                successRate:0,
                totalPercentageGain:0,
                transactions:[]
            },
            buyAndHold:{
                winningStrats:0,
                loosingStrats:0,
                successRate:0,
                totalPercentageGain:0
            }
        };
        this.calculateBuyAndHold = this.calculateBuyAndHold.bind(this);
        this.handleChangeTimeframe = this.handleChangeTimeframe.bind(this);
    };

    async componentDidMount() {
        this.fetchEntryStrategies();
        this.fetchExitStrategies();
        await this.fetchStocksName();
        console.log("fetch stock names: "+ JSON.stringify(this.state.stockNamesToIds));
    }

    async fetchEntryStrategies(){
        fetch('http://localhost:8080/entryStrategies').then((res)=>res.json()).then((data)=> {
            this.setState({entryStrategies: data.map(name => ({title:name}))},
                ()=> this.setState({entryStrategy: this.state.entryStrategies[this.state.entryStrategies.length-1]}));
        });
    };

    async fetchExitStrategies(){
        fetch('http://localhost:8080/exitStrategies').then((res)=>res.json()).then((data)=> {
            this.setState({exitStrategies: data.map(name => ({title:name}))},
                ()=> this.setState({exitStrategy: this.state.exitStrategies[this.state.exitStrategies.length-1]}));
        });
    };

    async fetchStocksName() {
        const res = await fetch('https://api.tase.co.il/api/content/searchentities?lang=0');
        const data = await res.json();
        console.log(data);
        this.setState({stockNamesToIds: data.filter((stock)=> stock.SubId!=null && stock.SubType==="0").map(stock => ({value:stock.Name, key: stock.SubId}))});
    };

    stockSelectionCallback = (selection) => {
        console.log(selection.value);
        this.setState({stockName: selection.value, timeFrame:'יומי'});
        this.props.changeStock(selection.key,selection.value);
        console.log("stockName: "+selection.value);
    };

    onStopLossPercentageChange = (event) => {
        const percentage = event.target.value;
        if(floatRegExp.test(percentage) || percentage===''){
            this.setState({stopLossPercentage:percentage});
        }
    };

    onTakeProfitPercentageChange= (event) => {
        const percentage = event.target.value;
        if(floatRegExp.test(percentage) || percentage===''){
            this.setState({takeProfitPercentage:percentage});
        }
    };

    onSubmitHandle = (event) => {
        event.preventDefault();
        if (this.state.stockName!=='' && this.state.stockName!== 'error' && !!this.state.entryStrategy && this.state.entryStrategy!=='error' && !!this.state.exitStrategy && this.state.exitStrategy!=='error' && this.state.stopLossPercentage!=='' && this.state.takeProfitPercentage!==''){
            if (this.state.buttonAction === 'calc'){
                this.calculateStrategy();
            }
            else {
                this.findBestStrategy();
            }
        }
        else {
            if (!this.state.stockName){
                this.setState({stockName:'error'});
            }
            if(!this.state.entryStrategy){
                this.setState({entryStrategy:'error'});
            }
            if(!this.state.exitStrategy){
                this.setState({exitStrategy:'error'});
            }
        }
    };

    calculateStrategy = () => {
        fetch('http://localhost:8080/calculateStrategyResults',{
            method:'post',headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }, body:JSON.stringify({
                entryStrategyNames:[this.state.entryStrategy.title],
                exitStrategyNames:[this.state.exitStrategy.title],
                stopLoss:this.state.stopLossPercentage,
                takeProfit:this.state.takeProfitPercentage})
        }) .then(resp => resp.json()).then((data)=> {
            console.log(data);
            document.getElementById("calcResults").style={opacity:1};
            this.setState({
                    calcResults:{
                        numberOfTransactions:data.transactions.length,
                        winningStrats:data.numberOfWinningTransactions,
                        loosingStrats:data.numberOfLosingTransactions,
                        successRate:data.successRatePercentage,
                        totalPercentageGain:data.totalPercentageProfit,
                        transactions: data.transactions
                    }
                }, () => this.doAfterCalculation()
            );

        }).catch(console.log);
    };

    findBestStrategy = () => {
        fetch('http://localhost:8080/bestStrategy',{
            method:'post',headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }, body:JSON.stringify({
                entryStrategyNames:[this.state.entryStrategy.title],
                exitStrategyNames:[this.state.exitStrategy.title],
                stopLoss:this.state.stopLossPercentage,
                takeProfit:this.state.takeProfitPercentage})
        }) .then(resp => resp.json()).then((data)=> {
            console.log(data);
            document.getElementById("calcResults").style={opacity:1};
            this.setState({
                entryStrategy:this.state.entryStrategies[this.state.entryStrategies.findIndex(strat=> strat.title === (data.entryStrategyName))],
                exitStrategy:this.state.exitStrategies[this.state.exitStrategies.findIndex(strat=> strat.title === (data.exitStrategyName))],
                    calcResults:{
                        numberOfTransactions:data.transactions.length,
                        winningStrats:data.numberOfWinningTransactions,
                        loosingStrats:data.numberOfLosingTransactions,
                        successRate:data.successRatePercentage,
                        totalPercentageGain:data.totalPercentageProfit,
                        transactions: data.transactions
                    }
                }, () => this.doAfterCalculation()
            );
        }).catch(console.log);
    };

    doAfterCalculation = () => {
        this.calculateBuyAndHold();
        this.props.markResultsOnGraph(this.state.calcResults);
    };

    calculateBuyAndHold = () => {
        let firstCandlePrice = this.props.candlesData[0].y[3];
        let lastCandlePrice = this.props.candlesData[this.props.candlesData.length-1].y[3];
        let winningStrats = firstCandlePrice < lastCandlePrice ? 1 : 0;
        let loosingStrats = 1 - winningStrats;
        let successRate = 100 * winningStrats;
        let totalPercentageGain = (((lastCandlePrice - firstCandlePrice)/firstCandlePrice)*100).toFixed(2);
        this.setState({
            buyAndHold: {
                winningStrats:winningStrats,
                loosingStrats:loosingStrats,
                successRate: successRate,
                totalPercentageGain: totalPercentageGain
            }
        });
    };

    handleChangeTimeframe = (event) => {
        let value = event.target.value;
        let param = value ==='יומי' ? 'daily' : value === 'שבועי' ? 'weekly' : 'monthly';
         fetch('http://localhost:8080/changeTimeFrame/'+param,{
             headers: {
            'Accept': 'application/json', 'Content-Type': 'application/json', 'Access-Control-Allow-Origin':'*'}
        }).then((res)=>res.json()).then((newCandleList)=> {
            this.setState({timeFrame:value});
            this.props.changeCandlesRawData(newCandleList);
        });
    };

    render() {
        const timeFrame = this.state.timeFrame;
        const { classes } = this.props;
        return (
            <div align="right">
                <form onSubmit={this.onSubmitHandle} id="form" style={{maxWidth: "450px"}}>
                    <h2 style={{textAlign: "center"}}> מחשבון חישוב לאחור</h2>
                    <Bootstrap.Table style={{border: "1px solid",borderRadius: "20px",borderCollapse: "unset"}}>
                        <tbody style={{direction:"rtl"}}>
                        <tr><td id="stockName">
                            <ReactSearchBox
                                placeholder="שם מניה"
                                data={this.state.stockNamesToIds}
                                autoFocus
                                value={this.state.stockName}
                                onChange={(change)=> {this.setState({stockName:change})}}
                                onSelect={(selection)=> {this.stockSelectionCallback(selection)}}
                                dropDownBorderColor={"grey"}
                                inputBoxBorderColor={this.state.stockName==='error'? 'red': ''}
                            /></td><td>בחר מניה</td></tr>
                        <tr><td>
                        <RadioGroup
                            name="timeFrame"
                            value={timeFrame.toString()}
                            onChange={this.handleChangeTimeframe}
                            row
                        >
                            {["יומי","שבועי","חודשי"].map((value) => (
                                <FormControlLabel
                                    key={value}
                                    value={value.toString()}
                                    control={<Radio />}
                                    label={value.toString()}
                                />
                            ))}
                        </RadioGroup>
                        </td><td>מרווח זמן</td></tr>
                        <tr><td>
                                <Autocomplete
                                    debug={true}
                                    id="entry_strategy_box"
                                    value={this.state.entryStrategy}
                                    options={this.state.entryStrategies}
                                    getOptionLabel={(option) => option.title}
                                    style={{width: 300, border:this.state.entryStrategy ==='error' ? 'solid 1px red': '' }}
                                    onChange={(event,value)=> this.setState({entryStrategy: value})}
                                    renderInput={(params) => <TextField {...params} fullWidth label="אסטרטגיות כניסה" variant="outlined" SelectProps={{ ...params.InputProps, classes:{classes}}}  />}
                                />
                            </td><td>אסטרטגיית כניסה</td></tr>
                        <tr><td><Autocomplete
                            id="exit_strategy_box"
                            value={this.state.exitStrategy}
                            options={this.state.exitStrategies}
                            getOptionLabel={(option) => option.title}
                            style={{ width: 300,border: this.state.exitStrategy ==='error' ? 'solid 1px red': '' }}
                            onChange={(event,value)=> this.setState({exitStrategy: value})}
                            renderInput={(params) => <TextField {...params} label="אסטרטגיות יציאה" variant="outlined" />}
                        /></td><td> אסטרטגיית יציאה </td></tr>
                        <tr><td><TextField placeholder="אחוז" required onChange={this.onStopLossPercentageChange} value={this.state.stopLossPercentage}/></td><td> סטופ לוס</td></tr>
                        <tr><td><TextField placeholder="אחוז" required onChange={this.onTakeProfitPercentageChange} value={this.state.takeProfitPercentage}/></td><td>לקיחת רווח</td></tr>
                        <tr><td>
                            <span><input id="calcButton" type="submit" value="חשב" onClick={()=>this.setState({buttonAction:'calc'})}/></span>
                            <span style={{float:'left'}}><input id="calcAll" type="submit" value="מצא אסטרטגיה מיטבית" onClick={()=>this.setState({buttonAction:'calcAll'})}/></span>
                        </td></tr>
                        </tbody>
                    </Bootstrap.Table>
                </form>
                <div id="calcResults" style={{opacity:0}}>
                    <hr/>
                    <h2 style={{textAlign: "center"}}> תוצאות החישוב</h2>
                    <Bootstrap.Table>
                        <thead>
                        <tr style={{direction:"rtl"}}>
                            <th>קנה והחזק</th>
                            <th>אסטרטגיה נבחרת</th>
                        </tr>
                        </thead>
                        <tbody style={{direction:"rtl"}}>
                        <tr><td>1</td><td>{this.state.calcResults.numberOfTransactions}</td><td>מספר עסקאות: </td></tr>
                        <tr><td>{this.state.buyAndHold.winningStrats}</td><td>{this.state.calcResults.winningStrats}</td><td>מספר עסקאות ברווח: </td></tr>
                        <tr><td>{this.state.buyAndHold.loosingStrats}</td><td>{this.state.calcResults.loosingStrats}</td><td>מספר עסקאות בהפסד: </td></tr>
                        <tr><td>{this.state.buyAndHold.successRate}</td><td>{this.state.calcResults.successRate.toFixed(2)}</td><td>אחוזי הצלחה: </td></tr>
                        <tr><td style={{direction:"ltr",float: "right",color:this.state.buyAndHold.totalPercentageGain>0?"green":"red"}}>
                            <b>{this.state.buyAndHold.totalPercentageGain}</b></td>
                            <td><span style={{direction:"ltr",float: "right",color:this.state.calcResults.totalPercentageGain>0?"green":"red"}}>
                                <b>{this.state.calcResults.totalPercentageGain.toFixed(2)}</b></span></td>
                            <td>אחוזי רווח מכלל העסקאות: </td></tr>
                        </tbody>
                    </Bootstrap.Table>
                </div>
            </div>
        );
    }
}

const floatRegExp = new RegExp('^[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)$');

export default withStyles(useStyles)(Calculator)