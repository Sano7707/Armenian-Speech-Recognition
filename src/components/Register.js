import React, { useState } from 'react';
import axios from 'axios';
import '../App.css';

function Register() {
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [registrationSuccess, setRegistrationSuccess] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  const handleRegistration = async () => {
    try {
      const userData = {
        firstName,
        lastName,
        email,
        password
      };

      const response = await axios.post('https://wealthy-wired-kodiak.ngrok-free.app/register', userData);

      if (response.status === 201) {
        console.log('User registered:', response.data);
        setRegistrationSuccess(true);
      }
    } catch (error) {
      console.error('Registration error:', error);
      if (error.response && error.response.status === 500) {
        setRegistrationSuccess(false);
        setErrorMessage('Email already registered.');
      }
    }
  };

  if (registrationSuccess) {
    return (
      <div className='centered-container'>
        <div className="login-container">
          <h1>Registration is successful!</h1>
          <p>Verification link is sent to your account</p>
          <div className="registration-link">
            <p>To access your account <a href="/">Login here</a></p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="centered-container">
      <div className="login-container">
        <h2>Registration</h2>
        <div className="input-container">
          <input type="text" placeholder="First Name" value={firstName} onChange={(e) => setFirstName(e.target.value)} />
        </div>
        <div className="input-container">
          <input type="text" placeholder="Last Name" value={lastName} onChange={(e) => setLastName(e.target.value)} />
        </div>
        <div className="input-container">
          <input type="email" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
        </div>
        <div className="input-container">
          <input type="password" placeholder="Password" value={password} onChange={(e) => setPassword(e.target.value)} />
        </div>
        <button className="login-button" onClick={handleRegistration}>Register</button>
        {errorMessage && <p className="error-message">{errorMessage}</p>}
        <div className="login-link">
          <p className='registration-link'>Already have an account? <a href="/login">Login here</a></p>
        </div>
      </div>
    </div>
  );
}

export default Register;
