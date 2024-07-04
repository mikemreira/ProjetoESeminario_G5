import {
    Box, CircularProgress,
    Stack,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    IconButton,
    Button,
    Typography, Avatar,
} from '@mui/material';
import { useEffect, useState } from "react";
import { useCookies } from "react-cookie";
import * as React from "react";
import { Delete, Edit } from "@mui/icons-material";
import AddIcon from "@mui/icons-material/Add";
import { useParams, useNavigate } from "react-router-dom";
import {MRT_GlobalFilterTextField, MRT_TablePagination} from "material-react-table";

interface UserModel {
    id: number
    nome: string
    email: string
    morada: string
    func: string
    foto: string | null
}

interface UserOutputModel {
    users: UserModel[]
}


export default function ObraFuncionarios() {
    const [cookies] = useCookies(["token"]);
    const [users, setUsers] = useState<UserOutputModel>({ users: [] });
    const [loading, setLoading] = useState(true);
    const { oid } = useParams<{ oid: string }>();
    const navigate = useNavigate();

    useEffect(() => {
        fetch(`/api/obras/${oid}/users`, {
            method: "GET",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`
            },
        }).then((res) => {
            if (res.ok) return res.json()
            else return null
        }).then((body) => {
            if (body) {
               // console.log(JSON.stringify(body))
                setUsers(body)
                setLoading(false)
            }
        }).catch(error => {
            console.error("Error fetching registos: ", error)
        })
    }, [cookies.token, oid])

    const handleViewProfile = (uid: number) => {
        navigate(`/obras/${oid}/funcionarios/${uid}`)
    }

    const handleViewUserRecords = (uid: number) => {
        navigate(`/obras/${oid}/registers/${uid}`)
    }

    const handleViewAllRecords = () => {
        navigate(`/obras/${oid}/registers/all`)
    }

    const handleClickAddFuncionario = () => {
        navigate(`/obras/${oid}/funcionario/invite`)
    }

    if (loading) return <CircularProgress />
    else return (
        <Stack sx={{ m: '5rem 0' }}>
            <Box
                sx={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                }}
            >
                <Typography variant="h4" color={"black"}>Membros da Obra</Typography>
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={(e) => {
                            e.stopPropagation()
                            handleViewAllRecords()
                        }}
                        sx={{ mr: 2 }}
                    >
                        Ver todos os Registos
                    </Button>
                    <IconButton onClick={handleClickAddFuncionario} color="primary" sx={{
                        bgcolor: 'primary.main',
                        borderRadius: '40%',
                        width: '40px',
                        height: '40px',
                        '&:hover': {
                            bgcolor: 'primary.dark',
                        },
                    }} title={"Adicionar membro"}>
                        <AddIcon sx={{ fontSize: 32, color: 'white' }}/>
                    </IconButton>
                </Box>
            </Box>
            <TableContainer sx={{ backgroundColor: '#cccccc', mt: 2 }}>
                <Table sx={{ tableLayout: 'fixed' }}>
                    <TableHead>
                        <TableRow>
                            <TableCell align="center"></TableCell>
                            <TableCell align="left" sx={{ fontSize: '1.1rem' }}>Nome</TableCell>
                            <TableCell align="left" sx={{ fontSize: '1.1rem' }}>Função</TableCell>
                            <TableCell align="center" sx={{ fontSize: '1.1rem' }}>Registos</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {users.users.map((user) => (
                            <TableRow key={user.id} onClick={() => handleViewProfile(user.id)} sx={{ cursor: 'pointer' }}>
                                <TableCell align="center">
                                    <Avatar
                                        alt={user.nome}
                                        src={user.foto || "https://cdn-icons-png.flaticon.com/512/3135/3135715.png"}
                                        variant="rounded"
                                        sx={{ width: "40%", height: "40%" }}
                                    />
                                </TableCell>
                                <TableCell align="left" sx={{ fontSize: '1.1rem' }}>{user.nome}</TableCell>
                                <TableCell align="left" sx={{ fontSize: '1.1rem' }}>{user.func}</TableCell>
                                <TableCell align="center">
                                    <Button
                                        variant="contained"
                                        color="primary"
                                        onClick={(e) => {
                                            e.stopPropagation()
                                            handleViewUserRecords(user.id)
                                        }}
                                    >
                                        Ver Registos
                                    </Button>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Stack>
    );
};
