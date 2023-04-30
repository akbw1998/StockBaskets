import './Tab.css';

const Tab = ({ tabName, isActive, onClick }) => {
  const handleClick = () => {
    onClick(tabName);
  };

  return (
    <div className={`tab ${isActive ? 'active' : ''}`} onClick={handleClick}>
      <div className="tab-button">{tabName}</div>
    </div>
  );
};

export default Tab;
