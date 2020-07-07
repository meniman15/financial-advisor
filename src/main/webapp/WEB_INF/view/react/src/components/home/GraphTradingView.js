import React from 'react';
import TradingViewWidget, {Themes} from 'react-tradingview-widget';

class GraphTradingView extends React.Component{
	
	render(){
		return(
		  <TradingViewWidget
		    width= {980}
		    height= {610}
			symbol={this.props.stockSymbol}
		    timezone='exchange'
		    theme={Themes.DARK}
		    style = "1"
			locale="he_IL"
			toolbar_bg= '#f1f3f6'
		    enable_publishing= {false}
		    withdateranges= {true}
		    range= '12m'
		    hide_side_toolbar= {false}
		    allow_symbol_change= {true}
		    save_image= {false}
		    container_id= 'tradingview_05905'
		  />);
	}
}

export default GraphTradingView;