import { useNavigate } from "react-router-dom"
import { useEffect } from "react";
import {useSetAvatar, useSetUser} from "../context/Authn";
import {path} from "../App";

export default function LogOut() {
    const navigate = useNavigate()
    const setUser = useSetUser()
    const setAvatar = useSetAvatar()

    useEffect(() => {
        fetch(`${path}/users/signout`, {
            method: "POST",
            headers: {
                'Content-type': 'application/json'
            },
        }).then(res => {
            const expirationDate = new Date();
            setUser(undefined)
            setAvatar(undefined)
            localStorage.clear()
            expirationDate.setHours(expirationDate.getHours() - 1);
            document.cookie = `token=; expires=${expirationDate.toUTCString()}; path=/`;
            document.cookie = `username=; expires=${expirationDate.toUTCString()}; path=/`;
            navigate("/login")
        })
    }, [])

    return null

}