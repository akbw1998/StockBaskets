import Header from "../../components/Header/Header";
import ProfileBar from "../../components/ProfileBar/ProfileBar";
import { useDispatch, useSelector } from 'react-redux';
import { UserManagementSliceActions } from "../../store/UserManagement-slice";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Tab from "../../components/Tab/Tab";
import { useState } from "react";
import ExpertList from "../../components/ExpertList/ExpertList";
import './Investor.css';
import AddToWalletModal from "../../components/Modals/AddToWalletModal";
import InvestorInvestoryList from "../../components/InvestoryList/InvestorInvestoryList";
import {getData, updateData } from "../../utils/fetch";

const Investor = () => {
   const currentRole = useSelector(state => state.userManagement.role);
   const currentUser = useSelector(state => state.userManagement.currentUser);
   const [isUserLoaded, setIsUserLoaded] = useState(false);
   const [isAddToWalletModalOpen, setIsAddToWalletModalOpen] = useState(false);
   const dispatch = useDispatch();
   const navigate = useNavigate();
   const [activeTab, setActiveTab] = useState('Investories');
   // loading from localstorage to store if url accessed directly instead of redirecting from home/login
   useEffect(() => {
    const user = JSON.parse(localStorage.getItem("user"));
    const role = localStorage.getItem("role");
    if(user){
      if(role !== 'investor'){
        navigate('/authorizationerror')
      }
      dispatch(UserManagementSliceActions.setCurrentUser(user));
      dispatch(UserManagementSliceActions.setRole(role));
      setIsUserLoaded(true);
    }else{
      navigate('/authenticationerror');
    }
  }, []);

  useEffect(() => {
    const intervalId = setInterval(() => {
      updateMarketData(); // call the function to update market data every 10 seconds
    }, 10000); // 10000 milliseconds = 10 seconds

    return () => clearInterval(intervalId); // cleanup function to clear the interval when component unmounts
  }, [currentUser]); // run only after currentUser is set

  async function updateMarketData() {
    try {
      const updateMarketResponse = await updateData("http://localhost:8080/market", { /* data to update */ });
      const updateMarketResponseJSON = await updateMarketResponse.json();
      console.log(updateMarketResponseJSON); // log the response data
      if(updateMarketResponse.ok){
        // alert('-----Market Updated-----')
        // dispatch(UserManagementSliceActions.setCurrentUser(userJSON));
        console.log('-----Endpoint used to GET updated user--------');
        console.log(`http://localhost:8080/investors/${currentUser.id}`);
        const userResponse = await getData(`http://localhost:8080/investors/${currentUser.id}`);
        console.log(userResponse);
        const userResponseJSON = await userResponse.json();
        if(userResponse.ok){
          //  alert('-----Fetched updated user-----');
           dispatch(UserManagementSliceActions.setCurrentUser(userResponseJSON));
           localStorage.setItem('user', JSON.stringify(userResponseJSON));
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
        if (typeof updateMarketResponseJSON === 'string') {
           error.message = updateMarketResponseJSON;
        } else {
           error.message = updateMarketResponseJSON.message || "Error updating investment";
        }
        throw error;
     }
  }catch(error){
     console.log('------In catch error------')
     console.log(error);
     alert(`${error.message}`);
  };
  }

  const handleTabClick = (tabName) => {
    setActiveTab(tabName);
  };

  const closeAddToWalletModal = () => {setIsAddToWalletModalOpen(false);}

   return (
      <>
      <Header/>
      <ProfileBar>
        <div className="profile-bar__detail">
          <span className="profile-bar__label">Wallet:</span>
          <span className="profile-bar__value">{isUserLoaded ? ("$" + currentUser.wallet.toFixed(2)): "Loading..."}</span>
          <span className="profile-bar__btn">{isUserLoaded ? <button className = "add-to-wallet" onClick = {()=>setIsAddToWalletModalOpen(true)}>Add to wallet</button>: "Loading..."}</span>
          <span className="profile-bar__label">Portfolio:</span>
          <span className="profile-bar__value">{isUserLoaded ? (currentUser.portfolio > 0 ? ("$" + currentUser.portfolio.toFixed(2)) : "$0.00") : "Loading..."}</span>
          <span className="profile-bar__label">Total amount invested:</span>
          <span className="profile-bar__value">{isUserLoaded ? (currentUser.amountInvested > 0 ? ("$" + currentUser.amountInvested.toFixed(2)) : "$0.00") : "Loading..."}</span>
        </div>
        <div className="profile-bar__detail">
          <span className="profile-bar__label">Email:</span>
          <span className="profile-bar__value">{isUserLoaded ? currentUser.email : "Loading..."}</span>
          <span className="profile-bar__label">Role:</span>
          <span className="profile-bar__value">{isUserLoaded ? currentRole : "Loading..."}</span>
        </div>
      </ProfileBar>
      <div className="tabs">
        <Tab
          tabName="Investories"
          isActive={activeTab === 'Investories'}
          onClick={handleTabClick}
        />
        <Tab
          tabName="Experts"
          isActive={activeTab === 'Experts'}
          onClick={handleTabClick}
        />
      </div>
      {isAddToWalletModalOpen && <AddToWalletModal currentUser={currentUser} closeModal = {closeAddToWalletModal}/>}
      {(activeTab === 'Experts' && isUserLoaded) && <ExpertList currentUser = {currentUser}/>}
      {(activeTab === 'Investories' && isUserLoaded) && <InvestorInvestoryList currentUser = {currentUser} />}
      </>
   );
}
 
export default Investor;