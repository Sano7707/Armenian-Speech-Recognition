import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function LoginPage({ onLogin }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();
  const [errorMessage, setErrorMessage] = useState('');


  const handleLogin = async () => {
    try {
      const url = `https://wealthy-wired-kodiak.ngrok-free.app/login?email=${email}&password=${password}`;
      const response = await axios.get(url);
      const userData = response.data;
      // Save user ID in session storage
      sessionStorage.setItem('userId', userData);
      
      // Update login status
      onLogin();
      // Navigate to home page
      navigate('/');
    } catch (error) {
      console.error('Login error:', error);
      if (error.response && error.response.data) {
        setErrorMessage(error.response.data);
      } else {
        setErrorMessage('An error occurred during login. Please try again later.');
      }
    }
  };
  

  return (
    <div className='login-container'>
      <div className="login-form">
        <h2>Sign in to your account</h2>
        <div className="input-container">
          <input type="email" placeholder="Enter email" value={email} onChange={(e) => setEmail(e.target.value)} />
        </div>
        <div className="input-container">
          <input type="password" placeholder="Enter password" value={password} onChange={(e) => setPassword(e.target.value)} />
        </div>
        <button className="login-button" type="button" onClick={handleLogin}>Login</button>
        <div className="login-link">
          <p className='registration-link'>Not registered yet? <a href="/register">Register here</a></p>
        </div>
        {errorMessage && <div className="error-message">{errorMessage}</div>}
      </div>
    </div>
  );
}

export default LoginPage;
