import React from "react";
import GraphTradingView from "./GraphTradingView";

export default class Graph extends React.Component{
    constructor(props) {
        super(props);
        this.state = {
            tradingViewStockName: 'TASE:TA35'
        }
    }

    render() {
        return (
            <GraphTradingView stockSymbol={this.state.tradingViewStockName}/>
        );
    }
}