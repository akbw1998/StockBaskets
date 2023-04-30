import React, { useState } from "react";
import './LoginForm.css';
import { useNavigate } from "react-router-dom";
import * as FetchAPI from '../../utils/fetch';
import { useDispatch } from "react-redux";
import { useEffect } from "react";
import { UserManagementSliceActions } from "../../store/UserManagement-slice";
import { useSelector } from "react-redux";
const LoginForm = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const currentUserRole = useSelector(state => state.userManagement.role); // for sending authorized users to their page
  const currentUser = useSelector(state => state.userManagement.currentUser); // for sending authorized users to their page

  useEffect(() => { // basically logged in user shouldnt be able to come to this page through url manipulation. Send him to his page
   console.log("current user : ");
   console.log(currentUser);
   console.log("current role : " + currentUserRole);
   if (currentUser) {
     if (currentUserRole === 'investor') {
       navigate('/investor');
     } else if (currentUserRole === 'expert') {
       navigate('/expert');
     }
   }
 }, [navigate]);

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
    // perform validation and submit the form data
    const params = window.btoa(`${email}:${password}`);
    const data = {role : role};

    try {
      const response = await FetchAPI.postData(
        "http://localhost:8080/auth/login",
        data,
        {
         Authorization: `Basic ${params}`
        }
      );
      console.log(response);
      const json = await response.json();
      console.log(json);
      if (response.ok) {
        // code here
        console.log("inside ok block login");
        localStorage.setItem("user", JSON.stringify(json));
        localStorage.setItem("role", role);
        dispatch(UserManagementSliceActions.setCurrentUser(json));
        dispatch(UserManagementSliceActions.setRole(role));
        if(role === 'investor'){
            navigate('/investor');
        }else
            navigate('/expert');
        }
        else{
         const error = new Error();
         if (typeof json === 'string') {
            error.message = json;
          } else {
            error.message = json.message || "Error logging in";
          }
         setErrorMessage(error.message);
       }
     } catch (error) {
       console.log(error);
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
            <p className = "form-title">Login</p>
            <button className="close" onClick = {handleClose}>X</button>
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
      <button type="submit">Login</button>
      {errorMessage && (
      <p className="error-msg" style={{ color: "red" }}>
         {errorMessage}
      </p>
      )}
    </form>
   </div>
  );
};

export default LoginForm;