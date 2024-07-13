import React, { useState } from "react";
import { Navigate } from "react-router-dom";
import { useCookies } from "react-cookie"
// @ts-ignore
import {useSetAvatar, useSetUser} from "../context/Authn.tsx";
import Alert from "@mui/material/Alert";
// @ts-ignore
import logo from '../assets/logo-black-transparent.png';
import '../Login.css';
// @ts-ignore
import signUpIn from '../assets/sign.png';
import {path} from "../App";


export default function LogIn() {
  const [cookies, setCookies] = useCookies(["token"])
  const [values, setValues] = useState({
    email: "",
    password: ""
  });

  const handleInputChange = (event: { preventDefault: () => void; target: { name: any; value: any; }; }) => {
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

  const handleSubmit = (e: { preventDefault: () => void; }) => {
    e.preventDefault();

    fetch(`${path}/users/signin`, {
        method: "POST",
        headers: {
            'Content-type': 'application/json'
        },
        body: JSON.stringify(values)
    }).then(res => {
        setSubmitted(true)
        if (res.status == 201) {
            return res.json()
        }
        else {
            setValid(false)
            return { error: "Invalid email or password" }
        }
    }).then(body => {
        if (body.error) {
            setError(body.error)
        }
        else {
            setValid(true)
            setCookies("token", body.token, { path: '/' })
            setUser(body.token)
            setAvatar(body.foto)
            localStorage.setItem("token", body.token)
            localStorage.setItem("avatar", body.foto)
        }
    }).catch(error => {
        setError(error.message)
    })
  }

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
                        <Navigate to={"/"} replace={true}/>
                    )}
                    {!valid && (
                        <>
                            <input
                                className="form-field"
                                type="email"
                                placeholder="E-mail"
                                name="email"
                                value={values.email}
                                onChange={handleInputChange}
                            />
                            {submitted && !values.email && (
                                <span id="last-name-error">Please enter an email</span>
                            )}
                            <input
                                className="form-field"
                                type="password"
                                placeholder="Password"
                                name="password"
                                value={values.password}
                                onChange={handleInputChange}
                            />
                            {submitted && !values.password && (
                                <span id="password-error">Please enter a password</span>
                            )}
                            <button className="form-field" type="submit" >
                                Log In
                            </button>
                            <p style={{color: 'black'}}>Esqueceu-se da password? <a href={"/forgotPassword"}>Clique aqui.</a></p>
                            {submitted && !valid && <Alert severity="error" sx={{m: 1}}>{error}</Alert>}
                        </>
                    )}
                </form>
            </div>
        </div>
    );
}