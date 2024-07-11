import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import {Snackbar} from "@mui/material";
import Alert from "@mui/material/Alert";
import {useCookies} from "react-cookie";
import React, {useState} from "react";
import {Navigate, useNavigate} from "react-router-dom";
import signUpIn from "../assets/sign.png";
import logo from "../assets/logo-black-transparent.png";

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
        console.log("values: " + values.email)
        fetch("/api/forget-password", {
            method: "POST",
            headers: {
                "Content-type": "application/json",
            },
            body: values.email
        }).then(res => {
            setSubmitted(true)
            console.log(res)
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

    const handleClose = (event: any, reason: string) => {
        if (reason === 'clickaway') {
            return;
        }
        setOpen(false);
    }

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
/*
    return (
        <div>
            <form onSubmit={handleSubmit}>
                <TextField
                    variant="outlined"
                    margin="normal"
                    required
                    fullWidth
                    name="email"
                    label="email"
                    type="email"
                    id="email"
                    onChange={handleInputChange}
                />
                <Button
                    type="submit"
                    fullWidth
                    variant="contained"
                    color="primary"
                >
                    Enviar para email
                </Button>
            </form>

            <Button
                fullWidth
                variant="contained"
                color="secondary"
                onClick={handleCancel}
            >
                Cancelar
            </Button>
        </div>
    )

 */
}