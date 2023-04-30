import { useState } from 'react';
import './AddToWalletModal.css';
import {getData, updateData} from '../../utils/fetch';
import { useDispatch } from 'react-redux';
import { UserManagementSliceActions } from '../../store/UserManagement-slice';

const AddToWalletModal = ({closeModal, currentUser}) => {
   const [depositAmount, setDepositAmount] = useState(null);
   const dispatch = useDispatch();
   const handleClose = () =>{
      closeModal();
   }
   const handleDepositChange = (event) =>{
      setDepositAmount(event.target.value);
   }

   const handleAddToWallet = async (e) =>{
      e.preventDefault();
      console.log('------current user inside modal------');
      console.log(currentUser);
      console.log('amount selected : ' + depositAmount);
      const data = {'amount' : depositAmount};
      try{
         const updateWalletResponse = await updateData(`http://localhost:8080/investors/${currentUser.id}/wallet`, data);
         const updateWalletJSON = await updateWalletResponse.json();
         if(updateWalletResponse.ok){
            alert('-----Wallet updated-----')
            // dispatch(UserManagementSliceActions.setCurrentUser(userJSON));
            console.log('-----Endpoint used --------');
            console.log(`http://localhost:8080/investors/${currentUser.id}`);
            const userResponse = await getData(`http://localhost:8080/investors/${currentUser.id}`);
            console.log(userResponse);
            const userResponseJSON = await userResponse.json();
            if(userResponse.ok){
               // alert('-----Fetched updated user-----');
               dispatch(UserManagementSliceActions.setCurrentUser(userResponseJSON));
               handleClose();
            }else{
               alert('---FEtching user failed-----');
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
            if (typeof updateWalletJSON === 'string') {
               error.message = updateWalletJSON;
            } else {
               error.message = updateWalletJSON.message || "Error updating user wallet";
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
      <div className = 'add-to-wallet-modal-container'>
         <div className = 'add-to-wallet-modal'>
            <div className = "add-to-waller-modal-header">
               Add to Wallet
               <button className = "close-btn" onClick = {handleClose}>X</button>
            </div>
            <form className = 'modal-body' onSubmit={handleAddToWallet}>
               <label htmlFor='deposit-amount'>Deposit Amount : </label>
               <input name = 'deposit-amount' type ='number' required min={1} onChange={handleDepositChange} className='deposit-input'/>
               <button type = 'submit'>Deposit</button>
            </form>
         </div>
      </div>
   );
}
 
export default AddToWalletModal;