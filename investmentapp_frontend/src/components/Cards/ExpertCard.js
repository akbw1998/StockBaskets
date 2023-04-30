import './ExpertCard.css';

const ExpertCard = (props) => {

   const expert = props.expert;
   const userSubscriptions = props.userSubscriptions;
   const handleUnsubscribe = props.handleUnsubscribe;
   const handleSubscribe = props.handleSubscribe;
   const selectedType = props.selectedType;
   const handleTypeChange = props.handleTypeChange;
   const isLoadingSubscriptions = props.isLoadingSubscriptions;

   return (
      <li key={expert.id}>
               <div className='expertcard'>
                  <div className = "expertcard-header">
                     <p><span className='header-title'>Expert Name</span> : {expert.username}</p>
                     <p><span className='header-title'>Expert Email</span> : {expert.email}</p>
                  </div>
                  <div className = "expertcard-body">
                     {!isLoadingSubscriptions && userSubscriptions.some(subscription => subscription.expert === expert.id)
                     ? <div className = 'unsubscribe-container'>
                        <p>Subscription type: {userSubscriptions.find(subscription => subscription.expert === expert.id).type} (no refunds)</p>
                        <button onClick={() => handleUnsubscribe(expert.id)}>Unsubscribe</button>
                        </div>
                     : <div className='subscription-container'>
                        <p className = 'subscription-title'>Subscription Options</p>
                        <div className='subscription-options'>
                        <label>
                           <input type="radio" name={`subscription-${expert.id}`} value="MONTHLY - 30" checked={selectedType === `MONTHLY - 30 - subscription-${expert.id}`} onChange={handleTypeChange} />
                           MONTHLY - 30$
                        </label>
                        <label>
                           <input type="radio" name={`subscription-${expert.id}`} value="QUARTERLY - 100" checked={selectedType === `QUARTERLY - 100 - subscription-${expert.id}`} onChange={handleTypeChange} />
                           QUARTERLY - 100$
                        </label>
                        <label>
                           <input type="radio" name={`subscription-${expert.id}`} value="ANNUAL - 300" checked={selectedType === `ANNUAL - 300 - subscription-${expert.id}`} onChange={handleTypeChange} />
                           ANNUAL - 300$
                        </label>
                        <button disabled={!selectedType.includes(`subscription-${expert.id}`)} onClick={() => handleSubscribe(expert.id, selectedType.split(' - ')[1], selectedType.split(' - ')[0])}>
                           Subscribe
                        </button>
                        </div>
                        </div>
                     }
                  </div>
               </div>
            </li>
   );
}
 
export default ExpertCard;
