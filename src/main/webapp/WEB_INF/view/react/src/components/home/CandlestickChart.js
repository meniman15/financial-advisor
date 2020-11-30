import React, {Component} from 'react';
import CanvasJSReact from './canvasjs.react';

let CanvasJSChart = CanvasJSReact.CanvasJSChart;
 
class CandlestickChart extends Component {

	render() {
		if (!this.props.data || this.props.data.length === 0 ){
			return <h2> Loading... </h2>; 
			
		}

		const options = {
			theme: "light1", // "light1", "light2", "dark1", "dark2",
			height:500,
			minWidth:300,
			zoomEnabled: true,
			animationEnabled: true,
			title:{
				text: this.props.stockName,
				fontSize:30
			},
			axisX: {
				valueFormatString: "DD MMM YY",
				crosshair:{
					enabled: true,
					snapToDataPoint: true
				},
				labelFontSize: 10
			},
			axisY: {
				includeZero:false,
				prefix: "",
				title: "מחיר",
				titleFontSize: 20,
				minimum: this.props.minimumY-this.props.minimumY/50,
                stripLines: this.props.shouldShowTechnicals ? this.props.technicalLinesData : '',
				gridThickness:1,
				gridDashType: "dot",
				crosshair: {
					enabled: true
				},
				labelFontSize: 10
            },
			dataPointWidth: 1,
			toolTip:{
				borderThickness: 2
			},
			data: [{
				type: "candlestick",
				name: "Daily Graph",
				yValueFormatString: "###0.00",
				xValueFormatString: "DD-MM-YYYY",
				risingColor: "green",
				fallingColor: "red",
				toolTipContent: "{label}<br/> Open: {y[0]} <br/> High: {y[1]} <br/> Low: {y[2]} <br/> Close: {y[3]} <br/> <Span style='\"'color: {percentageColor};'\"'> Change: {percentage}% </Span>",
				dataPoints: this.props.data
			}],
		};
		
		return (
		<div>
			<CanvasJSChart options = {options} borderColorFunc = {changeBorderColor}
				/* onRef={ref => this.chart = ref} */
			/>
			{/*You can get reference to the chart instance as shown above using onRef. This allows you to access all chart properties and methods*/} 
		</div>
		);
	}

}
function changeBorderColor(chart){
	var dataSeries;
	for( var i = 0; i < chart.options.data.length; i++){
		dataSeries = chart.options.data[i];
		for(var j = 0; j < dataSeries.dataPoints.length; j++){
			dataSeries.dataPoints[j].color = (dataSeries.dataPoints[j].y[0] <= dataSeries.dataPoints[j].y[3]) ? (dataSeries.risingColor ? dataSeries.risingColor : dataSeries.color) : (dataSeries.fallingColor ? dataSeries.fallingColor : dataSeries.color);
		}
	}
}

export default CandlestickChart;