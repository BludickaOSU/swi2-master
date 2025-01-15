import { Box, Button, Chip, Divider, TextField, Typography } from '@mui/material';
import axios from 'axios';
import React, { useState } from 'react'
import {Link, useNavigate} from 'react-router-dom';

const SIGNUP_TOKEN_URL = "http://localhost:8080/signup";

const Signup = () => {

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const navigate = useNavigate();

  function signup(e: any) {
    e.preventDefault();
    
    const signupBody = {
      username: username,
      password: password
    }

    axios.post(SIGNUP_TOKEN_URL, signupBody)
        .then(response => {
            setSuccessMessage("Signup successful! Redirecting to login...");
            setTimeout(() => {
                navigate("/");
            }, 2000);
        })
      .catch(error => {
        try {

        } catch (e) {

        }
      });
  }

  return (
      <div className="background-gradient">
          {successMessage && (
              <div className="success-message">
              <Typography variant="body1" color="success.main" className="margin-all">
                  {successMessage}
              </Typography>
              </div>
          )}
        <div className="center">
        <h1>Sign up</h1>
        </div>
        <div className="margin-all center white-box">
          <form onSubmit={signup}>
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
            <div className="margin-all center">
              <Box>
                <Button
                    variant='contained'
                    type='submit'
                >Sign up</Button>
              </Box>
            </div>
          </form>
        </div>
        <div className="margin-all">
        <Divider>
          <Chip label="OR"/>
        </Divider>
        </div>
        <div className="margin-all center">
        <Link to="/">
          <Button>
            Log in
          </Button>
        </Link>
        </div>
      </div>
  )
}

export default Signup