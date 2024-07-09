import {
    Avatar,
    Box,
    Button,
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


interface ObraFuncionariosFormProps {
    handleViewAllRecords: () => void;
    handleClickAddFuncionario: () => void;
    handleViewProfile: (id: number) => void;
    handleViewUserRecords: (id: number) => void;
    users: UserOutputModel;
    handleRemoveUser: (id: number) => void;
}

export default function ObraFuncionariosForm({
    handleViewAllRecords,
    handleClickAddFuncionario,
    handleViewProfile,
    handleViewUserRecords,
    users,
    handleRemoveUser
}: ObraFuncionariosFormProps) {
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
                            <TableCell align="center"></TableCell>
                            <TableCell align="left">Nome</TableCell>
                            <TableCell align="left">Função</TableCell>
                            <TableCell align="center">Registos</TableCell>
                            <TableCell align="center">Ação</TableCell>
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
                                <TableCell align="left">{user.nome}</TableCell>
                                <TableCell align="left">{user.func}</TableCell>
                                <TableCell align="center">
                                    <IconButton
                                        variant="contained"
                                        onClick={(e) => {
                                            e.stopPropagation();
                                            handleViewUserRecords(user.id);
                                        }}
                                        title="Ver registos"
                                        color={"default"}
                                    >
                                        <TapAndPlayIcon />
                                    </IconButton>
                                </TableCell>
                                <TableCell align="center">
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
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Stack>
    );
}