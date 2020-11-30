import React, {Component} from 'react';
import CanvasJSReact from './canvasjs.react';

let CanvasJSChart = CanvasJSReact.CanvasJSChart;

class VolumeChart extends Component {

    render() {
        if (!this.props.volumeData || this.props.volumeData.length === 0 ){
            return <h2> Loading... </h2>;

        }

        const options = {
            theme: "light1", // "light1", "light2", "dark1", "dark2",
            height:100,
            zoomEnabled: true,
            animationEnabled: true,
            axisX: {
                valueFormatString: "DD MMM YY",
                crosshair:{
                    enabled: true
                }
            },
            axisY: {
                includeZero:false,
                prefix: "",
                title: "מחזור",
                titleFontSize: 11,
                gridThickness:0,
                crosshair:{
                    enabled: true,
                    snapToDataPoint: true
                }
            },
            dataPointWidth: 2,
            data: [{
                type: "column",
                name: "Daily Volume",
                yValueFormatString: "###0.00",
                xValueFormatString: "DD-MM-YYYY",
                dataPoints: this.props.volumeData
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
/*    var dataSeries;
    for( var i = 0; i < chart.options.data.length; i++){
        dataSeries = chart.options.data[i];
        for(var j = 0; j < dataSeries.dataPoints.length; j++){
            dataSeries.dataPoints[j].color = (dataSeries.dataPoints[j].y[0] <= dataSeries.dataPoints[j].y[3]) ? (dataSeries.risingColor ? dataSeries.risingColor : dataSeries.color) : (dataSeries.fallingColor ? dataSeries.fallingColor : dataSeries.color);
        }
    }*/
}
export default VolumeChart;