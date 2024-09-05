import {
    Box,
    Stack,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Typography
} from "@mui/material";
import {
    flexRender,
    MRT_TableBodyCellValue,
    MRT_ToolbarAlertBanner
} from "material-react-table";
import IconButton from "@mui/material/IconButton";
import * as React from "react";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import {Registo} from "../../registos/Registos";
import {path} from "../../App";
import {useCookies} from "react-cookie";
import RegistoForm from "../../registos/RegistoForm";
import RegistoExitForm from "../../registos/RegistoExitForm";
import {useEffect, useState} from "react";
import {Obra} from "../ObrasInfo";
import Button from "@mui/material/Button";
import TextField from "@mui/material/TextField";
import {Pagination} from "../../Utils";

interface ObraRegistosOfUserFormProps {
    obra: Obra;
    selectedUser: number;
    table: any;
    username: string;
    handleGetUserRegisters: (pageNumber: number, uid: number) => void;
    handleCloseForm: (reload: boolean) => void;
    openForm: boolean;
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

}

export default function ObraRegistosOfUserForm({
    obra,
    selectedUser,
    table,
    username,
    handleGetUserRegisters,
    handleCloseForm,
    openForm,
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
    setEndDate
}: ObraRegistosOfUserFormProps) {
    const [cookies] = useCookies(["token"]);
    const [exitOpenForm, setExitOpenForm] = useState(false);
    const [selectedRegisto, setSelectedRegisto] = useState<Registo | null>(null);

    useEffect(() => {
        handleGetUserRegisters(page, selectedUser);
    }, [page, initialDate, endDate]);

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
                        mb: 2
                    }}
                >
                    <Typography variant="h5" color={"black"}>Registos de {username}</Typography>
                </Box>
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
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
                <TableContainer sx={{ backgroundColor: '#cccccc', mt: 2 }}>
                    <Table sx={{ tableLayout: 'fixed' }}>
                        <TableHead>
                            {table.getHeaderGroups().map((headerGroup: { id: React.Key | null | undefined; headers: any[]; }) => (
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
                            {table.getRowModel().rows.map((row: { id: React.Key | null | undefined; getIsSelected: () => boolean | undefined; getVisibleCells: () => any[]; original: Registo; }, rowIndex: number | undefined) => (
                                <TableRow key={row.id} selected={row.getIsSelected()}>
                                    {row.getVisibleCells().map((cell, _columnIndex) => (
                                        <TableCell align="center" variant="body" key={cell.id}>
                                            {cell.column.id === 'endTime' && (row.original.status === 'Incompleto' || row.original.status === "Incompleto via NFC") ? (
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
                <Pagination
                    totalPages={totalPages}
                    page={page}
                    handleFirstPage={handleFirstPage}
                    handlePreviousPage={handlePreviousPage}
                    handlePageChange={handlePageChange}
                    handleNextPage={handleNextPage}
                    handleLastPage={handleLastPage}
                />
                <MRT_ToolbarAlertBanner stackAlertBanner table={table} />
                <RegistoForm open={openForm} onHandleClose={handleCloseForm} obra={obra}/>
                <RegistoExitForm open={exitOpenForm} onHandleClose={handleExitCloseForm} registo={selectedRegisto}/>
            </Stack>
        </>
    );
}