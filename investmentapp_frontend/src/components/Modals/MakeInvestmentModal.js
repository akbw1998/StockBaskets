import { useState } from 'react';
import './MakeInvestmentModal.css';
import {getData, postData} from '../../utils/fetch';
import { useDispatch } from 'react-redux';
import { UserManagementSliceActions } from '../../store/UserManagement-slice';
import { useEffect } from 'react';
const MakeInvestmentModal = ({closeModal, currentUser, investory, investorySharePrice}) => {
   const [shareQty, setShareQty] = useState(null);
   const [investmentAmount, setInvestmentAmount] = useState(null);
   const enableScroll = () => {document.body.style.overflow = ''}
   const disableScroll = () => {document.body.style.overflow = 'hidden'}
   const dispatch = useDispatch();

   useEffect(() =>disableScroll(),[]);

   useEffect(() => {
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

   const handleMakeInvestment = async (e) =>{
      e.preventDefault();
      console.log('------inside handleMakeInvestment------');
      console.log('-------current user-----');
      console.log(currentUser);
      // console.log(currentUser);
      console.log('investmentAmount : ' + investmentAmount);
      console.log('shareQty Selected : ' + shareQty);
      const data = {
         "investoryId" : investory.id,
         "investorySharesToAdd" : shareQty,
         "investmentAmountToAdd" : investmentAmount
     }
     console.log('data json : ');
     console.log(data);
      try{
         console.log("----endpoint to POST new investment------");
         const endpoint = `http://localhost:8080/investors/${currentUser.id}/investments`;
         console.log(endpoint)
         const makeInvestmentResponse = await postData(endpoint, data);
         const makeInvestmentJSON = await makeInvestmentResponse.json();
         if( makeInvestmentResponse.ok){
            alert('-----New Investment Added-----')
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
            if (typeof makeInvestmentJSON === 'string') {
               error.message = makeInvestmentJSON;
            } else {
               error.message = makeInvestmentJSON.message || "Error POSTing a new investment";
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
      <div className = 'make-investment-modal-container'>
         <div className = 'make-investment-modal'>
            <div className = "make-investment-modal-header">
               Make an Investment
               <button className = "close-btn" onClick = {handleClose}>X</button>
            </div>
            <form className = 'modal-body' onSubmit={handleMakeInvestment}>
               <label htmlFor='share-qty'>Share Qty : </label>
               <input id = 'share-qty' name = 'share-qty' type ='number' required min={1} onChange={handleShareQtyChange} className='share-inp'/>
               {(investmentAmount !== null && investmentAmount !== 0) && <p className = 'investment-amount-display'>Investment Amount : {"$" + investmentAmount.toFixed(2)}</p>}
               <button type = 'submit'>Invest</button>
            </form>

         </div>
      </div>
   );
}
 
export default MakeInvestmentModal;