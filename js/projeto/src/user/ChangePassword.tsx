import React, { useState } from "react";
import {Navigate, useNavigate} from "react-router-dom";
import { useCookies } from "react-cookie"
import Button from '@mui/material/Button';
import Snackbar from '@mui/material/Snackbar';
import Alert from "@mui/material/Alert";
// @ts-ignore
import logo from '../assets/logo-black-transparent.png';
import {Stack, Typography} from "@mui/material";
import signUpIn from "../assets/sign.png";

interface UserEditPasswordInputModel {
    password: string
}

export default function ChangePassword() {
    const [values, setValues] = useState<UserEditPasswordInputModel>({password: null })
    const [cookies] = useCookies(["token"])
    const [submitted, setSubmitted] = useState(false)
    const [valid, setValid] = useState(false)
    const [error, setError] = useState("")
    const [open, setOpen] = React.useState(false)
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
        console.log("values: " + values.password)
        fetch("/api/users/me/changepassword", {
            method: "PUT",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`,
            },
            body: values.password
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
                setError("Invalid password")
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

    if (!cookies.token) {
        return (
            <Stack sx={{ m: '5rem 0', alignItems: 'center' }}>
                <Typography variant="h4" color="error">Erro de autenticação</Typography>
                <Typography variant="body1" color="error">Precisa de estar autenticado para acessar a esta página.</Typography>
                <Button variant="contained" color="primary" onClick={() => navigate("/login")}>
                    Login
                </Button>
            </Stack>
        )
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
                                type="password"
                                placeholder="Nova password"
                                name="password"
                                value={values.password}
                                onChange={handleInputChange}
                            />
                            {submitted && !values.password && (
                                <span id="last-name-error">Introduza uma nova password</span>
                            )}
                            <button className="form-field" type="submit" >
                                Alterar password
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
