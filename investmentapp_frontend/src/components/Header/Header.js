import { useState } from 'react';
import './Header.css';
import logo from './icons/investment_logo.svg';
import { useDispatch, useSelector } from 'react-redux';
import { UserManagementSliceActions } from '../../store/UserManagement-slice';
import { useNavigate } from 'react-router-dom';

const Header = () => {
  const role = useSelector(state => state.userManagement.role);
  const currentUser = useSelector(state => state.userManagement.currentUser);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const renderLoginForm = () => {
    // Handle login logic here
    navigate('/login');
    // dispatch(UserManagementSliceActions.setRole("Investor"));
    // dispatch(UserManagementSliceActions.setCurrentUser("Richard C."));
  }

  const renderRegisterForm = () => {
    // Handle login logic here
    navigate('/register');
    // dispatch(UserManagementSliceActions.setRole("Investor"));
    // dispatch(UserManagementSliceActions.setCurrentUser("Richard C."));
  }

  const handleLogout = () => {
    // Handle logout logic here
    dispatch(UserManagementSliceActions.setRole(null));
    dispatch(UserManagementSliceActions.setCurrentUser(null));
    localStorage.clear();
    navigate('/');
  }
   
  return (
    <header className="header">
      <div className = 'logo-container'>
        <img className="header__logo" src={logo} />
        <p>StockBaskets - Investments Made Easy</p>
      </div>
      {role ? (
        <div className="header__username">Welcome back {currentUser.username}</div>
      ) : (
        <div className="header__buttons">
          <button className="header__button" onClick={renderLoginForm}>
            Login
          </button>
          {!role && (
            <button className="header__button" onClick = {renderRegisterForm}>Register</button>
          )}
        </div>
      )}
      {role && (
        <button className="header__button header__button--logout" onClick={handleLogout}>
          Logout
        </button>
      )}
    </header>

  );
}

export default Header;
