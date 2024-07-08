import {Box, Stack, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography} from "@mui/material";
import {
    flexRender,
    MRT_GlobalFilterTextField,
    MRT_TableBodyCellValue,
    MRT_TablePagination,
    MRT_ToolbarAlertBanner
} from "material-react-table";
import IconButton from "@mui/material/IconButton";
import AddIcon from "@mui/icons-material/Add";
import CheckIcon from "@mui/icons-material/Check";
import CloseIcon from "@mui/icons-material/Close";
import * as React from "react";

interface ObraRegistosOfAllPendingUsersFormProps {
    table: any;
    handleClickOpen: () => void;
    handleAcceptOrRejectPendingRegister: (id: number, uid: number, status: string) => void;
}

export default function ObraRegistosOfAllPendingUsersForm({
                                                                table,
                                                                handleClickOpen,
                                                                handleAcceptOrRejectPendingRegister,
                                                            }: ObraRegistosOfAllPendingUsersFormProps) {

    return (
        <>
            <Stack sx={{ m: '2rem 0' }}>
                <Box
                    sx={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                    }}
                >
                    <Typography variant="h5" color={"black"}>Registos Pendentes da obra</Typography>
                    <MRT_GlobalFilterTextField table={table} />
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
        </>
    );
}