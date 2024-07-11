import {Navigate, useNavigate, useParams} from "react-router-dom";
import React, {useEffect, useState} from "react";
import {useCookies} from "react-cookie";
import signUpIn from "../assets/sign.png";
import logo from "../assets/logo-black-transparent.png";
import Alert from "@mui/material/Alert";

interface UserResetPasswordInputModel {
    password: string
    confirmPassword: string
}

export default function ResetPassword() {
    const [values, setValues] = useState<UserResetPasswordInputModel>({ password: "", confirmPassword: "" })
    const [submitted, setSubmitted] = useState(false)
    const [valid, setValid] = useState(false)
    const [error, setError] = useState("")
    const [open, setOpen] = useState(false)
    const navigate = useNavigate()

    // Extract email and token from query parameters
    const queryParams = new URLSearchParams(location.search)
    const email = queryParams.get("email")
    const token = queryParams.get("token")

    useEffect(() => {
        if (!email || !token) {
            setError("Missing email or token")
        }
    }, [email, token])


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
        if (values.password !== values.confirmPassword) {
            setError("Passwords não coincidem")
            setValid(false)
            setSubmitted(true)
            return
        }
        fetch(`/api/set-password?email=${email}&token=${token}`, {
            method: "PUT",
            headers: {
                "Content-type": "application/json",
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
                setError("A password deve ter pelo menos 8 caracteres, um número e uma letra maiúscula.")
                return res.json()
            }
        }).catch(error => {
            setError(error.message)
        })
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
                                type="password"
                                placeholder="Nova Password"
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
                                <span id="last-name-error">Adicione uma password</span>
                            )}
                            {values.password !== values.confirmPassword && (
                                <span id="confirm-password-error">As passwords não coincidem</span>
                            )}
                            <button className="form-field" type="submit"
                                    disabled={!values.password || values.password !== values.confirmPassword}>
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