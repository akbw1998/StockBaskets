import React from 'react';
import './ProfileBar.css';

const ProfileBar = ({ children }) => {
  return (
      <div className="profile-bar__container">
        <div className="profile-bar__details">
          {children}
        </div>
    </div>
  );
};

export default ProfileBar;