import Alert from "@mui/material/Alert";
import {useCookies} from "react-cookie";
import React, {useState} from "react";
import {Navigate, useNavigate} from "react-router-dom";
// @ts-ignore
import signUpIn from "../assets/sign.png";
// @ts-ignore
import logo from "../assets/logo-black-transparent.png";
import {path} from "../App";

interface UserForgotPasswordInputModel {
    email: string
}

export default function ForgotPassword() {
    const [values, setValues] = useState<UserForgotPasswordInputModel>({ email: "" })
    const [cookies] = useCookies(["token"])
    const [submitted, setSubmitted] = useState(false)
    const [valid, setValid] = useState(false)
    const [error, setError] = useState("")
    const [open, setOpen] = useState(false)
    const navigate = useNavigate()

    const handleInputChange = (event: { preventDefault: () => void; target: { name: any; value: any; }; }) => {
        event.preventDefault()
        const { name, value } = event.target;
        setValues((values) => ({
            ...values,
            [name]: value
        }))
    }

    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        fetch(`${path}/forget-password`, {
            method: "POST",
            headers: {
                "Content-type": "application/json",
            },
            body: values.email
        }).then(res => {
            setSubmitted(true)
            if (res.status == 201) {
                setValid(true)
                setOpen(true)
                return res.json()
            }
            else {
                setValid(false)
                setError("Invalid email")
                return res.json()
            }
        }).catch(error => {
            setError(error.message)
        })
    };

    const handleCancel = () => {
        navigate(-1)
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
                            <button className="form-field" type="submit" >
                                Recuperar password
                            </button>
                            <button className={"form-field"} onClick={handleCancel}>
                                Cancelar
                            </button>
                            {submitted && !valid && <Alert severity="error" sx={{m: 1}}>{error}</Alert>}
                        </>
                    )}
                </form>
            </div>
        </div>
    );
}