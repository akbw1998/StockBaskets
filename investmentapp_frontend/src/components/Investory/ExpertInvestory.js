import './ExpertInvestory.css';

const ExpertInvestory = ({investory}) => {
   const { investoryName, quantifiedStocks, changePercentage, description, initialPrice} = investory;
 
   return (
     <div className="investory">
      <div className='investory-header'>
        <div className = "investory-name">{investoryName}</div>
      </div>
       <div className='investory-desc'>
         <p className='desc-title'>Description</p>
         <p className = 'desc-content'>{description}</p>
       </div>
       <div className="stock-info">
         <p className='stock-info-title'>Stock Composition</p>
         {quantifiedStocks.map((qs) => (
           <div key={qs.stock.id} className="stock-composition">
             <div className="stock-company">{qs.stock.company}</div>
             <div className="stock-qty">Qty: {qs.qty}</div>
             <div className="stock-price">
               Share Price: {"$" + qs.stock.currentSharePrice.toFixed(2)}
             </div>
           </div>
         ))}
       </div>
       
       <div className="stats">
         <p className='stats-title'>Investory Stats</p>
         <p>Initial Investory share price : ${initialPrice.toFixed(2)}</p>
         <p>Current Investory share price : ${investory.quantifiedStocks.reduce((acc, qs) => {
         return acc + (qs.qty * qs.stock.currentSharePrice);
       }, 0).toFixed(2)}</p>
         <p> Net profit % per unit share (since creation): <span className={changePercentage >= 0 ? "profit-positive" : "profit-negative"}>{changePercentage.toFixed(1)}%</span> </p>
       </div>
     </div>
   );
 }

 export default ExpertInvestory;