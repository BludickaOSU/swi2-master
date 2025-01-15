import { useState } from 'react';
import './App.css';
import { Route, Routes } from 'react-router-dom';
import Signup from './components/Signup';
import Home from './components/Home';

function App() {

  const [user, setUser] = useState(getUserToken());

  function setUserToken(userToken: any) {
    localStorage.setItem('userToken', JSON.stringify(userToken));
    setUser(userToken);
  }

  function getUserToken() {
    let userToken = JSON.parse(localStorage.getItem('userToken') || '""');
    return userToken;
  }

  return (
    <div className="App">
      <Routes>
        <Route path='/' element={<Home user={user} setUserToken={setUserToken} />} />
        <Route path='/signup' element={<Signup />} />
      </Routes>
    </div>
  );
}

export default App;
