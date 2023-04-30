import { useState, useEffect } from 'react';
import { getData, postData, deleteData } from '../../utils/fetch';
import './ExpertList.css';
import { useDispatch } from 'react-redux';
import { UserManagementSliceActions } from '../../store/UserManagement-slice';
import ExpertCard from '../Cards/ExpertCard';

const ExpertList = ({currentUser}) => {
  const [experts, setExperts] = useState([]);
  const [isLoadingExperts, setIsLoadingExperts] = useState(true);
  const [userSubscriptions, setUserSubscriptions] = useState([]);
  const [isLoadingSubscriptions, setIsLoadingSubscriptions] = useState(true);
  const [selectedType, setSelectedType] = useState('');
  const dispatch = useDispatch();

  useEffect(() => {
    setIsLoadingExperts(true);
    // Fetch list of all experts
    getData("http://localhost:8080/experts")
      .then(response => response.json())
      .then(data => {
        setExperts(data);
        setIsLoadingExperts(false);
      //   alert('Loaded experts first time');
        console.log('------Expert List-------');
        console.log(experts);
      })
      .catch(error => console.log(error));

    // Fetch list of user's subscriptions
    getData(`http://localhost:8080/subscriptions?investorId=${currentUser.id}`)
      .then(response => response.json())
      .then(data => {
         setUserSubscriptions(data);
         setIsLoadingSubscriptions(false);
         // alert('Loaded subscriptions first time');
         console.log('----Subscriptions List of user -----');
         console.log(userSubscriptions);
      })
      .catch(error => console.log(error));
  }, []);

  const handleSubscribe = async (expertId, price, type) => {
    // Post request to subscribe to an expert
    const data = { expertId, price,investorId: currentUser.id, type };
    try{
      const response = await postData('http://localhost:8080/subscriptions', data);
      console.log(response);
      const json = await response.json();
      console.log(json);
      if(response.ok){
         setUserSubscriptions([...userSubscriptions, json]);
         alert('Added subscription');
         console.log("----New subscription-----");
         console.log(json);
         console.log('---------Fetching updated user---------');
         const userResponse = await getData(`http://localhost:8080/investors/${currentUser.id}`);
         const userJSON = await userResponse.json();
         if(userResponse.ok){
            dispatch(UserManagementSliceActions.setCurrentUser(userJSON));
            // alert('-----current user updated----');
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
               error.message = json.message || "Error Adding subscription";
            }
            throw error;
        }
      }catch(error){
         console.log(error);
         alert(`${error.message}`);
      };
    }

  const handleUnsubscribe = async (expertId) => {
    // Find the subscription object corresponding to the expert
    const subscription = userSubscriptions.find(subscription => subscription.expert === expertId);
    if (!subscription) {
      console.log('Subscription not found');
      return;
    }
    try{
      const response = await deleteData(`http://localhost:8080/subscriptions/${subscription.id}`);
    console.log(response);
    const json = await response.json();
    console.log(json);
    if(response.ok){
      setUserSubscriptions(userSubscriptions.filter(subscription => subscription.expert !== expertId));
      // alert(json);
      console.log('---------Fetching updated user---------');
         const userResponse = await getData(`http://localhost:8080/investors/${currentUser.id}`);
         const userJSON = await userResponse.json();
         if(userResponse.ok){
            dispatch(UserManagementSliceActions.setCurrentUser(userJSON));
            localStorage.setItem('user', JSON.stringify(userJSON));
            // alert('-----current user updated----');
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
            error.message = json.message || "Error Deleting subscription";
         }
         throw error;
     }
   }catch(error){
      console.log(error);
      alert(`${error.message}`);
   };
}

  const handleTypeChange = (event) => {
    console.log(event);
    setSelectedType((event.target.value).concat(' - ', event.target.name));
  };

  return (
    <div>
      {isLoadingExperts && <p>Loading Expert List...</p>}
      {!isLoadingExperts && experts.length === 0 && <p>No experts found.</p>}
      {!isLoadingExperts && experts.length > 0 &&
        <ul className = "expertlist">
            {experts.map(expert => <ExpertCard 
                                       key={expert.id}
                                       expert = {expert}
                                       userSubscriptions = {userSubscriptions}
                                       handleUnsubscribe = {handleUnsubscribe}
                                       handleSubscribe = {handleSubscribe}
                                       selectedType = {selectedType}
                                       handleTypeChange = {handleTypeChange}
                                       isLoadingSubscriptions = {isLoadingSubscriptions}
                                       />)}
          
         </ul>
      }
   </div>
  )

}

export default ExpertList;
