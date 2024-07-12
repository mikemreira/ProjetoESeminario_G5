import React, { useState } from "react";
import { Navigate } from "react-router-dom";
import Snackbar from "@mui/material/Snackbar";
import Alert from "@mui/material/Alert";
// @ts-ignore
import logo from "../assets/logo-black-transparent.png";
// @ts-ignore
import signUpIn from "../assets/sign.png";

export default function SignUp() {
  const [values, setValues] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: ""
  });
    const [open, setOpen] = React.useState(false);

    const handleClick = () => {
        setOpen(true);
    };


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
  const [error, setError] = useState("")

  const handleSubmit = (e: { preventDefault: () => void; }) => {
    e.preventDefault();
    if (values.password !== values.confirmPassword) {
        setError("Passwords não coincidem");
        setValid(false);
        setSubmitted(true);
        return;
    }
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
      <div className="login-container">
          <div className="sign-image">
              <img src={signUpIn} alt="Image" />
          </div>
          <div className="login-form">
              <div className="login-image">
                  <img src={logo} alt="Logo"/>
              </div>
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
                      <>
                          <input
                              className="form-field"
                              type="password"
                              placeholder="Password"
                              name="password"
                              value={values.password}
                              onChange={handleInputChange}
                          />
                          <input
                              className="form-field"
                              type="password"
                              placeholder="Confirmar password"
                              name="confirmPassword"
                              value={values.confirmPassword}
                              onChange={handleInputChange}
                          />
                      </>
                  )}
                  {submitted && !values.password && (
                      <span id="email-error">Please enter a password</span>
                  )}
                  {values.password !== values.confirmPassword && (
                      <span id="confirm-password-error">As passwords não coincidem</span>
                  )}
                  {!valid && (
                      <button className="form-field" type="submit" onClick={handleClick}
                              disabled={!values.password || values.password !== values.confirmPassword}>
                          Registar
                      </button>
                  )}
                  {submitted && !valid && <Alert severity="error" sx={{m: 1}}>{error}</Alert>}
              </form>
              <form className="info-pass-form">
                  <p>A password deve ter pelo menos:</p>
                  <ul>
                      <li>5 caracteres;</li>
                      <li>uma letra maiúscula;</li>
                      <li>uma letra minúscula;</li>
                      <li>um número.</li>
                  </ul>
              </form>
          </div>
      </div>
  );
}