import { useEffect, useState } from "react";
import { useCookies } from "react-cookie";
import * as React from "react";
import {useTheme} from "@mui/material/styles";
import {Navigate, useNavigate, useParams} from "react-router-dom";
import {
    Card,
    CardContent,
    Typography,
    List,
    ListItem,
    ListItemText,
    CircularProgress,
    Grid,
    Avatar,
    Button,
    Box,
    Divider, Badge,
} from "@mui/material";
import IconButton from "@mui/material/IconButton";
import PendingActionsIcon from '@mui/icons-material/PendingActions';
import Tooltip from "@mui/material/Tooltip";


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

interface Registo {
    id: number;
    id_utilizador: number;
    id_obra: number;
    nome: string;
    entrada: DateObject;
    saida: DateObject | null;
    status: string | null;
}

interface RegistosOutputModel {
    registers: Registo[];
}

const formatDate = (dateObj: DateObject | null): string => {
    return dateObj ? dateObj.value$kotlinx_datetime : "Não definido";
}

export default function ObrasInfo() {
    const [cookies] = useCookies(["token"]);
    const [obra, setObra] = useState<Obra | null>(null);
    const { oid } = useParams<{ oid: string }>();
    const navigate = useNavigate();
    const [pendingRegisters, setPendingRegisters] = useState<RegistosOutputModel>({ registers: [] });

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
                console.log(body);
                setObra(body);
            })
            .catch((error) => {
                console.error("Error fetching obra:", error);
            });
    }, [cookies.token, oid]);

    useEffect(() => {
        fetch(`/api/obras/${oid}/registos/pendente`, {
            method: "GET",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`,
            },
        }).then((res) => {
            if (res.ok) {
                return res.json();
            } else {
                throw new Error('Failed to fetch registos pendentes');
            }
        }).then((body) => {
            console.log(body);
            setPendingRegisters(body);

        }).catch((error) => {
            console.error("Error fetching registos pendentes:", error);
        });
    }, [cookies.token, oid]);

    const handleClickRegistos = () => {
        navigate(`/obras/${oid}/registers`)
    }

    const handleClickFuncionarios = () => {
        navigate(`/obras/${oid}/funcionarios`)
    }

    const handleClickAddFuncionario = () => {
        navigate(`/obras/${oid}/funcionario/invite`)
    }

    const handleClickPendingRegisters = () => {
        navigate(`/obras/${oid}/registers/pending`);
    }

    if (!obra) {
        return <CircularProgress />;
    }

    return (
        <Card>
            <CardContent>
                <Typography variant="h4" component="h2" gutterBottom>
                    Informações da Obra
                </Typography>
                <Grid container spacing={2}>
                    <Grid item xs={12} md={4}>
                        <Avatar
                            alt={obra.name}
                            src={obra.foto || "https://t-obra.com/wp-content/uploads/2019/09/graca16.jpg"}
                            variant="rounded"
                            sx={{ width: "100%", height: "auto" }}
                        />
                    </Grid>
                    <Grid item xs={12} md={8}>
                        <Box display="flex" justifyContent="flex-end" alignItems="center">
                            <Tooltip title="Registos Pendentes">
                                <IconButton color="primary" onClick={handleClickPendingRegisters}>
                                    <Badge badgeContent={pendingRegisters.registers.length} color="warning">
                                        <PendingActionsIcon sx={{ color: 'black' }} />
                                    </Badge>
                                </IconButton>
                            </Tooltip>
                        </Box>
                        <List>
                            <ListItem>
                                <ListItemText primary="Nome" secondary={obra.name} primaryTypographyProps={{ style: { color: '#0000FF' } }} />
                            </ListItem>
                            <Divider />
                            <ListItem>
                                <ListItemText primary="Localização" secondary={obra.location} primaryTypographyProps={{ style: { color: '#0000FF' } }} />
                            </ListItem>
                            <Divider />
                            <ListItem>
                                <ListItemText primary="Descrição" secondary={obra.description} primaryTypographyProps={{ style: { color: '#0000FF' } }} />
                            </ListItem>
                            <Divider />
                            <ListItem>
                                <ListItemText primary="Data de Início" secondary={formatDate(obra.startDate)} primaryTypographyProps={{ style: { color: '#0000FF' } }}/>
                            </ListItem>
                            <Divider />
                            <ListItem>
                                <ListItemText primary="Data de Fim" secondary={formatDate(obra.endDate)} primaryTypographyProps={{ style: { color: '#0000FF' } }}/>
                            </ListItem>
                            <Divider />
                            <ListItem>
                                <ListItemText primary="Status" secondary={obra.status} primaryTypographyProps={{ style: { color: '#0000FF' } }}/>
                            </ListItem>
                            <Divider />
                            <ListItem>
                                <ListItemText primary="Função" secondary={obra.function} primaryTypographyProps={{ style: { color: '#0000FF' } }}/>
                            </ListItem>
                        </List>
                        <Box mt={2}>
                            <Button variant="contained" color="primary" sx={{ mr: 2 }} onClick={() => handleClickRegistos()}>
                                Ver Registos
                            </Button>
                            {obra.role === "admin" && (
                                <Button variant="contained" color="secondary" sx={{ mr: 2 }} onClick={() => handleClickFuncionarios()}>
                                    Ver Funcionários
                                </Button>
                            )}
                            {obra.role === "admin" && (
                                <Button variant="contained" color="secondary" onClick={() => handleClickAddFuncionario()}>
                                    Adicionar Funcionário
                                </Button>
                            )}
                        </Box>
                    </Grid>
                </Grid>
            </CardContent>
        </Card>
    );
}