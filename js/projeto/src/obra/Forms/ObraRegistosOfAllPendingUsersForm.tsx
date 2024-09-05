import {Box, Stack, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography} from "@mui/material";
import {
    flexRender,
    MRT_TableBodyCellValue,
} from "material-react-table";
import IconButton from "@mui/material/IconButton";
import CheckIcon from "@mui/icons-material/Check";
import CloseIcon from "@mui/icons-material/Close";
import * as React from "react";
import Button from "@mui/material/Button";
import TextField from "@mui/material/TextField";
import {useEffect} from "react";
import {Pagination} from "../../Utils";

interface ObraRegistosOfAllPendingUsersFormProps {
    table: any;
    handleAcceptOrRejectPendingRegister: (id: number, uid: number, status: string) => void;
    totalPages: number;
    handleNextPage: () => void;
    handlePreviousPage: () => void;
    handleFilterReset: () => void;
    handlePageChange: (page: number) => void;
    handleFirstPage: () => void;
    handleLastPage: () => void;
    page: number;
    initialDate: string | null;
    setInitialDate: (date: string) => void;
    endDate: string | null;
    setEndDate: (date: string) => void;
    handleGetPendingRegisters: (page: number) => void;
}

export default function ObraRegistosOfAllPendingUsersForm({
    table,
    handleAcceptOrRejectPendingRegister,
    totalPages,
    handleNextPage,
    handlePreviousPage,
    handleFilterReset,
    handlePageChange,
    handleFirstPage,
    handleLastPage,
    page,
    initialDate,
    setInitialDate,
    endDate,
    setEndDate,
    handleGetPendingRegisters
}: ObraRegistosOfAllPendingUsersFormProps) {

    useEffect(() => {
        handleGetPendingRegisters(page);
    }, [page, initialDate, endDate])

    return (
        <>
            <Stack sx={{ m: '2rem 0' }}>
                <Box
                    sx={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        mb: 2
                    }}
                >
                    <Typography variant="h5" color={"black"}>Registos Pendentes da obra</Typography>
                </Box>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                    <TextField
                        label="Desde"
                        type="date"
                        value={initialDate || ''}
                        onChange={(e) => setInitialDate(e.target.value)}
                        InputLabelProps={{ shrink: true }}
                        sx={{ marginRight: 2 }}
                    />
                    <TextField
                        label="Até"
                        type="date"
                        value={endDate || ''}
                        onChange={(e) => setEndDate(e.target.value)}
                        InputLabelProps={{ shrink: true }}
                        sx={{ marginRight: 2 }}
                    />
                    <Button variant="contained" color="primary" onClick={handleFilterReset}>
                        Limpar Pesquisa
                    </Button>
                </Box>
                <TableContainer sx={{ backgroundColor: '#cccccc',  }}>
                    <Table sx={{ tableLayout: 'fixed' }}>
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
                                            <MRT_TableBodyCellValue
                                                cell={cell}
                                                table={table}
                                                staticRowIndex={rowIndex}
                                            />
                                        </TableCell>
                                    ))}
                                    <TableCell>
                                        <IconButton style={{ color: '#3547a1' }} title={"Aceitar"} onClick={() => handleAcceptOrRejectPendingRegister(row.original.id, row.original.uid, "completed")}>
                                            <CheckIcon sx={{ color: 'green' }}/>
                                        </IconButton>
                                        <IconButton style={{ color: '#c24242' }} title={"Rejeitar"} onClick={() => handleAcceptOrRejectPendingRegister(row.original.id, row.original.uid, "rejected")}>
                                            <CloseIcon sx={{ color: 'red' }}/>
                                        </IconButton>
                                    </TableCell>
                                </TableRow>
                            ))}
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
            </Stack>
        </>
    );
}