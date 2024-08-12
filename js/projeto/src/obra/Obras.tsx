import { useEffect, useState } from "react";
import { useCookies } from "react-cookie";
import * as React from "react";
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import IconButton from '@mui/material/IconButton';
import { Avatar, Box, CircularProgress, Snackbar, Stack, Typography } from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import InfoIcon from "@mui/icons-material/Info";
import {useNavigate } from "react-router-dom";
import Button from "@mui/material/Button";
import {path} from "../App";
// @ts-ignore
import emptyFoto from "../assets/noImage.png";

interface DateObject {
    year: number;
    dayOfMonth: number;
    month: string;
    dayOfWeek: string;
    dayOfYear: number;
    monthNumber: number;
    value$kotlinx_datetime: string;
}

interface Obra {
    oid: number;
    name: string;
    location: string;
    description: string;
    startDate: DateObject | null;
    endDate: DateObject | null;
    status: string;
    foto: string | null;
}

interface ObrasOutputModel {
    obras: Obra[];
}

export default function Obras() {
    const [cookies] = useCookies(["token"]);
    const [obras, setObras] = useState<ObrasOutputModel>({ obras: [] });
    const [loading, setLoading] = useState(true);
    const [snackbarOpen, setSnackbarOpen] = useState(false);

    useEffect(() => {
        fetch(`${path}/obras`, {
            method: "GET",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`,
            },
        })
            .then((res) => {
                if (res.ok) {
                    return res.json();
                } else if (res.status === 404) {
                    setSnackbarOpen(true);
                    console.error("No obras found");
                    return null;
                } else {
                    throw new Error("Error fetching obras");
                }
            })
            .then((body) => {
                if (body) {
                    setObras(body);
                }
                setLoading(false);
            })
            .catch((error) => {
                console.error("Error fetching obras:", error);
                setLoading(false);
            });
    }, [cookies.token]);

    const navigate = useNavigate();

    const handleClickObra = (oid: number) => {
        navigate(`/obras/${oid}`);
    }

    const handleClickAddObra = () => {
        navigate("/addObra");
    }

    const handleCloseSnackbar = () => {
        setSnackbarOpen(false);
    };

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
        <Stack sx={{ m: '5rem 0' }}>
            <Box
                sx={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                }}
            >
                <Typography variant="h4" color={"black"}>Obras</Typography>
                <IconButton onClick={handleClickAddObra} color="primary" sx={{
                    bgcolor: 'primary.main',
                    borderRadius: '40%',
                    width: '40px',
                    height: '40px',
                    '&:hover': {
                        bgcolor: 'primary.dark',
                    },
                }}>
                    <AddIcon sx={{ fontSize: 32, color: 'white' }}/>
                </IconButton>
            </Box>
            <TableContainer sx={{ backgroundColor: '#cccccc', mt: 2 }}>
                <Table sx={{ tableLayout: 'fixed' }}>
                    <TableHead>
                        <TableRow>
                            <TableCell align="center"></TableCell>
                            <TableCell align="left" sx={{ fontSize: '1.1rem' }}>Nome</TableCell>
                            <TableCell align="left" sx={{ fontSize: '1.1rem' }}>Localização</TableCell>
                            <TableCell align="center" sx={{ fontSize: '1.1rem' }}>Info</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {loading ? (
                            <TableRow>
                                <TableCell colSpan={4} align="center">
                                    <CircularProgress />
                                </TableCell>
                            </TableRow>
                        ) : (
                            obras.obras.length > 0 ? (
                                obras.obras.map((obra) => (
                                    <TableRow key={obra.oid}>
                                        <TableCell align="center">
                                            <Avatar
                                                alt={obra.name}
                                                src={obra.foto || emptyFoto}
                                                variant="rounded"
                                                sx={{ width: "40%", height: "40%" }}
                                            />
                                        </TableCell>
                                        <TableCell align="left" sx={{ fontSize: '1.1rem' }}>{obra.name}</TableCell>
                                        <TableCell align="left" sx={{ fontSize: '1.1rem' }}>{obra.location}</TableCell>
                                        <TableCell align="center">
                                            <IconButton onClick={() => handleClickObra(obra.oid)}>
                                                <InfoIcon/>
                                            </IconButton>
                                        </TableCell>
                                    </TableRow>
                                ))
                            ) : (
                                <TableRow>
                                    <TableCell colSpan={4} align="center">
                                        Ainda não está registado em nenhuma obra.
                                    </TableCell>
                                </TableRow>
                            )
                        )}
                    </TableBody>
                </Table>
            </TableContainer>

            <Snackbar
                open={snackbarOpen}
                autoHideDuration={10000}
                onClose={handleCloseSnackbar}
                message={"Não existem obras registadas."}
                anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
            />
        </Stack>
    );
}
