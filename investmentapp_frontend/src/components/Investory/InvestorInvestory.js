import { useState } from 'react';
import './InvestorInvestory.css';
import MakeInvestmentModal from '../Modals/MakeInvestmentModal';
import { getData,deleteData } from '../../utils/fetch';
import { useDispatch } from 'react-redux';
import { UserManagementSliceActions } from '../../store/UserManagement-slice';
import UpdateInvestmentModal from '../Modals/UpdateInvestmentModal';
const InvestorInvestory = ({investory,currentUser}) => {
   const {investoryName, expertName, expertEmail, quantifiedStocks, changePercentage, description, initialPrice} = investory;
   
   const subscription = currentUser.subscriptions.find(subscription => subscription.expert === investory.expert);
   const investment = currentUser.investments.find(investment => investment.investory === investory.id);

   const [isMakeInvestmentModalOpen, setIsMakeInvestmentModalOpen] = useState(false);
   const [isUpdateInvestmentModalOpen, setIsUpdateInvestmentModalOpen] = useState(false);

   const investorySharePrice = investory.quantifiedStocks.reduce((acc, qs) => {
      return acc + (qs.qty * qs.stock.currentSharePrice);
    }, 0);

   const dispatch = useDispatch();

   const handleWithdrawInvestment = async () => {
      try{
      const endpoint = `http://localhost:8080/investors/${currentUser.id}/investments/${investment.id}`;
      console.log('--------In handleWidthdrawInvestment-------');
      console.log('-------Delete endpoint---------');
      console.log(endpoint);
      const response = await deleteData(endpoint);
      console.log("Response : ");
      console.log(response);
      const json = await response.json();
      console.log('Response.json() = ');
      console.log(json);
      if(response.ok){
        console.log('---------Fetching updated user---------');
        const endpoint = `http://localhost:8080/investors/${currentUser.id}`;
        console.log('endpoint = ' + endpoint);
           const userResponse = await getData(endpoint);
           const userJSON = await userResponse.json();
           if(userResponse.ok){
              dispatch(UserManagementSliceActions.setCurrentUser(userJSON));
              localStorage.setItem('user', JSON.stringify(userJSON));
            //   alert('-----current user updated----');
           }else{
              const error = new Error();
              if (typeof json === 'string') {
                 error.message = json;
              } else {
                 error.message = json.message || "Error fetching current user by id";
              }
              throw error;
           }
       }else{
        const error = new Error();
           if (typeof json === 'string') {
              error.message = json;
           } else {
              error.message = json.message || "Error Deleting investment";
           }
           throw error;
       }
     }catch(error){
        console.log(error);
        alert(`${error.message}`);
     };
  }

   return (
      <>
     <div className="investory">
      <div className='investory-header'>
        <div className = "investory-name">{investoryName}</div>
        <div className = 'expert-details'>
            <p>Expert Name : {expertName}</p>
            <p>Expert Email : {expertEmail}</p>
         </div>
        {/* <p>Test for user : {currentUser.username}</p> */}
      </div>
       <div className='investory-desc'>
         <p className='desc-title'>Description</p>
         <p className='desc-content'>{description}</p>
       </div>
       {subscription ? (
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
       ) : (
         <div className = 'unsubscribed'>
            (Subscribe to Expert to see Stock Composition Information)
         </div>
       )
       }
       <div className="stats">
         <p className='stats-title'>Investory Stats</p>
         <p>Initial Investory share price : ${initialPrice.toFixed(2)}</p>
         <p>Current Investory share price : ${investorySharePrice.toFixed(2)}</p>
         <p> Net profit % per unit share (since creation): <span className={changePercentage >= 0 ? "profit-positive" : "profit-negative"}>{changePercentage.toFixed(1)}%</span> </p>
       </div>
         <div className="investment">
            <p className='investment-title'>Investment Info</p>
            <div className = "investment-body">
                  {/* <p className = 'total-amount-invested'>Total Amount invested : {investment ? ("$" + investment.totalAmountInvested.toFixed(2)) : "$0.00"}</p>
                  <p className = 'total-investment-value'>Total Investment Value : {investment ? ("$" + investment.totalInvestmentValue.toFixed(2)) : "$0.00"}</p> */}
                     {investment ? (
                        subscription ? (
                           <>
                           <div>
                              <p className = 'total-shares-invested'>Total Investory Shares owned : {investment ? (investment.totalInvestoryShares) : "0"}</p>
                              <p className = 'total-amount-invested'>Total Amount invested : {investment ? ("$" + investment.totalAmountInvested.toFixed(2)) : "$0.00"}</p>
                              <p className = 'total-investment-value'>Total Investment Value : {investment ? ("$" + investment.totalInvestmentValue.toFixed(2)) : "$0.00"}</p>
                           </div>
                        
                           <div className='investment-actions'>
                              <button onClick={() => setIsUpdateInvestmentModalOpen(true)}>Add to Investment</button>
                              <button onClick = {() => handleWithdrawInvestment()}>Withdraw Investment</button>
                           </div>
                        </>
                        ) : (
                           <>
                           <div>
                              <p className = 'total-shares-invested'>Total Investory Shares owned : {investment ? (investment.totalInvestoryShares) : "0"}</p>
                              <p className = 'total-amount-invested'>Total Amount invested : {investment ? ("$" + investment.totalAmountInvested.toFixed(2)) : "$0.00"}</p>
                              <p className = 'total-investment-value'>Total Investment Value : {investment ? ("$" + investment.totalInvestmentValue.toFixed(2)) : "$0.00"}</p>
                           </div>
                           <div className = 'unsubscribed'>
                              (Subscribe to Expert to make/add-to investments)
                           </div>
                           <div className = 'investment-actions'>
                              <button onClick={() => handleWithdrawInvestment()}>Withdraw Investment</button>
                           </div>
                           </>
                        )
                     ) : (subscription ?
                        (
                        <>
                           <div>
                              <p className = 'total-shares-invested'>Total Investory Shares owned : {investment ? (investment.totalInvestoryShares) : "0"}</p>
                              <p className = 'total-amount-invested'>Total Amount invested : {investment ? ("$" + investment.totalAmountInvested.toFixed(2)) : "$0.00"}</p>
                              <p className = 'total-investment-value'>Total Investment Value : {investment ? ("$" + investment.totalInvestmentValue.toFixed(2)) : "$0.00"}</p>
                           </div>
                           <div className = 'investment-actions'>
                              <button onClick={() => setIsMakeInvestmentModalOpen(true)}>Make Investment</button>
                           </div>
                        </>
                     ) : (
                        <>
                           <div className = 'unsubscribed'>
                              (Subscribe to Expert to make/add-to investments)
                           </div>
                        </>
                     )
                     )
                   }
            </div>
         </div>
     </div>
     {isMakeInvestmentModalOpen && <MakeInvestmentModal investory = {investory} investorySharePrice = {investorySharePrice} currentUser={currentUser} closeModal = {setIsMakeInvestmentModalOpen}/>}
     {isUpdateInvestmentModalOpen && <UpdateInvestmentModal investory = {investory} investorySharePrice = {investorySharePrice} currentUser={currentUser} investment = {investment} closeModal = {setIsUpdateInvestmentModalOpen}/>}
     </>

   );
 }

 export default InvestorInvestory;