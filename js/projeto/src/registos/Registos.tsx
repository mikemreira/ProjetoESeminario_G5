import {
    MRT_GlobalFilterTextField,
    MRT_TableBodyCellValue,
    MRT_TablePagination,
    useMaterialReactTable
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
    IconButton
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
import {FilterList} from "react-admin";
import FilterListIcon from "@mui/icons-material/FilterList";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import RegistoExitForm from "./RegistoExitForm";
import EditIcon from "@mui/icons-material/Edit";

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
}

interface ExitWebInputModel {
    endTime: DateObject
    registerId: number
    oid: number
}

export default function Registos () {
    const [cookies] = useCookies(["token"]);
    const [registos, setRegistos] = useState<Registo[]>([])
    const [loading, setLoading] = useState(true);
    const [openForm, setOpenForm] = useState(false);
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const navigate = useNavigate();
    const [exitOpenForm, setExitOpenForm] = useState(false);
    const [selectedRegisto, setSelectedRegisto] = useState<Registo | null>(null);

    const fetchRegistos = () => {
        handleMenuClose()
        console.log("fetching registos")
        fetch(`${path}/registos`, {
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
                setRegistos(body.registers);
                setLoading(false);
            }
        }).catch(error => {
            console.error("Error fetching registos: ", error);
        })
    }

    useEffect(() => {
        fetchRegistos()
    }, [cookies.token])

    const handleClickOpenForm = () => {
        setOpenForm(true);
    };

    const handleCloseForm = (reload: boolean) => {
        setOpenForm(false);
        if (reload) {
            fetchRegistos();
        }
    };

    const handleCloseSnackbar = () => {
        setSnackbarOpen(false);
    };

    const handleClickOpenObra = (oid: number) => {
        navigate(`/obras/${oid}`)
    }

    const table = useMaterialReactTable({
        columns,
        data: registos,
        enableRowSelection: false,
        initialState: {
            pagination: { pageSize: 5, pageIndex: 0 },
            showGlobalFilter: true,
        },
        muiPaginationProps: {
            rowsPerPageOptions: [5, 10, 15],
            variant: 'outlined',
        },
        paginationDisplayMode: 'pages',
    });

    const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null)

    const handleMenuOpen = (event: React.MouseEvent<HTMLButtonElement>) => {
        setAnchorEl(event.currentTarget)
    }
    const handleMenuClose = () => {
        setAnchorEl(null)
    }

    const isMenuOpen = Boolean(anchorEl)

    const fetchRegistosIncompletos = () => {
        handleMenuClose()
        console.log("fetching registos incompletos")
        fetch(`${path}/registos/incompletos`, {
            method: "GET",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`
            },
        }).then((res) => {
            if (res.ok) return res.json()
            else return null
        }).then((body) => {
            console.log("incompletos: " + body)
            if (body) {
                setRegistos(body.registers);
            }
        }).catch(error => {
            console.error("Error fetching registos: ", error)
        })
    }

    const handleClickExitOpenForm = (registo: Registo) => {
        setSelectedRegisto(registo)
        setExitOpenForm(true);
    };

    const handleExitCloseForm = (reload: boolean) => {
        setExitOpenForm(false);
        if (reload) {
            fetchRegistosIncompletos();
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
                <Typography variant="h4" color={"black"}>Registos</Typography>
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                    <IconButton onClick={handleMenuOpen} color="primary">
                        <FilterList  icon={<FilterListIcon/>} label={""} title={"Filtro"}/>
                    </IconButton>
                    <MRT_GlobalFilterTextField table={table} />
                        <Menu
                            anchorEl={anchorEl}
                            open={isMenuOpen}
                            onClose={handleMenuClose}
                        >
                            <MenuItem onClick={() => fetchRegistos()}>Todos</MenuItem>
                            <MenuItem onClick={() => fetchRegistosIncompletos()}>Incompletos</MenuItem>
                        </Menu>
                </Box>
                <IconButton onClick={handleClickOpenForm} color="primary" sx={{
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
            <TableContainer sx={{ backgroundColor: '#cccccc' }}>
                <Table sx={{ tableLayout: 'fixed' }}>
                    <TableHead>
                        {table.getHeaderGroups().map((headerGroup) => (
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
                            table.getRowModel().rows.length == 0 ?
                                <TableRow>
                                    <TableCell colSpan={4} align="center">
                                        Ainda não efetuou nenhum registo.
                                    </TableCell>
                                </TableRow>
                                :
                            table.getRowModel().rows.map((row, rowIndex) => (
                                <TableRow key={row.id} selected={row.getIsSelected()}>
                                    {row.getVisibleCells().map((cell, _columnIndex) => (
                                        <TableCell align="center" variant="body" key={cell.id}>
                                            {cell.column.id === 'nome_obra' ? (
                                                <Button onClick={() => handleClickOpenObra(row.original.id_obra)} style={{ textTransform: 'none' }}>
                                                    {cell.row.original.nome_obra}
                                                </Button>
                                            ) : (
                                                <MRT_TableBodyCellValue
                                                    cell={cell}
                                                    table={table}
                                                    staticRowIndex={rowIndex}
                                                />
                                            )}
                                            {cell.column.id === 'status' && cell.row.original.status === "unfinished" ? (
                                                <IconButton style={{ color: '#3547a1' }} title={"Finalizar"} onClick={() => handleClickExitOpenForm(cell.row.original)}>
                                                    <EditIcon/>
                                                </IconButton>
                                            ) : null}
                                        </TableCell>
                                    ))}
                                </TableRow>
                            ))
                        )}
                    </TableBody>
                </Table>
            </TableContainer>
            <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 2 }}>
            <MRT_TablePagination table={table} />
            </Box>
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

