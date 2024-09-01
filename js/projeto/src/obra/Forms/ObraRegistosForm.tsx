import React, {useEffect, useState} from "react";
import {
    Box, CircularProgress,
    FormControl, InputLabel, Select,
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
import {Obra, pageSize, UserRegistersAndObraOutputModel} from "../ObrasInfo";
import EditIcon from "@mui/icons-material/Edit";
import {Registo} from "../../registos/Registos";
import RegistoExitForm from "../../registos/RegistoExitForm";
import {path} from "../../App";
import DeleteIcon from "@mui/icons-material/Delete";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import KeyboardDoubleArrowLeftIcon from "@mui/icons-material/KeyboardDoubleArrowLeft";
import ArrowBackIosIcon from "@mui/icons-material/ArrowBackIos";
import ArrowForwardIosIcon from "@mui/icons-material/ArrowForwardIos";
import KeyboardDoubleArrowRightIcon from "@mui/icons-material/KeyboardDoubleArrowRight";

interface ObraRegistosFormProps {
    obra: Obra;
    handleClickOpenForm: () => void;
    handleCloseForm: (reload: boolean) => void;
    table: any;
    openForm: boolean;
    registo: UserRegistersAndObraOutputModel;
    setRegistos: (registos: UserRegistersAndObraOutputModel) => void;
    totalPages: number;
    setTotalPages: (totalpages: number) => void;
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

export default function ObraRegistosForm({
     obra,
     handleClickOpenForm,
     handleCloseForm,
     table,
     openForm,
     registo,
     setRegistos,
     totalPages,
     setTotalPages,
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
}: ObraRegistosFormProps) {
    const [cookies] = useCookies(["token"]);
    const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null)
    const [selectedFilter, setSelectedFilter] = useState<string>(`${path}/obras/${obra.oid}/registos`)
    const [filter, setFilter] = useState<'all' | 'me' | 'pending' | 'unfinished'>("all")
    const [title, setTitle] = useState<string>("Registos")
    const [exitOpenForm, setExitOpenForm] = useState(false);
    const [selectedRegisto, setSelectedRegisto] = useState<Registo | null>(null);
    const [loading, setLoading] = useState(false);

    const handleMenuOpen = (event: React.MouseEvent<HTMLButtonElement>) => {
        setAnchorEl(event.currentTarget)
    }
    const handleMenuClose = () => {
        setAnchorEl(null)
    }

    const handleFilterChange = (event: { target: { value: any; }; }) => {
        const selectedValue = event.target.value;
        let filter, title;
        switch (selectedValue) {
            case 'all':
                setFilter('all');
                filter = registo.allRoute;
                title = 'Registos';
                break;
            case 'me':
                setFilter('me');
                filter = registo.meRoute;
                title = 'Registos Pessoais';
                break;
            case 'pending':
                setFilter('pending');
                filter = registo.pendingRoute;
                title = 'Registos Pendentes';
                break;
            case 'unfinished':
                setFilter('unfinished');
                filter = registo.unfinishedRoute;
                title = 'Registos Incompletos';
                break;
            default:
                setFilter('all');
                filter = registo.allRoute;
                title = 'Registos';
        }
        setSelectedFilter(filter);
        setTitle(title);
        handleFilterSelect(filter, title, 1);
    };


    const handleFilterSelect = (filter: string, title: string, pageNumber: number) => {
        setLoading(true)
        setSelectedFilter(filter)
        setTitle(title)
        handleMenuClose()
        const params = new URLSearchParams({ page: String(pageNumber) });
        if (initialDate) params.append("initialDate", initialDate);
        if (endDate) params.append("endDate", endDate);
        const queryString = params.toString();
        const fetchUrl = `${filter}?${queryString}`;
        console.log('Fetching registers from URL:', fetchUrl);
        fetch(fetchUrl, {
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
                setRegistos((prevState: UserRegistersAndObraOutputModel): UserRegistersAndObraOutputModel => {
                    return {
                        ...prevState,
                        registers: body.registers,
                        registersSize: body.registersSize ?? prevState.registersSize,
                    };
                });
                const newTotalPages = Math.ceil(body.registersSize / pageSize)
                setTotalPages(newTotalPages)
            }
        }).catch(error => {
            console.error("Error fetching registos: ", error)
        }).finally(() => {
            setLoading(false)
        })
    }

    const handleClickDeleteRegister = (registo: Registo) => {
        fetch(`${path}/obras/${registo.oid}/registos/${registo.id}`, {
            method: "DELETE",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`
            },
        }).then((res) => {
            if (res.ok) {
                handleFilterSelect(selectedFilter, title, page)
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
            handleFilterSelect(selectedFilter, title, page);
        }
    };

    const isMenuOpen = Boolean(anchorEl)

    useEffect(() => {
        handleFilterSelect(selectedFilter, title, page);
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
                    <Typography variant="h4" color={"black"}>{title}</Typography>
                    <IconButton onClick={handleClickOpenForm} color="primary" title={"Adicionar registo"} sx={{
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
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                    {obra.role === "admin" && (
                        <>
                            <FormControl variant="outlined" sx={{ marginRight: 2, minWidth: 120 }}>
                                <InputLabel>Filtro</InputLabel>
                                <Select
                                    value={filter}
                                    onChange={handleFilterChange}
                                    label="Filtro"
                                >
                                    <MenuItem value="all">Todos</MenuItem>
                                    <MenuItem value="me">Meus</MenuItem>
                                    <MenuItem value="pending">Pendentes</MenuItem>
                                    <MenuItem value="unfinished">Incompletos</MenuItem>
                                </Select>
                            </FormControl>
                        </>
                    )}
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
                                                    header.getContext()
                                                )}
                                        </TableCell>
                                    ))}
                                </TableRow>
                            ))}
                        </TableHead>
                        <TableBody>
                            {loading ? (
                                <TableRow>
                                    <TableCell colSpan={table.getHeaderGroups().flatMap((headerGroup: { headers: any; }) => headerGroup.headers).length} align="center">
                                        <CircularProgress />
                                    </TableCell>
                                </TableRow>
                            ) : table.getRowModel().rows.length === 0 ? (
                                <TableRow>
                                    <TableCell
                                        colSpan={table.getHeaderGroups().flatMap((headerGroup: { headers: any; }) => headerGroup.headers).length}
                                        align="center"
                                    >
                                        Não existem registos.
                                    </TableCell>
                                </TableRow>
                            ) : (
                                table.getRowModel().rows.map((row: { id: React.Key | null | undefined; getIsSelected: () => boolean | undefined; getVisibleCells: () => any[]; original: Registo; }, rowIndex: number | undefined) => (
                                    <TableRow key={row.id} selected={row.getIsSelected()}>
                                        {row.getVisibleCells().map((cell) => (
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
                                ))
                            )}
                        </TableBody>
                    </Table>
                </TableContainer>
                <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2 }}>
                    { totalPages > 0 && (
                        <>
                            <IconButton onClick={handleFirstPage} disabled={page === 1} title={"Retroceder tudo"}>
                                <KeyboardDoubleArrowLeftIcon />
                            </IconButton>
                            <IconButton onClick={handlePreviousPage} disabled={page === 1} title={"Retroceder"}>
                                <ArrowBackIosIcon />
                            </IconButton>
                            {[...Array(3)].map((_, index) => {
                                const startPage = Math.max(1, Math.min(page - 1, totalPages - 2));
                                const currentPage = startPage + index;
                                if (currentPage <= totalPages) {
                                    return (
                                        <Button
                                            key={currentPage}
                                            onClick={() => handlePageChange(currentPage)}
                                            variant={page === currentPage ? "contained" : "outlined"}
                                            sx={{
                                                minWidth: '40px',
                                                minHeight: '40px',
                                                borderRadius: '50%',
                                                mx: 1
                                            }}
                                        >
                                            {currentPage}
                                        </Button>
                                    );
                                }
                                return null;
                            })}
                            <IconButton onClick={handleNextPage} disabled={page === totalPages} title={"Avançar"}>
                                <ArrowForwardIosIcon />
                            </IconButton>
                            <IconButton onClick={handleLastPage} disabled={page === totalPages} title={"Avançar tudo"}>
                                <KeyboardDoubleArrowRightIcon />
                            </IconButton>
                        </>
                    )}
                </Box>
                <RegistoForm open={openForm} onHandleClose={handleCloseForm} obra={obra}/>
                <RegistoExitForm open={exitOpenForm} onHandleClose={handleExitCloseForm} registo={selectedRegisto}/>
            </Stack>
        </>
    );
}