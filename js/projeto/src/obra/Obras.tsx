import { useEffect, useState } from "react";
import { useCookies } from "react-cookie";
import * as React from "react";
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import TablePagination from '@mui/material/TablePagination';
import IconButton from '@mui/material/IconButton';
import FirstPageIcon from '@mui/icons-material/FirstPage';
import KeyboardArrowLeft from '@mui/icons-material/KeyboardArrowLeft';
import KeyboardArrowRight from '@mui/icons-material/KeyboardArrowRight';
import LastPageIcon from '@mui/icons-material/LastPage';
import { useTheme } from '@mui/material/styles';
import {Box} from "@mui/material";
import Fab from '@mui/material/Fab';
import AddIcon from '@mui/icons-material/Add';
import {useLocation, useNavigate} from "react-router-dom";
import InfoIcon from '@mui/icons-material/Info';
import ChevronRightSharpIcon from '@mui/icons-material/ChevronRightSharp';
import { Snackbar } from "@mui/material";

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
}

interface ObrasOutputModel {
    obras: Obra[];
}

// Utility function to convert date object to string
const formatDate = (dateObj: DateObject | null): string => {
    return dateObj ? dateObj.value$kotlinx_datetime : "N/A";
}

export default function Obras() {
    const [cookies] = useCookies(["token"]);
    const [obras, setObras] = useState<ObrasOutputModel>({ obras: [] });
    const [page, setPage] = React.useState(0);
    const [rowsPerPage, setRowsPerPage] = React.useState(3);
    const theme = useTheme();
    const location = useLocation();
    const success = location.state?.success;
    const [open, setOpen] = React.useState(success || false);

    const handleClose = (event?: React.SyntheticEvent | Event, reason?: string) => {
        if (reason === 'clickaway') {
            return;
        }
        setOpen(false);
    };

    useEffect(() => {
        fetch("/api/obras", {
            method: "GET",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`,
            },
        })
            .then((res) => {
                if (res.ok) {
                    return res.json();
                } else {
                    return null;
                }
            })
            .then((body) => {
                if (body) {
                    setObras(body);
                    console.log(body);
                }
            })
            .catch((error) => {
                console.error("Error fetching obras:", error);
            });
    }, [cookies.token]);

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    const navigate = useNavigate();

    const handleClickObra = (oid: number) => {
        navigate(`/obras/${oid}`)
    }

    const handleClickAddObra = () => {
        navigate("/addObra")
    }


    return (
        <div className="form-obras">
            <h1 className="black-text">Obras</h1>
            <TableContainer component={Paper}  sx={{ border: '1px solid black' }}>
                <Table sx={{ minWidth: 650 }} aria-label="simple table">
                    <TableHead>
                        <TableRow>
                            <TableCell>Nome</TableCell>
                            <TableCell>Localização</TableCell>
                            <TableCell>Descrição</TableCell>
                            <TableCell>Data de Início</TableCell>
                            <TableCell>Data de Fim</TableCell>
                            <TableCell>Status</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {(rowsPerPage > 0
                                ? obras.obras.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                                : obras.obras
                        ).map((obra) => (
                            <TableRow key={obra.oid}>
                                <TableCell component="th" scope="row">
                                    <a href="#" className="black-text" onClick={() => handleClickObra(obra.oid)}>{obra.name}</a>
                                </TableCell>
                                <TableCell>{obra.location}</TableCell>
                                <TableCell>{obra.description}</TableCell>
                                <TableCell>{formatDate(obra.startDate)}</TableCell>
                                <TableCell>{formatDate(obra.endDate)}</TableCell>
                                <TableCell>{obra.status}</TableCell>
                                <TableCell> <IconButton onClick={() => handleClickObra(obra.oid)}>
                                    <ChevronRightSharpIcon/>
                                </IconButton>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
            <TablePagination
                rowsPerPageOptions={[1, 3, 5]}
                colSpan={3}
                count={obras.obras.length}
                rowsPerPage={rowsPerPage}
                page={page}
                onPageChange={handleChangePage}
                onRowsPerPageChange={handleChangeRowsPerPage}
                ActionsComponent={(props) => <TablePaginationActions {...props} />}
            />
            <Box sx={{ display: 'flex',justifyContent: 'flex-end','& > :not(style)': { m: 1 } }}>
                <Fab color="primary" aria-label="add" onClick={handleClickAddObra}>
                    <AddIcon />
                </Fab>
            </Box>
            <Snackbar
                open={open}
                autoHideDuration={5000}
                onClose={handleClose}
                message="Obra adicionada com sucesso"
                anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
            />
        </div>
    );

    // Custom TablePaginationActions component
    function TablePaginationActions(props) {
        const { count, page, rowsPerPage, onPageChange } = props;

        const handleFirstPageButtonClick = (event) => {
            onPageChange(event, 0);
        };

        const handleBackButtonClick = (event) => {
            onPageChange(event, page - 1);
        };

        const handleNextButtonClick = (event) => {
            onPageChange(event, page + 1);
        };

        const handleLastPageButtonClick = (event) => {
            onPageChange(event, Math.max(0, Math.ceil(count / rowsPerPage) - 1));
        };

        return (
            <Box sx={{ flexShrink: 0, ml: 2.5 }}>
                <IconButton
                    onClick={handleFirstPageButtonClick}
                    disabled={page === 0}
                    aria-label="first page"
                >
                    {theme.direction === 'rtl' ? <LastPageIcon /> : <FirstPageIcon />}
                </IconButton>
                <IconButton
                    onClick={handleBackButtonClick}
                    disabled={page === 0}
                    aria-label="previous page"
                >
                    {theme.direction === 'rtl' ? <KeyboardArrowRight /> : <KeyboardArrowLeft />}
                </IconButton>
                <IconButton
                    onClick={handleNextButtonClick}
                    disabled={page >= Math.ceil(count / rowsPerPage) - 1}
                    aria-label="next page"
                >
                    {theme.direction === 'rtl' ? <KeyboardArrowLeft /> : <KeyboardArrowRight />}
                </IconButton>
                <IconButton
                    onClick={handleLastPageButtonClick}
                    disabled={page >= Math.ceil(count / rowsPerPage) - 1}
                    aria-label="last page"
                >
                    {theme.direction === 'rtl' ? <FirstPageIcon /> : <LastPageIcon />}
                </IconButton>
            </Box>
        );
    }
}


