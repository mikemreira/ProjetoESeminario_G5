import {
    Box, CircularProgress,
    Stack,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    IconButton,
    Button,
    Typography, Avatar, Card, CardContent, Grid, Tabs, Tab,
} from '@mui/material';
import { useEffect, useState } from "react";
import { useCookies } from "react-cookie";
import * as React from "react";
import { Delete, Edit } from "@mui/icons-material";
import AddIcon from "@mui/icons-material/Add";
import { useParams, useNavigate } from "react-router-dom";
import {MRT_GlobalFilterTextField, MRT_TablePagination} from "material-react-table";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";

interface UserModel {
    id: number
    nome: string
    email: string
    morada: string
    func: string
    foto: string | null
}

interface UserOutputModel {
    users: UserModel[]
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

export default function ObraFuncionarios() {
    const [cookies] = useCookies(["token"]);
    const [users, setUsers] = useState<UserOutputModel>({ users: [] });
    const [loading, setLoading] = useState(true);
    const { oid } = useParams<{ oid: string }>();
    const navigate = useNavigate();
    const [tabIndex, setTabIndex] = useState(2);
    const handleTabChange = (event: React.ChangeEvent<{}>, newValue: number) => {
        setTabIndex(newValue);
    };
    const [obra, setObra] = useState<ObraOutputModel | null>(null);

    useEffect(() => {
        fetch(`/api/obras/${oid}/users`, {
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
               // console.log(JSON.stringify(body))
                setUsers(body)
                setLoading(false)
            }
        }).catch(error => {
            console.error("Error fetching registos: ", error)
        })
    }, [cookies.token, oid])

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

    const handleViewProfile = (uid: number) => {
        navigate(`/obras/${oid}/funcionarios/${uid}`)
    }

    const handleViewUserRecords = (uid: number) => {
        navigate(`/obras/${oid}/registers/${uid}`)
    }

    const handleViewAllRecords = () => {
        navigate(`/obras/${oid}/registers/all`)
    }

    const handleClickAddFuncionario = () => {
        navigate(`/obras/${oid}/funcionario/invite`)
    }

    const navigateVisaoGeral = () => {
        navigate(`/obras/${oid}`)
    }

    const navigateToRegisters = () => {
        navigate(`/obras/${oid}/registers`)
    }

    const navigateBack = () => {
        navigate("/obras")
    }

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
                            <Tab label="Visão Geral" onClick={navigateVisaoGeral} />
                            <Tab label="Registos" onClick={navigateToRegisters}/>
                            <Tab label="Membros" />
                        </Tabs>
                    </Grid>
                    <Grid item xs={12} md={8}>
                        <Stack sx={{ m: '5rem 0' }}>
                            <Box
                                sx={{
                                    display: 'flex',
                                    justifyContent: 'space-between',
                                    alignItems: 'center',
                                }}
                            >
                                <Typography variant="h4" color={"black"}>Membros da Obra</Typography>
                                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                    <Button
                                        variant="contained"
                                        color="primary"
                                        onClick={(e) => {
                                            e.stopPropagation()
                                            handleViewAllRecords()
                                        }}
                                        sx={{ mr: 2 }}
                                    >
                                        Ver todos os Registos
                                    </Button>
                                    <IconButton onClick={handleClickAddFuncionario} color="primary" sx={{
                                        bgcolor: 'primary.main',
                                        borderRadius: '40%',
                                        width: '40px',
                                        height: '40px',
                                        '&:hover': {
                                            bgcolor: 'primary.dark',
                                        },
                                    }} title={"Adicionar membro"}>
                                        <AddIcon sx={{ fontSize: 32, color: 'white' }}/>
                                    </IconButton>
                                </Box>
                            </Box>
                            <TableContainer sx={{ backgroundColor: '#cccccc', mt: 2 }}>
                                <Table sx={{ tableLayout: 'fixed' }}>
                                    <TableHead>
                                        <TableRow>
                                            <TableCell align="center"></TableCell>
                                            <TableCell align="left" sx={{ fontSize: '1.1rem' }}>Nome</TableCell>
                                            <TableCell align="left" sx={{ fontSize: '1.1rem' }}>Função</TableCell>
                                            <TableCell align="center" sx={{ fontSize: '1.1rem' }}>Registos</TableCell>
                                        </TableRow>
                                    </TableHead>
                                    <TableBody>
                                        {users.users.map((user) => (
                                            <TableRow key={user.id} onClick={() => handleViewProfile(user.id)} sx={{ cursor: 'pointer' }}>
                                                <TableCell align="center">
                                                    <Avatar
                                                        alt={user.nome}
                                                        src={user.foto || "https://cdn-icons-png.flaticon.com/512/3135/3135715.png"}
                                                        variant="rounded"
                                                        sx={{ width: "40%", height: "40%" }}
                                                    />
                                                </TableCell>
                                                <TableCell align="left" sx={{ fontSize: '1.1rem' }}>{user.nome}</TableCell>
                                                <TableCell align="left" sx={{ fontSize: '1.1rem' }}>{user.func}</TableCell>
                                                <TableCell align="center">
                                                    <Button
                                                        variant="contained"
                                                        color="primary"
                                                        onClick={(e) => {
                                                            e.stopPropagation()
                                                            handleViewUserRecords(user.id)
                                                        }}
                                                    >
                                                        Ver Registos
                                                    </Button>
                                                </TableCell>
                                            </TableRow>
                                        ))}
                                    </TableBody>
                                </Table>
                            </TableContainer>
                        </Stack>
                    </Grid>
                </Grid>
            </CardContent>
        </Card>
    );
};
