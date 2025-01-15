import { Box, Button, Chip, Divider, TextField } from '@mui/material';
import axios from 'axios';
import React, { useState } from 'react'
import { Link } from 'react-router-dom';
import '../css/DefaultColors.css';

const LOGIN_TOKEN_URL = "http://localhost:8080/login";

const Login = (props: any) => {

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  function login(e: any) {
    e.preventDefault();

    const userCredentials = {
      username: username,
      password: password
    };

    axios.post(LOGIN_TOKEN_URL, userCredentials)
      .then(response => {
        props.setUserToken(response.data);
      })
      .catch(error => {
        try {
          console.log(error.response.data);
          //setLoginError(error.response.data);
        } catch (e) {
          //setLoginError("Cannot access authentication server!");
        }
      });
  }

  return (
      <div className="background-gradient">
        <div className="center">
        <h1>Please log in</h1>
        </div>
        <div className="margin-all center white-box">
          <form onSubmit={login}>
            <div className="margin-all">
              <Box>
                <TextField
                    placeholder='Username'
                    onChange={(e) => setUsername(e.target.value)}
                    sx={{ input: { color: '#298DE3' } }}
                />
              </Box>
            </div>
            <div className="margin-all">
              <Box>
                <TextField
                    placeholder='Password'
                    type='password'
                    onChange={(e) => setPassword(e.target.value)}
                    sx={{ input: { color: '#298DE3' } }}
                />
              </Box>
            </div>
            <div className="center margin-all">
              <Box>
                <Button
                    variant='contained'
                    type='submit'
                >Log in</Button>
              </Box>
            </div>
          </form>
        </div>
        <div className="margin-all">
        <Divider>
          <Chip label="OR"/>
        </Divider>
        </div>
        <div className="center margin-all">
        <Link to="/signup">
          <Button>
            Sign up now
          </Button>
        </Link>
        </div>
      </div>
  )
}

export default Login