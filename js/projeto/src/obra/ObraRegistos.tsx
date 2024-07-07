import {
    MRT_GlobalFilterTextField,
    MRT_TableBodyCellValue,
    MRT_TablePagination,
    MRT_ToolbarAlertBanner,
    flexRender,
    useMaterialReactTable, MRT_Row,
} from 'material-react-table';
import {
    Avatar,
    Box, Card, CardContent, CircularProgress, Grid,
    Stack, Tab,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow, Tabs,
    Typography,
} from '@mui/material';
import { useEffect, useState } from "react";
import { useCookies } from "react-cookie";
import * as React from "react";
import {Delete, Edit} from "@mui/icons-material";
import IconButton from "@mui/material/IconButton";
import Button from "@mui/material/Button";
import AddIcon from "@mui/icons-material/Add";
import {useLocation, useNavigate, useParams, useSearchParams} from "react-router-dom";
import RegistoForm from "../registos/RegistoForm";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";

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

interface Obra {
    oid: number;
    name: string;
    location: string;
    description: string;
    startDate: DateObject | null;
    endDate: DateObject | null;
    status: string;
    role: string;
    foto: string | null;
    function: string;
}

interface ObraOutputModel {
    constructionAndRoleOutputModel: Obra
    usersRoute: string
    registersRoute: string
    editRoute: string
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
    const navigate = useNavigate();
    const [openForm, setOpenForm] = useState(false);
    const [tabIndex, setTabIndex] = useState(1);
    const handleTabChange = (event: React.ChangeEvent<{}>, newValue: number) => {
        setTabIndex(newValue);
    };
    const [obra, setObra] = useState<ObraOutputModel | null>(null);

    useEffect(() => {
        const page = searchParams.get('page') || '0'
        fetch(`/api/obras/${oid}/registos/me?page=${page}`, {
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

    useEffect(() => {
        fetch(`/api/obras/${oid}`, {
            method: "GET",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`,
            },
        })
            .then((res) => {
                if (res.ok) {
                    return res.json();
                } else {
                    throw new Error('Failed to fetch obra');
                }
            })
            .then((body) => {
                console.log("Obra fetched:", body);
                setObra(body);
            })
            .catch((error) => {
                console.error("Error fetching obra:", error);
            });
    }, [cookies.token, oid]);

    const handleClickOpen = () => {
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
    };

    const handleClickOpenForm = () => {
        setOpenForm(true);
    };

    const handleCloseForm = () => {
        setOpenForm(false);
    };

    const navigateToVisaoGeral = () => {
        navigate(`/obras/${oid}`)
    }

    const navigateToMembers = () => {
        navigate(`/obras/${oid}/funcionarios`)
    }

    const navigateBack = () => {
        navigate("/obras")
    }


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

    if (loading || !obra) return <CircularProgress />
    else return (
        <Card>
            <CardContent>
                <Box display="flex" justifyContent="flex-start">
                    <IconButton onClick={navigateBack} title={"Voltar"}>
                        <ArrowBackIcon />
                    </IconButton>
                </Box>
                <Grid container spacing={2}>
                    <Grid item xs={12} md={4} sx={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-center' }}>
                        <Typography variant="h4" color={"black"} sx={{ alignSelf: 'flex-start', mb: 2 }}>
                            {obra.constructionAndRoleOutputModel.name}
                        </Typography>
                        <Avatar
                            alt={obra.constructionAndRoleOutputModel.name}
                            src={obra.constructionAndRoleOutputModel.foto || "https://t-obra.com/wp-content/uploads/2019/09/graca16.jpg"}
                            variant="rounded"
                            sx={{
                                width: "40%",
                                height: "40%",
                                mb: 2,
                            }}
                        />
                        <Tabs
                            value={tabIndex}
                            onChange={handleTabChange}
                            indicatorColor="primary"
                            textColor="primary"
                            orientation="vertical"
                        >
                            <Tab label="Visão Geral" onClick={navigateToVisaoGeral} />
                            <Tab label="Registos" />
                            <Tab label="Membros" onClick={navigateToMembers}/>
                        </Tabs>
                    </Grid>
                    <Grid item xs={12} md={8}>
                        <Stack sx={{ m: '2rem 0' }}>
                            <Box
                                sx={{
                                    display: 'flex',
                                    justifyContent: 'space-between',
                                    alignItems: 'center',
                                }}
                            >
                                <Typography variant="h4" color={"black"}>Registos</Typography>
                                <MRT_GlobalFilterTextField table={table} />
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
                            <TableContainer sx={{ backgroundColor: '#cccccc', }}>
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
                                                <TableCell />
                                            </TableRow>
                                        ))}
                                    </TableHead>
                                    <TableBody>
                                        {table.getRowModel().rows.map((row, rowIndex) => (
                                            <TableRow key={row.id} selected={row.getIsSelected()}>
                                                {row.getVisibleCells().map((cell) => (
                                                    <TableCell align="center" variant="body" key={cell.id}>
                                                        <MRT_TableBodyCellValue
                                                            cell={cell}
                                                            table={table}
                                                            staticRowIndex={rowIndex}
                                                        />
                                                    </TableCell>
                                                ))}
                                                <TableCell>
                                                    <IconButton style={{ color: '#3547a1' }} onClick={() => handleEdit()}>
                                                        <Edit />
                                                    </IconButton>
                                                    <IconButton style={{ color: '#c24242' }} onClick={() => handleDelete(row.original)}>
                                                        <Delete />
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
                            <RegistoForm open={openForm} onHandleClose={handleCloseForm} onHandleOpen={handleClickOpenForm} />
                        </Stack>
                    </Grid>
                </Grid>
            </CardContent>
        </Card>
    );
};
