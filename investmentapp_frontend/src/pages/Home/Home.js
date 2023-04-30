import Header from "../../components/Header/Header";
import Footer from "../../components/Footer/Footer";
import './Home.css';
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

const Home = () => {
   const navigate = useNavigate();
   
   useEffect(() => {
    // console.log(localStorage.clear("user"))
      const user = JSON.parse(localStorage.getItem("user"));
      const role = localStorage.getItem("role");
      console.log("user : ");
      console.log(user);
      console.log("role : " + role);
      if (user) {
        if (role === 'investor') {
          navigate('/investor');
        } else if (role === 'expert') {
          navigate('/expert');
        }
      }
    }, [navigate]);

   return (
      <>
      <Header/>
      <div className="overlay-container">
        <div className="overlay">
          <div className="overlay-text">
            <p>The way to become rich is to put all your eggs in one basket and watch that basket - Andrew Carnegie</p>
          </div>
        </div>
      </div>
      <Footer />
      </>
   );
}
 
export default Home;