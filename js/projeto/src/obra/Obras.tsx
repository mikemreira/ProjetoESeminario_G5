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
import KeyboardDoubleArrowLeftIcon from "@mui/icons-material/KeyboardDoubleArrowLeft";
import ArrowBackIosIcon from "@mui/icons-material/ArrowBackIos";
import ArrowForwardIosIcon from "@mui/icons-material/ArrowForwardIos";
import KeyboardDoubleArrowRightIcon from "@mui/icons-material/KeyboardDoubleArrowRight";
import {pageSize} from "./ObrasInfo";

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
    size: number;
}

export default function Obras() {
    const [cookies] = useCookies(["token"]);
    const [obras, setObras] = useState<ObrasOutputModel>({ obras: [], size: 1 });
    const [loading, setLoading] = useState(true);
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [page, setPage] = useState<number>(1);
    const [totalPages, setTotalPages] = useState<number>(1);

    const handleNextPage = () => {
        if (page) {
            setPage(page + 1)
        }
    }

    const handlePreviousPage = () => {
        if (page > 1) {
            setPage(page - 1)
        }
    }

    const handlePageChange = (pageNumber: number) => {
        setPage(pageNumber)
    }

    const handleFirstPage = () => {
        setPage(1)
    }

    const handleLastPage = () => {
        setPage(totalPages)
    }

    const fetchObras = (pageNumber: number) => {
        const params = new URLSearchParams({ page: String(pageNumber) });
        const queryString = params.toString();
        fetch(`${path}/obras?${queryString}`, {
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
                    setObras(body)
                    setTotalPages(Math.ceil(body.size / pageSize))
                }
                setLoading(false);
            })
            .catch((error) => {
                console.error("Error fetching obras:", error);
                setLoading(false);
            });
    }

    useEffect(() => {
        fetchObras(page);
    }, [cookies.token, page]);

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
                    mb: 2,
                }}
            >
                <Typography variant="h4" color={"black"}>Obras</Typography>
                <IconButton onClick={handleClickAddObra} color="primary" title={"Adicionar obra"} sx={{
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
                                            <IconButton onClick={() => handleClickObra(obra.oid)} title={"Ver obra"}>
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
            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2 }}>
                { totalPages > 0 && (
                    <>
                        <IconButton onClick={handleFirstPage} disabled={page === 1} title={"Retroceder tudo"}>
                            <KeyboardDoubleArrowLeftIcon />
                        </IconButton>
                        <IconButton onClick={handlePreviousPage} disabled={page === 1} title={"Retroceder"}>
                            <ArrowBackIosIcon />
                        </IconButton>
                        {[...Array(3)].map((_, index) => {
                            const startPage = Math.max(1, Math.min(page - 1, totalPages - 2));
                            const currentPage = startPage + index;
                            if (currentPage <= totalPages) {
                                return (
                                    <Button
                                        key={currentPage}
                                        onClick={() => handlePageChange(currentPage)}
                                        variant={page === currentPage ? "contained" : "outlined"}
                                        sx={{
                                            minWidth: '40px',
                                            minHeight: '40px',
                                            borderRadius: '50%',
                                            mx: 1
                                        }}
                                    >
                                        {currentPage}
                                    </Button>
                                );
                            }
                            return null;
                        })}
                        <IconButton onClick={handleNextPage} disabled={page === totalPages} title={"Avançar"}>
                            <ArrowForwardIosIcon />
                        </IconButton>
                        <IconButton onClick={handleLastPage} disabled={page === totalPages} title={"Avançar tudo"}>
                            <KeyboardDoubleArrowRightIcon />
                        </IconButton>
                    </>
                )}
            </Box>

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
