import React, { useState } from "react";
import { Navigate } from "react-router-dom";
import Snackbar from "@mui/material/Snackbar";
import Alert from "@mui/material/Alert";
// @ts-ignore
import logo from "../assets/logo-black-transparent.png";

export default function SignUp() {
  const [values, setValues] = useState({
    name: "",
    email: "",
    password: ""
  });
    const [open, setOpen] = React.useState(false);

    const handleClick = () => {
        setOpen(true);
    };

    const handleClose = (event: any, reason: string) => {
        if (reason === 'clickaway') {
            return;
        }
        setOpen(false);
    }

  const handleInputChange = (event: { preventDefault: () => void; target: { name: any; value: any; }; }) => {
    event.preventDefault();

    const { name, value } = event.target;
    setValues((values) => ({
      ...values,
      [name]: value
    }));
  };

  const [submitted, setSubmitted] = useState(false);
  const [valid, setValid] = useState(false);
  const [error, setError] = useState(undefined)

  const handleSubmit = (e: { preventDefault: () => void; }) => {
    e.preventDefault();

    fetch("/api/users/signup", {
        method: "POST",
        headers: {
            'Content-type': 'application/json'
        },
        body: JSON.stringify(values)
    }).then(res => {
        setSubmitted(true)
        if (res.status == 201) {
            setValid(true)
        } else setValid(false)
        return res.json()
    }).then(body => {
        if (!valid){
            setError(body.error)
            console.log(body.error)
        }

    }).catch(error => {
        setError(error.message)
    })
  };

  return (
      <div className="form-container">
          <img
              src={logo}
              alt="Registo de acessos"
              style={{height: '2.5rem', width: 'auto'}}
          />
          <form className="register-form" onSubmit={handleSubmit}>
              {submitted && valid && (
                  <Navigate to={"/login"} replace={true}/>
              )}
              {!valid && (
                  <input
                      className="form-field"
                      type="text"
                      placeholder="Username"
                      name="name"
                      value={values.name}
                      onChange={handleInputChange}
                  />
              )}

              {submitted && !values.name && (
                  <span id="first-name-error">Please enter a username</span>
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
                      Registar
                  </button>
              )}
              {submitted && !valid && <Alert severity="error" sx={{m: 1}}>{error}</Alert>}
          </form>
      </div>
  );
}