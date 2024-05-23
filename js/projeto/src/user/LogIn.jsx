import React, { useState } from "react";
import { Navigate } from "react-router-dom";
import { useCookies } from "react-cookie"
import {useSetUser} from "../context/Authn.tsx";

export default function LogIn() {
  const [cookies, setCookies] = useCookies(["token"])
  const [values, setValues] = useState({
    email: "",
    password: ""
  });

  const handleInputChange = (event) => {
    /* event.persist(); NO LONGER USED IN v.17*/
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
  const setUser = useSetUser()

  const handleSubmit = (e) => {
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
            sessionStorage.setItem("token", body.token)
        }
    }).catch(error => {
        setError(error.message)
    })
  };

  return (
    <div className="form-container">
      <form className="register-form" onSubmit={handleSubmit}>
        {submitted && valid && (
            <Navigate to={"/success"} replace={true}/>
        )}

        {!valid && (
          <input
            class="form-field"
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
            class="form-field"
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
          <button class="form-field" type="submit">
            Log In
          </button>
        )}
          <div className="error">{error}</div>
      </form>
    </div>
  );
}