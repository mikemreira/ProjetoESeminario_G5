import {
    MRT_GlobalFilterTextField,
    MRT_TableBodyCellValue,
    MRT_TablePagination,
    MRT_ToolbarAlertBanner,
    flexRender,
    useMaterialReactTable, MRT_Row,
} from 'material-react-table';
import {
    Box, CircularProgress,
    Stack,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Typography,
} from '@mui/material';
import { useEffect, useState } from "react";
import { useCookies } from "react-cookie";
import * as React from "react";
import {Delete, Edit} from "@mui/icons-material";
import IconButton from "@mui/material/IconButton";
import Button from "@mui/material/Button";
import AddIcon from "@mui/icons-material/Add";
import {useNavigate, useParams, useSearchParams} from "react-router-dom";
import CheckIcon from '@mui/icons-material/Check';
import CloseIcon from '@mui/icons-material/Close';

const columns = [
    {
        accessorKey: 'uid',
        header: 'User ID',
    },
    {
        accessorKey: 'oid',
        header: 'Obra ID',
    },
    {
        accessorKey: 'userName',
        header: 'Nome',
    },
    {
        accessorKey: 'startTime',
        header: 'Entrada',
    },
    {
        accessorKey: 'endTime',
        header: 'Saida',
    },
    {
        accessorKey: 'status',
        header: 'State',
    },
    {
        id: 'actions',
        header: 'Actions'
    },
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
    userName: string;
    id: number;
    oid: number;
    uid: number;
    startTime: DateObject;
    endTime: DateObject | null;
    status: string | null;
}

export default function ObraRegistosOfAllPendingUsers() {
    const [cookies] = useCookies(["token"]);
    const [registos, setRegistos] = useState<Registo[]>([])
    const [loading, setLoading] = useState(true);
    const [open, setOpen] = useState(false);
    const { oid } = useParams<{ oid: string }>();
    const [searchParams, setSearchParams] = useSearchParams();

    useEffect(() => {
        const page = searchParams.get('page') || '0'
        fetch(`/api/obras/${oid}/registos/pendente?page=${page}`, {
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
                setRegistos(body.registers)
                setLoading(false)
                console.log(JSON.stringify(body))
            }
        }).catch(error => {
            console.error("Error fetching registos: ", error)
        })
    }, [cookies.token, open, searchParams])

    const handleClickOpen = () => {
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
    };

    const navigate = useNavigate();

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

    const handleAcceptOrRejectPendingRegister = (registerId: number, userId: number, response: string) => {
        fetch(`/api/obras/${oid}/registos`, {
            method: 'PUT',
            headers: {
                'Content-type': 'application/json',
                'Authorization': `Bearer ${cookies.token}`
            },
            body: JSON.stringify({ registerId: registerId, userId: userId, response: response })
        })
            .then((res) => {
                if (res.ok) return res;
                else return null;
            })
            .then((body) => {
                console.log(body);
                setRegistos((prevRegistos) =>
                    prevRegistos.map((registo) =>
                        registo.id === registerId ? { ...registo, status: response } : registo
                    )
                );
                navigate(`/obras/${oid}`)
            })
            .catch(error => {
                console.error("Error fetching: ", error);
            });
    };

    if (loading) return <CircularProgress/>
    else return (
        <Stack sx={{ m: '2rem 0' }}>
            <Box
                sx={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                }}
            >
                <Typography variant="h5" color={"black"}>Registos Pendentes da obra</Typography>
                {/**
                 * Use MRT components along side your own markup.
                 * They just need the `table` instance passed as a prop to work!
                 */}
                <MRT_GlobalFilterTextField table={table} />
                <IconButton onClick={handleClickOpen} color="primary" sx={{
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
            {/* Using Vanilla Material-UI Table components here */}
            <TableContainer sx={{ backgroundColor: '#cccccc',  }}>
                <Table sx={{ tableLayout: 'fixed' }}>
                    {/* Use your own markup, customize however you want using the power of TanStack Table */}
                    <TableHead>
                        {table.getHeaderGroups().map((headerGroup) => (
                            <TableRow key={headerGroup.id}>
                                {headerGroup.headers.map((header) => (
                                    <TableCell align="center" variant="head" key={header.id}>
                                        {header.isPlaceholder
                                            ? null
                                            : flexRender(
                                                header.column.columnDef.Header ??
                                                header.column.columnDef.header,
                                                header.getContext(),
                                            )}
                                    </TableCell>

                                ))}
                                <TableCell/>
                            </TableRow>
                        ))}
                    </TableHead>
                    <TableBody>
                        {table.getRowModel().rows.map((row, rowIndex) => (
                            <TableRow key={row.id} selected={row.getIsSelected()}>
                                {row.getVisibleCells().map((cell, _columnIndex) => (
                                    <TableCell align="center" variant="body" key={cell.id}>
                                        {/* Use MRT's cell renderer that provides better logic than flexRender */}
                                        <MRT_TableBodyCellValue
                                            cell={cell}
                                            table={table}
                                            staticRowIndex={rowIndex} //just for batch row selection to work
                                        />
                                    </TableCell>
                                ))}
                                <TableCell>
                                    <IconButton style={{ color: '#3547a1' }} onClick={() => handleAcceptOrRejectPendingRegister(row.original.id, row.original.uid, "completed")}>
                                        <CheckIcon sx={{ color: 'green' }}/>
                                    </IconButton>
                                    <IconButton style={{ color: '#c24242' }} onClick={() => handleAcceptOrRejectPendingRegister(row.original.id, row.original.uid, "rejected")}>
                                        <CloseIcon sx={{ color: 'red' }}/>
                                    </IconButton>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
            <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 2 }}>
                <MRT_TablePagination table={table} />
            </Box>
            <MRT_ToolbarAlertBanner stackAlertBanner table={table} />
        </Stack>
    );
};
