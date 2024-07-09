import { useNavigate } from "react-router-dom"
import { useEffect } from "react";
import {useSetAvatar, useSetUser} from "../context/Authn";

export default function LogOut() {
    const navigate = useNavigate()
    const setUser = useSetUser()
    const setAvatar = useSetAvatar()

    useEffect(() => {
        fetch("/api/users/signout", {
            method: "POST",
            headers: {
                'Content-type': 'application/json'
            },
        }).then(res => {
            const expirationDate = new Date();
            setUser(undefined)
            setAvatar(undefined)
            sessionStorage.clear()
            expirationDate.setHours(expirationDate.getHours() - 1);
            document.cookie = `token=; expires=${expirationDate.toUTCString()}; path=/`;
            document.cookie = `username=; expires=${expirationDate.toUTCString()}; path=/`;
            navigate("/login")
        })
    }, [])

    return null

}