import React from 'react';

const BigNumber = ({ number, message, color }) => {
    return (
        <div className='BigNumber'>
            <h2>{message}</h2>
            <div className='innerdiv' style={{ backgroundColor: color}}>
                {number}
            </div>
        </div>
    );
};
export default BigNumber;
