import Home from './pages/Home/Home';
import {BrowserRouter as Router, Routes, Route} from 'react-router-dom';
import RegisterForm from './pages/RegisterForm/RegisterForm';
import LoginForm from './pages/LoginForm/LoginForm';
import Investor from './pages/Investor/Investor';
import Expert from './pages/Expert/Expert';
import { useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { UserManagementSliceActions } from './store/UserManagement-slice';
import AuthorizationError from './pages/auth/AuthorizationError';
import AuthenticationError from './pages/auth/AuthenticationError';
function App() {
  const dispatch = useDispatch();

  useEffect(() => { // run this at start of app mount to load user and role
    const user = JSON.parse(localStorage.getItem("user"));
    const role = localStorage.getItem("role");
    console.log("user : ");
    console.log(user);
    console.log("role : " + role);
    dispatch(UserManagementSliceActions.setCurrentUser(user));
    dispatch(UserManagementSliceActions.setRole(role));
  },[]);

  return (
    <>
       <Router>
          <Routes>
             <Route path = "/" element = {<Home />} />
             <Route path = "/register" element = {<RegisterForm />} />
             <Route path = "/login" element = {<LoginForm />} />
             <Route path = "/investor" element = {<Investor />} />
             <Route path = "/expert" element = {<Expert />} />
             <Route path = "/authenticationerror" element = {<AuthenticationError />} />
             <Route path = "/authorizationerror" element = {<AuthorizationError />} />
          </Routes>
       </Router>  
    </>
   );
}

export default App;
