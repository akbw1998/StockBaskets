import React, { useState } from 'react';
import * as FetchAPI from '../../utils/fetch';
import { useEffect } from 'react';
import './CreateInvestoryForm.css';
const CreateInvestoryForm = ({setActiveTab, currentUser}) => {
  console.log(`current user inside create form : `);
  console.log(currentUser);
  const [areStocksLoaded, setAreStocksLoaded] = useState(false);
  const [stocks, setStocks] = useState([]);
  const [selectedStocks, setSelectedStocks] = useState([]);
  const [quantities, setQuantities] = useState({});
  const [description, setDescription] = useState('');
  const [investoryName, setInvestoryName] = useState('');

  useEffect(() => {
    async function fetchData() {
      const response = await FetchAPI.getData(
        "http://localhost:8080/market/stocks"
      );
      const data = await response.json();
      setStocks(data);
      setAreStocksLoaded(true);
    }
    fetchData();
  }, []);

  const handleCheckboxChange = (event) => {
    const stockId = Number(event.target.value);
    const isChecked = event.target.checked;

    if (isChecked) {
      setSelectedStocks([...selectedStocks, stockId]);
    } else {
      setSelectedStocks(selectedStocks.filter((id) => id !== stockId));
    }
  };

  const handleQuantityChange = (event) => {
    const stockId = Number(event.target.name);
    const quantity = Number(event.target.value);

    setQuantities({
      ...quantities,
      [stockId]: quantity,
    });
  };

  const handleDescriptionChange = (event) => {
    setDescription(event.target.value);
  };

  const handleInvestoryNameChange = (event) => {
    setInvestoryName(event.target.value);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const quantifiedStocks = selectedStocks.map((stockId) => ({
      stock: stocks.find((stock) => stock.id === stockId),
      qty: quantities[stockId],
    }));

    const allPositive = quantifiedStocks.every((qs) => qs.qty > 0);

    if (!allPositive) {
      alert('All selected stocks must have a positive quantity.');
    } else {
      const payload = {expertId : currentUser.id, quantifiedStocks, description, investoryName};
      console.log(payload);
      // Submit payload to backend API here
      try {
        const response = await FetchAPI.postData(
          "http://localhost:8080/investories",
          payload
        );
        console.log(response);
        const json = await response.json();
        if (response.ok) {
           alert("Investory successfully added.");
           setActiveTab("My Investories"); 
         } else if(response.status === 409){
           alert("Error adding investory : " + json);
         }else{
           throw new Error(json);
         }
       } catch (error) {
         console.log(error);
         alert("Error : " + error.message);
       }
    }
  };

  if (!areStocksLoaded) {
    return <div>Loading...</div>;
  }

  return (
   <div className='investoryform-container'>
     <h2>Add Investory Form</h2>
     <form onSubmit={handleSubmit} className='investoryform'>
     <div className="field-container">
        <label className="input-label" htmlFor="investory-name">
          Investory Name :
        </label>
        <input type = "text" required name = "investory-name" style = {{marginLeft: 10}} onChange={handleInvestoryNameChange}/>
    </div>
      <p style = {{fontWeight : 'bold',textDecoration: 'underline'}}>Select stocks to add to investory</p>
       {stocks.map((stock) => (
         <div key={stock.id} className = "field-container">
           <label className="input-label">
             <input
               type="checkbox"
               value={stock.id}
               checked={selectedStocks.includes(stock.id)}
               onChange={handleCheckboxChange}
             />
             {stock.company}
           </label>
           {selectedStocks.includes(stock.id) && (
            <>
             <div className="input-container">
               <label className="input-label" htmlFor={stock.company}>
                 Quantity:
               </label>
               <input
                 className="input-field"
                 type="number"
                 id={stock.company}
                 name={String(stock.id)}
                 value={quantities[stock.id] || ''}
                 onChange={handleQuantityChange}
                 min={1}
               />
             </div>
             <div className="input-container">
             <label className = 'input-label'>
                 Current Share Price:
             </label>
             <p>
               {stock.currentSharePrice.toFixed(2)}
             </p>
             </div>
             </>
           )}
         </div>
       ))}
       <div className="field-container">
        <label className="input-label" htmlFor="description">
          Description:
        </label>
        <textarea
          rows = "5"
          cols = "30"
          id="description"
          name="description"
          onChange={handleDescriptionChange}
          value={description}
          required
        />
      </div>
       <button type="submit">Submit</button>
     </form>
   </div>
 );
};

export default CreateInvestoryForm;
