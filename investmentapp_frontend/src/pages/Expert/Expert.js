import Header from "../../components/Header/Header";
import ProfileBar from '../../components/ProfileBar/ProfileBar';
import { useDispatch, useSelector } from 'react-redux';
import { useState } from "react";
import Tab from "../../components/Tab/Tab";
import CreateInvestoryForm from "../../components/Forms/CreateInvestoryForm";
import { UserManagementSliceActions } from "../../store/UserManagement-slice";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import ExpertInvestoryList from "../../components/InvestoryList/ExpertInvestoryList";
import { getData, updateData } from "../../utils/fetch";
const Expert = () => {
   const currentRole = useSelector(state => state.userManagement.role);
   const currentUser = useSelector(state => state.userManagement.currentUser);
   const [isUserLoaded, setIsUserLoaded] = useState(false);
   const [activeTab, setActiveTab] = useState('Create Investory');
   const dispatch = useDispatch();
   const navigate = useNavigate();

   // loading from localstorage to store if url accessed directly instead of redirecting from home/login
   useEffect(() => {
    const user = JSON.parse(localStorage.getItem("user"));
    const role = localStorage.getItem("role");
    console.log("user = ");
    console.log(user);
    console.log("role = ");
    console.log(role);
    if(user){
      if(role !== 'expert'){
        navigate('/authorizationerror');
      }
      dispatch(UserManagementSliceActions.setCurrentUser(user));
      dispatch(UserManagementSliceActions.setRole(role));
      setIsUserLoaded(true);
    }else{
      navigate("/authenticationerror");
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
        console.log(`http://localhost:8080/experts/${currentUser.id}`);
        const userResponse = await getData(`http://localhost:8080/experts/${currentUser.id}`);
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

   return (
      <>
      <Header />
      <ProfileBar>
        <div className="profile-bar__detail">
          <span className="profile-bar__label">Email:</span>
          <span className="profile-bar__value">{isUserLoaded ? currentUser.email : "Loading email..."}</span>
          <span className="profile-bar__label">Role:</span>
          <span className="profile-bar__value">{isUserLoaded ? currentRole : "Loading role..."}</span>
        </div>
      </ProfileBar>
      
      <div className="tabs">
        <Tab
          tabName="Create Investory"
          isActive={activeTab === 'Create Investory'}
          onClick={handleTabClick}
        />
        <Tab
          tabName="My Investories"
          isActive={activeTab === 'My Investories'}
          onClick={handleTabClick}
        />
      </div>
      {(activeTab === 'Create Investory' && isUserLoaded) && <CreateInvestoryForm setActiveTab = {setActiveTab} currentUser = {currentUser} />}
      {(activeTab === 'My Investories' && isUserLoaded) && <ExpertInvestoryList currentUser = {currentUser} />}
      </>
  );
}
 
export default Expert;