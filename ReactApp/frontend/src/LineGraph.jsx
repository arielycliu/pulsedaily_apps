import { XYPlot, XAxis, YAxis, LineSeries, VerticalGridLines, HorizontalGridLines } from 'react-vis';
import '../node_modules/react-vis/dist/style.css';
export default function LineGraph({ data }) {
    const lineTranformed = data.map((item, index) => (
        {
            x: index,
            y: item.response_rate
        }
    ));
    console.log(lineTranformed);
    return (
        <XYPlot width={400} height={300} stroke="#F3703B" >
            <VerticalGridLines />
            <HorizontalGridLines />
            <XAxis title="day" />
            <YAxis title="response rate" />
            {/* <AreaSeries data={lineTranformed} /> */}
            <LineSeries data={lineTranformed} />
        </XYPlot>
    )
}