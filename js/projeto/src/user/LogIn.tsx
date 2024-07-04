import React, { useState } from "react";
import { Navigate } from "react-router-dom";
import { useCookies } from "react-cookie"
// @ts-ignore
import {useSetAvatar, useSetUser} from "../context/Authn.tsx";
import Button from '@mui/material/Button';
import Snackbar from '@mui/material/Snackbar';
import Alert from "@mui/material/Alert";
// @ts-ignore
import logo from '../assets/logo-black-transparent.png';

export default function LogIn() {
  const [cookies, setCookies] = useCookies(["token"])
  const [values, setValues] = useState({
    email: "",
    password: ""
  });

  const handleInputChange = (event: { preventDefault: () => void; target: { name: any; value: any; }; }) => {
    /* event.persist(); NO LONGER USED IN v.17*/
    event.preventDefault();

    const { name, value } = event.target;
    setValues((values) => ({
      ...values,
      [name]: value
    }));
  };

  const [submitted, setSubmitted] = useState(false)
  const [valid, setValid] = useState(false)
  const [error, setError] = useState(undefined)
  const setUser = useSetUser()
  const setAvatar = useSetAvatar()
  const [open, setOpen] = React.useState(false)

  const handleSubmit = (e: { preventDefault: () => void; }) => {
    e.preventDefault();

    fetch("/api/users/signin", {
        method: "POST",
        headers: {
            'Content-type': 'application/json'
        },
        body: JSON.stringify(values)
    }).then(res => {
        setSubmitted(true)
        if (res.status == 201) return res.json()
        else {
            setValid(false)
            return { error: "Invalid email or password" }
        }
    }).then(body => {
        if (body.error) setError(body.error)
        else {
            setValid(true)
            setCookies("token", body.token, { path: '/' })
            setUser(body.token)
            setAvatar(body.foto)
            sessionStorage.setItem("token", body.token)
            sessionStorage.setItem("avatar", body.foto)
        }
    }).catch(error => {
        setError(error.message)
    })
  };
    const handleClick = () => {
        setOpen(true);
    };

    const handleClose = (event: any, reason: string) => {
        if (reason === 'clickaway') {
            return;
        }
        setOpen(false);
    }

  return (
      <div className="form-container">
          <img
              src={logo}
              alt="Registo de acessos"
              style={{height: '2.5rem', width: 'auto'}}
          />
          <form className="register-form" onSubmit={handleSubmit}>

              {submitted && valid && (
                  <Navigate to={"/success"} replace={true}/>
              )}

              {!valid && (
                  <input
                      className="form-field"
                      type="email"
                      placeholder="E-mail"
                      name="email"
                      value={values.email}
                      onChange={handleInputChange}
                  />
              )}

              {submitted && !values.email && (
                  <span id="last-name-error">Please enter an email</span>
              )}

              {!valid && (
                  <input
                      className="form-field"
                      type="password"
                      placeholder="Password"
                      name="password"
                      value={values.password}
                      onChange={handleInputChange}
                  />
              )}

              {submitted && !values.email && (
                  <span id="email-error">Please enter a password</span>
              )}
              {!valid && (
                  <button className="form-field" type="submit" onClick={handleClick}>
                      Log In
                  </button>
              )}
              <p style={{color: 'black'}}>Esqueceu-se da password? <a>Clique aqui.</a></p>
                  <Snackbar
                  open={open}
                  autoHideDuration={5000}
                  onClose={handleClose}
                  message={error}
                  anchorOrigin={{vertical: 'bottom', horizontal: 'center'}}
              />
              {submitted && !valid && <Alert severity="error" sx={{m: 1}}>{error}</Alert>}
          </form>
      </div>
  );
}
/*
<Snackbar
    open={open}
    autoHideDuration={5000}
    onClose={handleClose}
    message={error}
    anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
/>

 */