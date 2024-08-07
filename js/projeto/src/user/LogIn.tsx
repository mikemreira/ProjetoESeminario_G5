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
import {CircularProgress} from "@mui/material";
import Box from "@mui/material/Box";
import {handleInputChange} from "../Utils";


export default function LogIn() {
  const [cookies, setCookies] = useCookies(["token"])
  const [values, setValues] = useState({
    email: "",
    password: ""
  });
  const [loading, setLoading] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const [valid, setValid] = useState(false);
  const [error, setError] = useState(undefined);
  const setUser = useSetUser();
  const setAvatar = useSetAvatar();

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
            setLoading(true)
            return res.json()
        }
        else {
            setLoading(false)
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
            localStorage.setItem("token", body.token)
            fetch(`${path}/${body.hrefIcon}`, {
                method: "GET",
                headers: {
                    'Content-type': 'application/json',
                    'Authorization': `Bearer ${body.token}`
                }
            }).then(res => res.json())
                .then(fotoBody => {
                    setAvatar(fotoBody.foto)
                    localStorage.setItem("avatar", fotoBody.foto)
                    setLoading(false)
                }).catch(error => {
                setError(error.message)
                setLoading(false)
            })
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
                                onChange={handleInputChange(setValues)}
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
                                onChange={handleInputChange(setValues)}
                            />
                            {submitted && !values.password && (
                                <span id="password-error">Please enter a password</span>
                            )}
                            <button className="form-field" type="submit" >
                                Log In
                            </button>
                            <p style={{color: 'black'}}>Esqueceu-se da password? <a href={"/forgotPassword"}>Clique aqui.</a></p>
                            {loading && (
                                <Box
                                    sx={{
                                        display: 'flex',
                                        justifyContent: 'center',
                                        alignItems: 'center',
                                    }}
                                >
                                    <CircularProgress />
                                </Box>
                            )}
                            {!loading && submitted && !valid && <Alert severity="error" sx={{m: 1}}>{error}</Alert>}
                        </>
                    )}
                </form>
            </div>
        </div>
    );
}