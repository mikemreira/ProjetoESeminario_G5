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
import {useLocation, useParams, useSearchParams} from "react-router-dom";

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

const handleEdit = () => {  }
const handleDelete = (row: Registo) => {
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
    userName: string;
    oid: number;
    uid: number;
    startTime: DateObject;
    endTime: DateObject | null;
    status: string | null;
}


export default function ObraRegistos() {
    const [cookies] = useCookies(["token"]);
    const [registos, setRegistos] = useState<Registo[]>([])
    const [loading, setLoading] = useState(true);
    const [open, setOpen] = useState(false);
    const { oid } = useParams<{ oid: string }>();
    const [searchParams, setSearchParams] = useSearchParams();
    const status = searchParams.get('status')

    useEffect(() => {
        const page = searchParams.get('page') || '0'
        fetch(`/api/obras/${oid}/registos/me?page=${page}&status=${status}`, {
            method: "GET",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`
            },
        }).then((res) => {
            console.log(oid)
            console.log(status)
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

    const table = useMaterialReactTable({
        columns,
        data: registos, //must be memoized or stable (useState, useMemo, defined outside of this component, etc.)
        //MRT display columns can still work, optionally override cell renders with `displayColumnDefOptions`
        enableRowSelection: true,
        initialState: {
            pagination: { pageSize: 5, pageIndex: 0 },
            showGlobalFilter: true,
        },
        //customize the MRT components
        muiPaginationProps: {
            rowsPerPageOptions: [5, 10, 15],
            variant: 'outlined',
        },
        paginationDisplayMode: 'pages',
    });

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
                {/**
                 * Use MRT components along side your own markup.
                 * They just need the `table` instance passed as a prop to work!
                 */}
                <MRT_GlobalFilterTextField table={table} />
                <MRT_TablePagination table={table} />
                {status === "on going" && (
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
                )}
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
                                    <IconButton style={{ color: '#3547a1' }} onClick={() => handleEdit()}>
                                        <Edit/>
                                    </IconButton>
                                    <IconButton style={{ color: '#c24242' }} onClick={() => handleDelete(row.original)}>
                                        <Delete/>
                                    </IconButton>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
            <MRT_ToolbarAlertBanner stackAlertBanner table={table} />
        </Stack>
    );
};
