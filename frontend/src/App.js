import './App.css';
import Auth from './components/Auth/Auth';
import Store from './components/Store/Store';

import {BrowserRouter, Route, Routes} from 'react-router-dom';
import React, {useEffect} from "react";
import {verifyLogin} from "./components/Auth/AuthUtils";

export const url = "http://localhost:8080"

function setToken(userToken) {
    sessionStorage.setItem('token', JSON.stringify(userToken));
}

function getToken() {
    const tokenString = sessionStorage.getItem('token');
    return tokenString ? JSON.parse(tokenString) : null;
}

function setUserId(userId) {
    sessionStorage.setItem('userId', JSON.stringify(userId));
}

function getUserId() {
    const tokenString = sessionStorage.getItem('userId');
    return tokenString ? JSON.parse(tokenString) : null;
}

export default function App() {
    verifyLogin();

    useEffect(() => {
        const interval = setInterval(() => {
            verifyLogin(); //Verify once every 5min
        }, 5 * 60 * 1000);
        return () => clearInterval(interval);
    }, []);

    const token = getToken();
    const userId = getUserId();

    if(!token){
        return <Auth setToken={setToken}  setUserId={setUserId}/>
    }

  return (
      <div className="wrapper">
          <BrowserRouter>
              <Routes>
                  <Route path="" element={<Store token={token} userId={userId}/>} />
              </Routes>
          </BrowserRouter>
      </div>
  );
}