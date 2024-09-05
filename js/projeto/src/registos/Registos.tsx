import {
    MRT_TableBodyCellValue,
} from 'material-react-table';
import {
    Box, Snackbar,
    Stack,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Typography,
    CircularProgress,
    IconButton, FormControl, InputLabel, Select
} from '@mui/material';
import { useEffect, useState } from "react";
import { useCookies } from "react-cookie";
import * as React from "react";
import RegistoForm from "./RegistoForm";
import AddIcon from "@mui/icons-material/Add";
import {useNavigate} from "react-router-dom";
import Button from "@mui/material/Button";
import {path} from "../App";
import NavBar from "../NavBar";
import MenuItem from "@mui/material/MenuItem";
import RegistoExitForm from "./RegistoExitForm";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import {DateRangeFilter, formatDate, mapStatusToPortuguese, Pagination, table} from "../Utils";
import ArrowForwardIosIcon from '@mui/icons-material/ArrowForwardIos';
import ArrowBackIosIcon from '@mui/icons-material/ArrowBackIos';
import TextField from "@mui/material/TextField";
import KeyboardDoubleArrowLeftIcon from '@mui/icons-material/KeyboardDoubleArrowLeft';
import KeyboardDoubleArrowRightIcon from '@mui/icons-material/KeyboardDoubleArrowRight';

const columns = [
    { accessorKey: 'nome_obra', header: 'Nome da obra' },
    { accessorKey: 'entrada', header: 'Entrada' },
    { accessorKey: 'saida', header: 'Saida' },
    { accessorKey: 'status', header: 'Estado' }
];

export interface DateObject {
    year: number;
    dayOfMonth: number;
    month: string;
    dayOfWeek: string;
    dayOfYear: number;
    monthNumber: number;
    value$kotlinx_datetime: string;
}

export interface Registo {
    id: number;
    id_utilizador: number;
    id_obra: number;
    nome_obra: string;
    entrada: DateObject;
    saida: DateObject | null;
    status: string | null;
    oid: number | null;
}

const pageSize = 5;

export default function Registos () {
    const [cookies] = useCookies(["token"]);
    const [registos, setRegistos] = useState<Registo[]>([])
    const [loading, setLoading] = useState(true);
    const [openForm, setOpenForm] = useState(false);
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const navigate = useNavigate();
    const [exitOpenForm, setExitOpenForm] = useState(false);
    const [selectedRegisto, setSelectedRegisto] = useState<Registo | null>(null);
    const [title, setTitle] = useState<string>("Registos")
    const [filterMode, setFilterMode] = useState<'all' | 'incomplete'>('all')

    const [page, setPage] = useState<number>(1);
    const [totalPages, setTotalPages] = useState(1);

    const [initialDate, setInitialDate] = useState<string | null>(null);
    const [endDate, setEndDate] = useState<string | null>(null);

    const fetchRegistos = (pageNumber: number) => {
        handleMenuClose()
        setFilterMode('all')
        setTitle("Registos")
        setLoading(true)
        const params = new URLSearchParams({ page: String(pageNumber) });
        if (filterMode === 'all') {
            if (initialDate) params.append("initialDate", initialDate);
            if (endDate) params.append("endDate", endDate);
        }
        const queryString = params.toString();
        fetch(`${path}/registos?${queryString}`, {
            method: "GET",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`
            },
        }).then((res) => {
            if (res.ok) {
                return res.json()
            }
            else {
                setSnackbarOpen(true);
                setLoading(false);
                return null;
            }
        }).then((body) => {
            if (body) {
                console.log("body1: ", JSON.stringify(body));
                const parsedRegisters = body.registers.map((register: { entrada: string; saida: string; status: string; }) => ({
                    ...register,
                    entrada: formatDate(register.entrada),
                    saida: formatDate(register.saida),
                    status: mapStatusToPortuguese(register.status),
                }));
                setRegistos(parsedRegisters);
                console.log("body2: ", JSON.stringify(parsedRegisters));
                //setRegistos(body.registers);
                setTotalPages(Math.ceil(body.registersSize / pageSize));
                setLoading(false);
            }
        }).catch(error => {
            console.error("Error fetching registos: ", error);
        })
    }

    const fetchRegistosIncompletos = (pageNumber: number) => {
        handleMenuClose()
        setFilterMode('incomplete')
        setTitle("Registos Incompletos")
        setLoading(true)
        const params = new URLSearchParams({ page: String(pageNumber) });
        if (filterMode === 'all') {
            if (initialDate) params.append("initialDate", initialDate);
            if (endDate) params.append("endDate", endDate);
        }
        const queryString = params.toString();
        fetch(`${path}/registos/incompletos?${queryString}`, {
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
                console.log("body1: ", JSON.stringify(body));
                const parsedRegisters = body.registers.map((register: { entrada: string; saida: string; status: string; }) => ({
                    ...register,
                    entrada: formatDate(register.entrada),
                    saida: formatDate(register.saida),
                    status: mapStatusToPortuguese(register.status),
                }));
                console.log("body2: ", JSON.stringify(parsedRegisters));
                setRegistos(parsedRegisters);
                //setRegistos(body.registers)
                setTotalPages(Math.ceil(body.registersSize / pageSize))
                setLoading(false)
            }
        }).catch(error => {
            console.error("Error fetching registos: ", error)
        })
    }

    useEffect(() => {
        console.log("page: ", page)
        if (filterMode === 'all') {
            fetchRegistos(page)
        }
        if (filterMode === 'incomplete') {
            fetchRegistosIncompletos(page)
        }
    }, [cookies.token, page, initialDate, endDate])

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

    const handleFilterChange = (event: { target: { value: any; }; }) => {
        const selectedFilter = event.target.value
        setFilterMode(selectedFilter)
        setInitialDate(null)
        setEndDate(null)
        if (selectedFilter === 'all') {
            fetchRegistos(page)
        } else if (selectedFilter === 'incomplete') {
            fetchRegistosIncompletos(page)
        }
    }

    const handleFilterReset = () => {
        setInitialDate(null)
        setEndDate(null)
        setPage(1)
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

    const handleClickOpenForm = () => {
        setOpenForm(true);
    };

    const handleCloseForm = (reload: boolean) => {
        setOpenForm(false);
        if (reload) {
            fetchRegistos(page);
        }
    };

    const handleCloseSnackbar = () => {
        setSnackbarOpen(false);
    };

    const handleClickOpenObra = (oid: number) => {
        navigate(`/obras/${oid}`)
    }

    const regTable = table(columns, registos)

    const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null)

    const handleMenuOpen = (event: React.MouseEvent<HTMLButtonElement>) => {
        setAnchorEl(event.currentTarget)
    }
    const handleMenuClose = () => {
        setAnchorEl(null)
    }

    const isMenuOpen = Boolean(anchorEl)

    const handleClickDeleteRegister = (registo: Registo) => {
        fetch(`${path}/obras/${registo.id_obra}/registos/${registo.id}`, {
            method: "DELETE",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`
            },
        }).then((res) => {
            if (res.ok) {
                fetchRegistos(page)
            } else {
                console.error("Error deleting registo: ", res)
            }
        }).catch(error => {
            console.error("Error deleting registo: ", error)
        })
    }

    const handleClickExitOpenForm = (registo: Registo) => {
        setSelectedRegisto(registo)
        setExitOpenForm(true);
    };

    const handleExitCloseForm = (reload: boolean) => {
        setExitOpenForm(false);
        if (reload) {
            fetchRegistos(page);
        }
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
        <><NavBar />
        <Stack sx={{ m: '2rem 0' }}>
            <Box
                sx={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                }}
            >
                <Typography variant="h4" color={"black"}>{title}</Typography>
                <DateRangeFilter
                    initialDate={initialDate}
                    endDate={endDate}
                    setInitialDate={setInitialDate}
                    setEndDate={setEndDate}
                    handleFilterReset={handleFilterReset}
                />
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                    <FormControl variant="outlined" sx={{ marginRight: 2, minWidth: 120 }}>
                        <InputLabel>Filtro</InputLabel>
                        <Select
                            value={filterMode}
                            onChange={handleFilterChange}
                            label="Filtro"
                        >
                            <MenuItem value="all">Todos</MenuItem>
                            <MenuItem value="incomplete">Incompletos</MenuItem>
                        </Select>
                    </FormControl>
                    <IconButton onClick={handleClickOpenForm} color="primary" title={"Adicionar registo"} sx={{
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
            </Box>
            <TableContainer sx={{ backgroundColor: '#cccccc', marginTop: 2}}>
                <Table sx={{ tableLayout: 'fixed' }}>
                    <TableHead>
                        {regTable.getHeaderGroups().map((headerGroup) => (
                            <TableRow key={headerGroup.id}>
                                {headerGroup.headers.map((header) => (
                                    <TableCell align="center" variant="head" key={header.id}>
                                        {header.isPlaceholder ? null : header.column.columnDef.header}
                                    </TableCell>
                                ))}
                            </TableRow>
                        ))}
                    </TableHead>
                    <TableBody>
                        {loading ? (
                            <TableRow>
                                <TableCell colSpan={columns.length} align="center">
                                    <CircularProgress/>
                                </TableCell>
                            </TableRow>
                        ) : (
                            regTable.getRowModel().rows.length == 0 ?
                                <TableRow>
                                    <TableCell colSpan={4} align="center">
                                        Não existem registos.
                                    </TableCell>
                                </TableRow>
                                :
                            regTable.getRowModel().rows.map((row, rowIndex) => (
                                <TableRow key={row.id} selected={row.getIsSelected()}>
                                    {row.getVisibleCells().map((cell, _columnIndex) => (
                                        <TableCell align="center" variant="body" key={cell.id}>
                                            {cell.column.id === 'nome_obra' ? (
                                                <Button onClick={() => handleClickOpenObra(row.original.id_obra)} style={{ textTransform: 'none' }}>
                                                    {cell.row.original.nome_obra}
                                                </Button>
                                            ) : cell.column.id === 'saida' ? (
                                                (row.original.status === "Incompleto" || row.original.status === "Incompleto via NFC") ? (
                                                    <>
                                                        <IconButton color="primary" title={"Finalizar"} onClick={() => handleClickExitOpenForm(row.original as Registo)}>
                                                            <EditIcon />
                                                        </IconButton>
                                                        <IconButton color="error" title={"Eliminar"} onClick={() => handleClickDeleteRegister(row.original as Registo)}>
                                                            <DeleteIcon />
                                                        </IconButton>
                                                    </>
                                                )
                                             : (
                                                <MRT_TableBodyCellValue
                                                    cell={cell}
                                                    table={regTable}
                                                    staticRowIndex={rowIndex}
                                                />
                                                    )
                                            ) : (
                                                <MRT_TableBodyCellValue
                                                cell={cell}
                                                table={regTable}
                                                staticRowIndex={rowIndex}
                                                 />
                                            )}
                                        </TableCell>
                                    ))}
                                </TableRow>
                            ))
                        )}
                    </TableBody>
                </Table>
            </TableContainer>
            <Pagination
                totalPages={totalPages}
                page={page}
                handleFirstPage={handleFirstPage}
                handlePreviousPage={handlePreviousPage}
                handlePageChange={handlePageChange}
                handleNextPage={handleNextPage}
                handleLastPage={handleLastPage}
            />
            <RegistoForm open={openForm} onHandleClose={handleCloseForm} obra={undefined}/>
            <RegistoExitForm open={exitOpenForm} onHandleClose={handleExitCloseForm} registo={selectedRegisto}/>
            <Snackbar
                open={snackbarOpen}
                autoHideDuration={10000}
                onClose={handleCloseSnackbar}
                message={"Sem registos."}
                anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
            />
        </Stack>
        </>
    );
};

