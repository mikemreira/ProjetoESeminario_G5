import {
    Avatar,
    Box, CircularProgress,
    IconButton,
    Stack,
    Table, TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Typography
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import * as React from "react";
import { UserOutputModel } from '../ObrasInfo';
import TapAndPlayIcon from '@mui/icons-material/TapAndPlay';
import PersonRemoveIcon from '@mui/icons-material/PersonRemove';
import {useCookies} from "react-cookie";
import {useEffect, useState} from "react";
import {UserModel} from "../../user/Profile";
import {path} from "../../App";


interface ObraFuncionariosFormProps {
    handleClickAddFuncionario: () => void;
    handleViewProfile: (id: number) => void;
    handleViewUserRecords: (pageNumber: number, id: number) => void;
    users: UserOutputModel;
    handleRemoveUser: (id: number) => void;
}

export default function ObraFuncionariosForm({
    handleClickAddFuncionario,
    handleViewProfile,
    handleViewUserRecords,
    users,
    handleRemoveUser
}: ObraFuncionariosFormProps) {
    const [cookies] = useCookies(['token']);
    const [currUser, setCurrUser] = useState<UserModel | undefined>(undefined)

    useEffect(() => {
        fetch(`${path}/users/me`, {
            method: "GET",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`,
            },
        }).then((res) => {
            if (res.ok) {
                return res.json();
            } else if (res.status === 404) {
                console.log("404")
            }
        }).then((body) => {
            setCurrUser(body)
            console.log(body);
        }).catch((error) => {
            console.error("Error fetching: ", error);
        })
    }, [cookies.token]);

    if (currUser === undefined) {
        return <div><CircularProgress /></div>;
    }

    return (
        <Stack sx={{ m: '2rem 0' }}>
            <Box
                sx={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                }}
            >
                <Typography variant="h4" color="black">Membros</Typography>
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                    <IconButton
                        onClick={handleClickAddFuncionario}
                        color="primary"
                        sx={{
                            bgcolor: 'primary.main',
                            borderRadius: '40%',
                            width: '40px',
                            height: '40px',
                            '&:hover': {
                                bgcolor: 'primary.dark',
                            },
                        }}
                        title="Adicionar membro"
                    >
                        <AddIcon sx={{ fontSize: 32, color: 'white' }} />
                    </IconButton>
                </Box>
            </Box>
            <TableContainer sx={{ backgroundColor: '#cccccc', mt: 2 }}>
                <Table sx={{ tableLayout: 'fixed' }}>
                    <TableHead>
                        <TableRow>
                            <TableCell align="left">Total: {users.users.length}</TableCell>
                            <TableCell align="left">Nome</TableCell>
                            <TableCell align="left">Função</TableCell>
                            <TableCell align="center">Registos</TableCell>
                            <TableCell align="center">Ação</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {users.users.map((user) => (
                            <TableRow key={user.id} onClick={() => handleViewProfile(user.id)} sx={{ cursor: 'pointer' }} title={"Ver perfil"}>
                                <TableCell align="center">
                                    <Avatar
                                        alt={user.nome}
                                        src={user.foto || "https://cdn-icons-png.flaticon.com/512/3135/3135715.png"}
                                        variant="rounded"
                                        sx={{ width: "40%", height: "40%" }}
                                    />
                                </TableCell>
                                <TableCell align="left">{user.nome}</TableCell>
                                <TableCell align="left">{user.func}</TableCell>
                                <TableCell align="center">
                                    <IconButton
                                        variant="contained"
                                        onClick={(e) => {
                                            e.stopPropagation();
                                            handleViewUserRecords(1, user.id);
                                        }}
                                        title="Ver registos"
                                        color={"default"}
                                    >
                                        <TapAndPlayIcon />
                                    </IconButton>
                                </TableCell>
                                <TableCell align="center">
                                    {user.id !== currUser?.id && (
                                        <IconButton
                                            variant="contained"
                                            onClick={(e) => {
                                                e.stopPropagation();
                                                handleRemoveUser(user.id);
                                            }}
                                            title="Remover membro"
                                            color={"error"}
                                        >
                                            <PersonRemoveIcon />
                                        </IconButton>
                                    )}

                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Stack>
    );
}