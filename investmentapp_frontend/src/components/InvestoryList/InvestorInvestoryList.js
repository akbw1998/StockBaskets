import * as FetchAPI from '../../utils/fetch';
import { useState } from 'react';
import { useEffect } from 'react';
import InvestorInvestory from '../Investory/InvestorInvestory';
import './InvestorInvestoryList.css';

const InvestorInvestoryList = ({currentUser}) => {
   const [investories, setInvestories] = useState([]);
   const [areInvestoriesLoaded, setAreInvestoriesLoaded] = useState(false);
 

   useEffect(() => {
     async function fetchData() {
      try{
         const response = await FetchAPI.getData(`http://localhost:8080/investories`);
         const data = await response.json();
         console.log("--------Investories data loaded---------------");
         console.log(data);
         setInvestories(data);
         setAreInvestoriesLoaded(true);
      }catch(e){
         alert(`Error : ${e.message}`);
      }
     }
     fetchData();
   }, [currentUser]);

   if(!areInvestoriesLoaded){return "Loading...";}

   return (
     <div className="investory-list">
       {investories.map((inv) => (
         <InvestorInvestory key={inv.id} investory={inv} currentUser = {currentUser}/>
       ))}
     </div>
   );
 }
 
 export default InvestorInvestoryList;