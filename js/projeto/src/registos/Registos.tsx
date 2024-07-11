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

const columns = [
    { accessorKey: 'nome_obra', header: 'Nome da obra' },
    { accessorKey: 'entrada', header: 'Entrada' },
    { accessorKey: 'saida', header: 'Saida' },
    { accessorKey: 'status', header: 'Estado' }
];

interface DateObject {
    year: number;
    dayOfMonth: number;
    month: string;
    dayOfWeek: string;
    dayOfYear: number;
    monthNumber: number;
    value$kotlinx_datetime: string;
}

interface Registo {
    id: number;
    id_utilizador: number;
    id_obra: number;
    nome_obra: string;
    entrada: DateObject;
    saida: DateObject | null;
    status: string | null;
}

export default function Registos () {
    const [cookies] = useCookies(["token"]);
    const [registos, setRegistos] = useState<Registo[]>([])
    const [loading, setLoading] = useState(true);
    const [openForm, setOpenForm] = useState(false);
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const navigate = useNavigate();

    const fetchRegistos = () => {
        fetch("/api/registos", {
            method: "GET",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`
            },
        }).then((res) => {
            if (res.ok) {
                console.log("Registos: " + res)
                return res.json()
            }
            else {
                setSnackbarOpen(true);
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
        <Stack sx={{ m: '2rem 0' }}>
            <Box
                sx={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                }}
            >
                <Typography variant="h4" color={"black"}>Registos</Typography>
                <MRT_GlobalFilterTextField table={table} />
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

            <Snackbar
                open={snackbarOpen}
                autoHideDuration={10000}
                onClose={handleCloseSnackbar}
                message={"Sem registos."}
                anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
            />
        </Stack>
    );
};

