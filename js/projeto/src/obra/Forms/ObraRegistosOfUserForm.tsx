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
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import {Registo} from "../../registos/Registos";
import {path} from "../../App";
import {useCookies} from "react-cookie";
import RegistoForm from "../../registos/RegistoForm";
import RegistoExitForm from "../../registos/RegistoExitForm";
import {useState} from "react";
import {Obra} from "../ObrasInfo";

interface ObraRegistosOfUserFormProps {
    obra: Obra;
    table: any;
    username: string;
    handleGetUserRegisters: (uid: number) => void;
    handleCloseForm: (reload: boolean) => void;
    openForm: boolean;
}

export default function ObraRegistosOfUserForm({
    obra,
    table,
    username,
    handleGetUserRegisters,
    handleCloseForm,
    openForm
}: ObraRegistosOfUserFormProps) {
    const [cookies] = useCookies(["token"]);
    const [exitOpenForm, setExitOpenForm] = useState(false);
    const [selectedRegisto, setSelectedRegisto] = useState<Registo | null>(null);

    const handleClickDeleteRegister = (registo: Registo) => {
        console.log(registo)
        fetch(`${path}/obras/${registo.oid}/registos/${registo.id}`, {
            method: "DELETE",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`
            },
        }).then((res) => {
            if (res.ok) {
                // @ts-ignore
                handleGetUserRegisters(registo.uid)
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
            // @ts-ignore
            handleGetUserRegisters(selectedRegisto.id_utilizador)
        }
    };

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
                                </TableRow>
                            ))}
                        </TableHead>
                        <TableBody>
                            {table.getRowModel().rows.map((row, rowIndex) => (
                                <TableRow key={row.id} selected={row.getIsSelected()}>
                                    {row.getVisibleCells().map((cell, _columnIndex) => (
                                        <TableCell align="center" variant="body" key={cell.id}>
                                            {cell.column.id === 'endTime' && (row.original.status === 'unfinished' || row.original.status === "unfinished_nfc") ? (
                                                <>
                                                    <IconButton color="primary" title={"Finalizar"} onClick={() => handleClickExitOpenForm(row.original)}>
                                                        <EditIcon />
                                                    </IconButton>
                                                    <IconButton color="error" title={"Eliminar"} onClick={() => handleClickDeleteRegister(row.original)}>
                                                        <DeleteIcon />
                                                    </IconButton>
                                                </>
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
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
                <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 2 }}>
                    <MRT_TablePagination table={table} />
                </Box>
                <MRT_ToolbarAlertBanner stackAlertBanner table={table} />
                <RegistoForm open={openForm} onHandleClose={handleCloseForm} obra={obra}/>
                <RegistoExitForm open={exitOpenForm} onHandleClose={handleExitCloseForm} registo={selectedRegisto}/>
            </Stack>
        </>
    );
}