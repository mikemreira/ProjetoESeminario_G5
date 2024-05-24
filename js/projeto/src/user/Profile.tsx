import {useCookies} from "react-cookie";
import {useEffect, useState} from "react";
import * as React from "react";
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';

interface UserModel {
    id: number
    nome: string
    email: string
    morada: string
}


export default function Profile() {
    const [cookies] = useCookies(["token"])
    const [user, setUser] = useState<UserModel>()

    useEffect(() => {
        fetch("/api/users/me", {
            method: "GET",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`,
            },
        })
            .then((res) => {
                if (res.ok) {
                    return res.json()
                } else {
                    return null
                }
            })
            .then((body) => {
                if (body) {
                    setUser(body)
                    console.log(body)
                }
            })
            .catch((error) => {
                console.error("Error fetching obras:", error)
            })
    }, [cookies.token])


    return (
        <div>
            <h1>Perfil</h1>
            <TableContainer component={Paper}>
                <Table sx={{ minWidth: 650 }} aria-label="user profile table">
                    <TableBody>
                        <TableRow>
                            <TableCell>ID</TableCell>
                            <TableCell>{user?.id}</TableCell>
                        </TableRow>
                        <TableRow>
                            <TableCell>Nome</TableCell>
                            <TableCell>{user?.nome}</TableCell>
                        </TableRow>
                        <TableRow>
                            <TableCell>Email</TableCell>
                            <TableCell>{user?.email}</TableCell>
                        </TableRow>
                        <TableRow>
                            <TableCell>Morada</TableCell>
                            <TableCell>{user?.morada}</TableCell>
                        </TableRow>
                    </TableBody>
                </Table>
            </TableContainer>
        </div>
    )
}