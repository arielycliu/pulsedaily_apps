import React from 'react';
import { RadialChart } from 'react-vis';

const PieChart = ({ data }) => {
    console.log(data);
    const ratingsTransformed = data.map((item) => (
        {
            label: item.rating,
            angle: item.rating_count
        }
    ));

    return (
        <>
            <div className='piechart'>
                <RadialChart
                    data={ratingsTransformed}
                    width={300}
                    height={330}
                    labelsRadiusMultiplier={1.15}
                    showLabels
                    animation
                />
            </div>
        </>
    );
};

export default PieChart;
