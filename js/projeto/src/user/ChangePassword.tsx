import React, { useState } from "react";
import {Navigate, useNavigate} from "react-router-dom";
import { useCookies } from "react-cookie"
import Button from '@mui/material/Button';
import Snackbar from '@mui/material/Snackbar';
import Alert from "@mui/material/Alert";
// @ts-ignore
import logo from '../assets/logo-black-transparent.png';

export default function ChangePassword() {
    const [values, setValues] = useState({password: "" })
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
        fetch("/api/users/me/password", {
            method: "PUT",
            headers: {
                'Content-type': 'application/json',
                "Authorization": `Bearer ${cookies.token}`,
            },
            body: JSON.stringify(values)
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
        }).then(body => {
            if (body.error) setError(body.error)
            else {
                setValid(true)
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

    const handleCancel = () => {
        navigate(-1)
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
                    <Navigate to={"/profile"} replace={true}/>
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

                {submitted && !values.password && (
                    <span id="password-error">Please enter a password</span>
                )}
                {!valid && (
                    <Button className="form-field" type="submit">
                        Confirmar
                    </Button>
                )}
                <Button onClick={handleCancel}>
                    Cancelar
                </Button>
                {submitted && !valid && <Alert severity="error" sx={{m: 1}}>{error}</Alert>}
            </form>
            <Snackbar open={open} autoHideDuration={6000} onClose={handleClose}>
                <Alert onClose={handleClose} severity="success" sx={{ width: '100%' }}>
                    Password changed successfully!
                </Alert>
            </Snackbar>
        </div>
    );
}
