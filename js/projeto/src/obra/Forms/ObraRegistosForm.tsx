import React, { useState} from "react";
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
import RegistoForm from "../../registos/RegistoForm";
import Menu from "@mui/material/Menu";
import {FilterList} from "react-admin";
import MenuItem from "@mui/material/MenuItem";
import FilterListIcon from '@mui/icons-material/FilterList';
import {useCookies} from "react-cookie";
import {Obra, RegistosOutputModel} from "../ObrasInfo";
import EditIcon from "@mui/icons-material/Edit";
import {Registo} from "../../registos/Registos";
import RegistoExitForm from "../../registos/RegistoExitForm";
import {path} from "../../App";

interface ObraRegistosFormProps {
    obra: Obra;
    handleClickOpenForm: () => void;
    handleCloseForm: (reload: boolean) => void;
    table: any;
    openForm: boolean;
    registo: RegistosOutputModel;
    setRegistos: (registos: RegistosOutputModel) => void;
}

export default function ObraRegistosForm({
     obra,
     handleClickOpenForm,
     handleCloseForm,
     table,
     openForm,
     registo,
     setRegistos
}: ObraRegistosFormProps) {
    const [cookies] = useCookies(["token"]);
    const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null)
    const [selectedFilter, setSelectedFilter] = useState<string>(`${path}/obras/${obra.oid}/registos/me`)
    const [title, setTitle] = useState<string>("Registos")
    const [exitOpenForm, setExitOpenForm] = useState(false);
    const [selectedRegisto, setSelectedRegisto] = useState<Registo | null>(null);

    const handleMenuOpen = (event: React.MouseEvent<HTMLButtonElement>) => {
        setAnchorEl(event.currentTarget)
    }
    const handleMenuClose = () => {
        setAnchorEl(null)
    }

    const handleFilterSelect = (filter: string, title: string) => {
        setSelectedFilter(filter)
        setTitle(title)
        handleMenuClose()
        fetch(filter, {
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
                setRegistos({
                    ...body,
                    meRoute: registo.meRoute,
                    pendingRoute: registo.pendingRoute,
                    allRoute: registo.allRoute,
                });
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
            handleFilterSelect(selectedFilter, title);
        }
    };

    const isMenuOpen = Boolean(anchorEl)

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
                    <Typography variant="h4" color={"black"}>{title}</Typography>
                    <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <IconButton onClick={handleMenuOpen} color="primary">
                            <FilterList  icon={<FilterListIcon/>} label={""} title={"Filtro"}/>
                        </IconButton>
                        <MRT_GlobalFilterTextField table={table} />
                        {obra.role === "admin" && (
                            <Menu
                                anchorEl={anchorEl}
                                open={isMenuOpen}
                                onClose={handleMenuClose}
                            >
                                <MenuItem onClick={() => handleFilterSelect(registo.allRoute, "Registos")}>Todos</MenuItem>
                                <MenuItem onClick={() => handleFilterSelect(registo.meRoute, "Registos Pessoais")}>Meus</MenuItem>
                                <MenuItem onClick={() => handleFilterSelect(registo.pendingRoute, "Registos Pendentes")}>Pendentes</MenuItem>
                            </Menu>
                        )}
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
                        <AddIcon sx={{ fontSize: 32, color: 'white' }} />
                    </IconButton>
                </Box>
                <TableContainer sx={{ backgroundColor: '#cccccc', mt: 2 }}>
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
                                                    header.getContext()
                                                )}
                                        </TableCell>
                                    ))}
                                </TableRow>
                            ))}
                        </TableHead>
                        <TableBody>
                            {table.getRowModel().rows.map((row, rowIndex) => (
                                <TableRow key={row.id} selected={row.getIsSelected()}>
                                    {row.getVisibleCells().map((cell) => (
                                        <TableCell align="center" variant="body" key={cell.id}>
                                            {cell.column.id === 'endTime' && (row.original.status === 'unfinished' || row.original.status === "unfinished_nfc") ? (
                                                <IconButton color="primary" title={"Finalizar"} onClick={() => handleClickExitOpenForm(row.original)}>
                                                    <EditIcon />
                                                </IconButton>
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