import {
    MRT_GlobalFilterTextField,
    MRT_TableBodyCellValue,
    MRT_TablePagination,
    flexRender,
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
import { Delete, Edit } from "@mui/icons-material";
import RegistoForm from "./RegistoForm";
import AddIcon from "@mui/icons-material/Add";

const columns = [
    { accessorKey: 'id_utilizador', header: 'User ID' },
    { accessorKey: 'id_obra', header: 'Obra ID' },
    { accessorKey: 'nome', header: 'Nome' },
    { accessorKey: 'entrada', header: 'Entrada' },
    { accessorKey: 'saida', header: 'Saida' },
    { accessorKey: 'status', header: 'State' },
    { id: 'actions', header: 'Actions' },
];

const handleEdit = () => { }
const handleDelete = (row: Registo) => {
    console.log("ID" + row.id)
}

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
    nome: string;
    entrada: DateObject;
    saida: DateObject | null;
    status: string | null;
}

interface RegistosOutputModel {
    registos: Registo[];
}

const Registos = () => {
    const [cookies] = useCookies(["token"]);
    const [registos, setRegistos] = useState<Registo[]>([])
    const [loading, setLoading] = useState(true);
    const [openForm, setOpenForm] = useState(false);
    const [snackbarOpen, setSnackbarOpen] = useState(false);

    useEffect(() => {
        fetch("/api/registos", {
            method: "GET",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`
            },
        }).then((res) => {
            if (res.ok) return res.json()
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
    }, [cookies.token])

    const handleClickOpenForm = () => {
        setOpenForm(true);
    };

    const handleCloseForm = () => {
        setOpenForm(false);
    };

    const handleCloseSnackbar = () => {
        setSnackbarOpen(false);
    };

    const table = useMaterialReactTable({
        columns,
        data: registos,
        enableRowSelection: true,
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

    return (
        <Stack sx={{ m: '2rem 0' }}>
            <Box
                sx={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                }}
            >
                <MRT_GlobalFilterTextField table={table} />
                <MRT_TablePagination table={table} />
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
            <TableContainer sx={{ backgroundColor: '#cccccc',  }}>
                <Table sx={{ tableLayout: 'fixed' }}>
                    <TableHead>
                        {table.getHeaderGroups().map((headerGroup) => (
                            <TableRow key={headerGroup.id}>
                                {headerGroup.headers.map((header) => (
                                    <TableCell align="center" variant="head" key={header.id}>
                                        {header.isPlaceholder ? null : flexRender(
                                            header.column.columnDef.Header ?? header.column.columnDef.header,
                                            header.getContext()
                                        )}
                                    </TableCell>
                                ))}
                                <TableCell/>
                            </TableRow>
                        ))}
                    </TableHead>
                    <TableBody>
                        {loading ? (
                            <TableRow>
                                <TableCell colSpan={columns.length + 1} align="center">
                                    <CircularProgress/>
                                </TableCell>
                            </TableRow>
                        ) : (
                            table.getRowModel().rows.map((row, rowIndex) => (
                                <TableRow key={row.id} selected={row.getIsSelected()}>
                                    {row.getVisibleCells().map((cell, _columnIndex) => (
                                        <TableCell align="center" variant="body" key={cell.id}>
                                            <MRT_TableBodyCellValue
                                                cell={cell}
                                                table={table}
                                                staticRowIndex={rowIndex}
                                            />
                                        </TableCell>
                                    ))}
                                    <TableCell>
                                        <IconButton style={{ color: '#3547a1' }} onClick={handleEdit}>
                                            <Edit/>
                                        </IconButton>
                                        <IconButton style={{ color: '#c24242' }} onClick={() => handleDelete(row.original)}>
                                            <Delete/>
                                        </IconButton>
                                    </TableCell>
                                </TableRow>
                            ))
                        )}
                    </TableBody>
                </Table>
            </TableContainer>

            <RegistoForm open={openForm} onHandleClose={handleCloseForm} onHandleOpen={handleClickOpenForm} />

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

export default Registos;
