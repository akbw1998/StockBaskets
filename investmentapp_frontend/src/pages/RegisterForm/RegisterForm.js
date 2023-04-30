import React, { useState } from "react";
import './RegisterForm.css';
import { useNavigate } from "react-router-dom";
import * as FetchAPI from '../../utils/fetch';
const RegisterForm = () => {
  const [displayName, setDisplayName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const navigate = useNavigate();

  const handleDisplayNameChange = (e) => {
    setDisplayName(e.target.value);
  };

  const handleEmailChange = (e) => {
    setEmail(e.target.value);
  };

  const handlePasswordChange = (e) => {
    setPassword(e.target.value);
  };

  const handleRoleChange = (e) => {
    setRole(e.target.value);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const data = {
      username: displayName,
      email,
      password,
      role,
    };
    try {
      const response = await FetchAPI.postData(
        "http://localhost:8080/auth/register",
        data
      );
      console.log(response);
      const json = await response.json();
      if (response.ok) {
         setSuccessMessage("Registration successful.");
         setErrorMessage("");
       } else if(response.status == 409){
         setSuccessMessage("");
         setErrorMessage(json);
       }else{
         throw new Error(json);
       }
     } catch (error) {
       console.log(error);
       setSuccessMessage("");
       setErrorMessage(error.message);
     }
  };

  const handleClose = () =>{
   navigate('/');
  }

  return (
   <div className="form-container">
      <form onSubmit={handleSubmit}>
         <div className = "form-header">
            <p className = "form-title">Register</p>
            <button className="close" onClick = {handleClose}>X</button>
         </div>
      <div>
        <label htmlFor="displayName">Display Name:</label>
        <input
          type="text"
          id="displayName"
          value={displayName}
          onChange={handleDisplayNameChange}
          required
        />
      </div>
      <div>
        <label htmlFor="email">Email:</label>
        <input
          type="email"
          id="email"
          value={email}
          onChange={handleEmailChange}
          pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$"
          required
        />
      </div>
      <div>
        <label htmlFor="password">Password:</label>
        <input
          type="password"
          id="password"
          value={password}
          onChange={handlePasswordChange}
          minLength="7"
          required
        />
      </div>
      <div>
        <label htmlFor="role">Role:</label>
        <select id="role" value={role} onChange={handleRoleChange} required>
          <option value="">Select Role</option>
          <option value="investor">Investor</option>
          <option value="expert">Expert</option>
        </select>
      </div>
      <button type="submit">Register</button>
      {successMessage && (
            <p className="success-msg" style={{ color: "green" }}>
              {successMessage}
            </p>
          )}
      {errorMessage && (
      <p className="error-msg" style={{ color: "red" }}>
         {errorMessage}
      </p>
      )}
    </form>
   </div>
  );
};

export default RegisterForm;