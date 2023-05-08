import { useState } from 'react';
import './UpdateInvestmentModal.css';
import {getData, updateData} from '../../utils/fetch';
import { useDispatch } from 'react-redux';
import { UserManagementSliceActions } from '../../store/UserManagement-slice';
import { useEffect } from 'react';



const UpdateInvestmentModal = ({closeModal, currentUser, investory, investorySharePrice, investment}) => {
   const [shareQty, setShareQty] = useState(null);
   const [investmentAmount, setInvestmentAmount] = useState(null);
   const dispatch = useDispatch();
   const enableScroll = () => {document.body.style.overflow = ''}
   const disableScroll = () => {document.body.style.overflow = 'hidden'}

   useEffect(()=>disableScroll(), []);
   useEffect(() => {
      console.log("--------RAN---------");
      // const unitInvestmentAmount = investory.quantifiedStocks.reduce((acc, qs) => {
      //    return acc + (qs.qty * qs.stock.currentSharePrice);
      //  }, 0);
      
      // console.log('investory share price : ' + investorySharePrice);
      setInvestmentAmount(shareQty*investorySharePrice);
    }, [investorySharePrice]); // run only after currentUser is set

   const handleClose = () =>{
      enableScroll();
      closeModal();
   }

   const handleShareQtyChange = (event) =>{
      const shareQty = event.target.value;
      setShareQty(shareQty);
      const unitInvestmentAmount = investory.quantifiedStocks.reduce((acc, qs) => {
         return acc + (qs.qty * qs.stock.currentSharePrice);
       }, 0);
      setInvestmentAmount(shareQty*unitInvestmentAmount);
   }

   const handleUpdateInvestment = async (e) =>{
      e.preventDefault();
      console.log('------inside handleUpdateInvestment------');
      console.log('-------current user-----');
      console.log(currentUser);
      // console.log(currentUser);
      console.log('investmentAmount : ' + investmentAmount);
      console.log('shareQty Selected : ' + shareQty);
      const data = {
         "investorySharesToAdd" : shareQty,
         "investmentAmountToAdd" : investmentAmount
     }
     console.log('data json : ');
     console.log(data);
      try{
         console.log("----endpoint to UPDATE investment------");
         const endpoint = `http://localhost:8080/investors/${currentUser.id}/investments/${investment.id}`;
         console.log(endpoint)
         const updateInvestmentResponse = await updateData(endpoint, data);
         const updateInvestmentJSON = await updateInvestmentResponse.json();
         if(updateInvestmentResponse.ok){
            alert('-----Investment Updated-----')
            // dispatch(UserManagementSliceActions.setCurrentUser(userJSON));
            console.log('-----Endpoint used to GET updated user--------');
            console.log(`http://localhost:8080/investors/${currentUser.id}`);
            const userResponse = await getData(`http://localhost:8080/investors/${currentUser.id}`);
            console.log(userResponse);
            const userResponseJSON = await userResponse.json();
            if(userResponse.ok){
               // alert('-----Fetched updated user-----');
               dispatch(UserManagementSliceActions.setCurrentUser(userResponseJSON));
               localStorage.setItem('user', JSON.stringify(userResponseJSON));
               handleClose();
            }else{
               alert('---Fetching user failed-----');
               const error = new Error();
               if (typeof userResponseJSON === 'string') {
                  error.message = userResponseJSON;
               } else {
                  error.message = userResponseJSON.message || "Error fetching current user by id";
               }
               throw error;
            }
         }else{
            const error = new Error();
            if (typeof updateInvestmentJSON === 'string') {
               error.message = updateInvestmentJSON;
            } else {
               error.message = updateInvestmentJSON.message || "Error updating investment";
            }
            throw error;
         }
      }catch(error){
         console.log('------In catch error------')
         console.log(error);
         alert(`${error.message}`);
      };
   }

   return (
      <div className = 'update-investment-modal-container'>
         <div className = 'update-investment-modal'>
            <div className = "update-investment-modal-header">
               Add to Existing Investment
               <button className = "close-btn" onClick = {handleClose}>X</button>
            </div>
            <form className = 'modal-body' onSubmit={handleUpdateInvestment}>
               <p className = 'owned-shares'>Owned shares : {investment.totalInvestoryShares}</p>
               <p className = 'amount-invested'>Amount Invested : {"$" + investment.totalAmountInvested.toFixed(2)}</p>
               <p className = 'investment-value'>Investment Value : {"$" + investment.totalInvestmentValue.toFixed(2)}</p>
               <label htmlFor='share-qty'>Share Qty to Add : </label>
               <input id = 'share-qty' name = 'share-qty' type ='number' required min={1} onChange={handleShareQtyChange} className='share-inp'/>
               {(investmentAmount !== null && investmentAmount !== 0) && <p className = 'investment-amount-display'>Investment Amount : {"$" + investmentAmount.toFixed(2)}</p>}
               <button type = 'submit'>Invest</button>
            </form>

         </div>
      </div>
   );
}
 
export default UpdateInvestmentModal;