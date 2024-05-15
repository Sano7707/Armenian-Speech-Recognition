import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './components/pages/Home';
import Services from './components/pages/Services';
import Products from './components/pages/Products';
import Registration from './components/pages/Registration';
import LoginPage from './components/pages/LoginPage';
import axios from 'axios';

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(sessionStorage.getItem('userId') !== null);
  const [userName, setUserName] = useState("");
  axios.defaults.headers.common['ngrok-skip-browser-warning'] = 'true';


  const fetchUserName = async () => {
    try {
      const userId = sessionStorage.getItem('userId');
      if (userId) {
        const response = await axios.get(`https://armenianspeech.info/users/${userId}`);
        const userData = response.data;
        setUserName(userData);
      }
    } catch (error) {
      console.error('Error fetching user data:', error);
    }
  };

  useEffect(() => {
    if (isLoggedIn) {
      fetchUserName();
    }
  }, [isLoggedIn]);

  return (
    <Router>
      <Navbar isLoggedIn={isLoggedIn} userName={userName} />
      <Routes>
        <Route path='/' element={<Home />} />
        <Route path='/services' element={<Services />} />
        <Route path='/products' element={<Products />} />
        <Route path='/register' element={<Registration />} />
        <Route
          path='/login'
          element={<LoginPage onLogin={() => setIsLoggedIn(true)} />}
        />
      </Routes>
    </Router>
  );
}

export default App;
