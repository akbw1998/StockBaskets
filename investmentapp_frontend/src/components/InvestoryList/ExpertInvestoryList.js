import * as FetchAPI from '../../utils/fetch';
import { useState } from 'react';
import { useEffect } from 'react';
import ExpertInvestory from '../Investory/ExpertInvestory';
import './ExpertInvestoryList.css';

const ExpertInvestoryList = ({currentUser}) => {
   const [investories, setInvestories] = useState([]);
   const [areInvestoriesLoaded, setAreInvestoriesLoaded] = useState(false);

   useEffect(() => {
     async function fetchData() {
       const response = await FetchAPI.getData(`http://localhost:8080/investories?expertId=${currentUser.id}`);
       const data = await response.json();
       console.log("--------Investories data loaded---------------");
       console.log(data);
       setInvestories(data);
       setAreInvestoriesLoaded(true);
     }
     fetchData();
   }, [currentUser]);

   if(!areInvestoriesLoaded){return "Loading...";}

   return (
     <div className="investory-list">
       {investories.map((inv) => (
         <ExpertInvestory key={inv.id} investory={inv} />
       ))}
     </div>
   );
 }
 
 export default ExpertInvestoryList;