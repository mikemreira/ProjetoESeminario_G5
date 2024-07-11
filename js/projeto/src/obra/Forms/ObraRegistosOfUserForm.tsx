import {Box, Stack, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography} from "@mui/material";
import {
    flexRender,
    MRT_GlobalFilterTextField,
    MRT_TableBodyCellValue,
    MRT_TablePagination,
    MRT_ToolbarAlertBanner
} from "material-react-table";
import IconButton from "@mui/material/IconButton";
import {Delete, Edit} from "@mui/icons-material";
import * as React from "react";

interface ObraRegistosOfUserFormProps {
    table: any;
    handleClickOpen: () => void;
    username: string;
}

export default function ObraRegistosOfUserForm({
                                                    table,
                                                    handleClickOpen,
                                                    username,
                                                }: ObraRegistosOfUserFormProps) {

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
                    <Typography variant="h5" color={"black"}>Registos de {username}</Typography>
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
                                        <IconButton style={{ color: '#3547a1' }}>
                                            <Edit/>
                                        </IconButton>
                                        <IconButton style={{ color: '#c24242' }} >
                                            <Delete/>
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