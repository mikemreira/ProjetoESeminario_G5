import React, { useState } from "react";
import {Navigate, useNavigate} from "react-router-dom";
import { useCookies } from "react-cookie"
import Button from '@mui/material/Button';
import Snackbar from '@mui/material/Snackbar';
import Alert from "@mui/material/Alert";
// @ts-ignore
import logo from '../assets/logo-black-transparent.png';
import {Stack, Typography} from "@mui/material";
// @ts-ignore
import signUpIn from "../assets/sign.png";

interface UserEditPasswordInputModel {
    password: string
    confirmPassword: string
}

export default function ChangePassword() {
    const [values, setValues] = useState<UserEditPasswordInputModel>({password: "", confirmPassword: ""})
    const [cookies] = useCookies(["token"])
    const [submitted, setSubmitted] = useState(false)
    const [valid, setValid] = useState(false)
    const [error, setError] = useState("")
    const [open, setOpen] = React.useState(false)
    const navigate = useNavigate()

    const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        event.preventDefault()
        const { name, value } = event.target;
        setValues((values) => ({
            ...values,
            [name]: value
        }))
    }

    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (values.password !== values.confirmPassword) {
            setError("Passwords não coincidem");
            setValid(false);
            setSubmitted(true);
            return;
        }
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
                setError("A password deve ter pelo menos 5 caracteres, uma letra maiúscula, uma letra minúscula e um número.")
                return res.json()
            }
        }).catch(error => {
            setError(error.message)
        })
    };

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
                            <input
                                className="form-field"
                                type="password"
                                placeholder="Confirmar a nova password"
                                name="confirmPassword"
                                value={values.confirmPassword}
                                onChange={handleInputChange}
                            />
                            {submitted && !values.password && (
                                <span id="last-name-error">Introduza uma nova password</span>
                            )}
                            {values.password !== values.confirmPassword && (
                                <span id="confirm-password-error">As passwords não coincidem</span>
                            )}
                            <button className="form-field" type="submit" disabled={!values.password || values.password !== values.confirmPassword}>
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
