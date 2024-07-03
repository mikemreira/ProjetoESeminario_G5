import {ChangeEvent, useEffect, useState} from "react";
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
    Divider, Badge, InputLabel, Select, MenuItem,
} from "@mui/material";
import IconButton from "@mui/material/IconButton";
import PendingActionsIcon from '@mui/icons-material/PendingActions';
import Tooltip from "@mui/material/Tooltip";
import TextField from "@mui/material/TextField";
import RemoveCircleOutlineIcon from '@mui/icons-material/RemoveCircleOutline';
import ClearIcon from '@mui/icons-material/Clear';
import ArrowCircleUpIcon from '@mui/icons-material/ArrowCircleUp';

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
    registers: Registo[]
}

const formatDate = (dateObj: DateObject | null | undefined): string => {
    if (!dateObj || !dateObj.value$kotlinx_datetime) {
        return "";
    }
    return dateObj.value$kotlinx_datetime.split('T')[0];
}
const parseDate = (dateStr: string): DateObject | null => {
    if (!dateStr) return null
    const date = new Date(dateStr)
    if (isNaN(date.getTime())) {
        return null
    }
    return {
        year: date.getFullYear(),
        dayOfMonth: date.getDate(),
        month: date.toLocaleString('default', { month: 'long' }),
        dayOfWeek: date.toLocaleString('default', { weekday: 'long' }),
        dayOfYear: Math.ceil((date.getTime() - new Date(date.getFullYear(), 0, 0).getTime()) / 86400000),
        monthNumber: date.getMonth() + 1,
        value$kotlinx_datetime: date.toISOString(),
    }
}

const func = [
    'Ajudante', 'Apontador', 'Armador de ferro', 'Arvorado', 'Calceteiro', 'Canalisador', 'Carpinteiro', 'Chefe de equipa', 'Condutor Manobrador', 'Diretor de serviços', 'Eletricista', 'Encarregado', 'Escriturário', 'Estucador',  'Ferramenteiro', 'Gruista', 'Impermiabilizador', 'Ladrilhador', 'Marteleiro', 'Montador de andaimes', 'Pedreiro', 'Pintor', 'Serralheiro', 'Servente', 'Soldador', 'Técnico de manutenção', 'Tubista', 'Outro'
]

export default function ObrasInfo() {
    const [cookies] = useCookies(["token"]);
    const [obra, setObra] = useState<Obra | null>(null);
    const { oid } = useParams<{ oid: string }>();
    const navigate = useNavigate();
    const [pendingRegisters, setPendingRegisters] = useState<RegistosOutputModel>({ registers: [] });
    const [isEditing, setIsEditing] = useState(false);
    const [editedObra, setEditedObra] = useState<Obra | null>(null);

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
                setEditedObra(body);
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

    const handleClickEditObra = () => {
        setIsEditing(true)
    }

    const handleCancelEdit = () => {
        setEditedObra(obra)
        setIsEditing(false)
    }

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target
        setEditedObra((prevObra) => ({
            ...prevObra!,
            [name]: name.includes('Date') ? parseDate(value) : value,
        }))
    }

    const handleSelectChange = (event: ChangeEvent<{ name?: string; value: unknown }>) => {
        const { name, value } = event.target
        setEditedObra((prevObra) => ({
            ...prevObra!,
            [name as string]: value,
        }))
    }


    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0]
        if (file) {
            const reader = new FileReader()
            reader.onloadend = () => {
                setEditedObra((prevObra) => ({
                    ...prevObra!,
                    foto: reader.result as string | null,
                }))
            }
            reader.readAsDataURL(file)
        }
    }

    const handleSaveObra = () => {
        console.log(editedObra)
        if (editedObra) {
            const editedObraForUpdate = {
                ...editedObra,
                startDate: editedObra.startDate?.value$kotlinx_datetime || null,
                endDate: editedObra.endDate?.value$kotlinx_datetime || null,
            };
            fetch(`/api/obras/${oid}`, {
                method: "PUT",
                headers: {
                    "Content-type": "application/json",
                    "Authorization": `Bearer ${cookies.token}`,
                },
                body: JSON.stringify(editedObraForUpdate),
            })
                .then((res) => {
                    if (res.ok) {
                        setObra(editedObra)
                        setIsEditing(false)
                    } else {
                        console.error("Failed to update obra")
                    }
                })
                .catch((error) => {
                    console.error("Error updating profile:", error)
                })
        }
    }

    const handleSuspendOrRecover = () => {
        if (!obra) return
        const updatedObra = {
            ...obra,
            startDate: obra.startDate?.value$kotlinx_datetime || null,
            endDate: obra.endDate?.value$kotlinx_datetime || null,
            status: obra.status === "on going" ? "recoverable" : "on going",
        };
        fetch(`/api/obras/${oid}`, {
            method: "PUT",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`,
            },
            body: JSON.stringify(updatedObra),
        })
            .then((res) => {
                if (res.ok) {
                    setObra((prevObra) => ({
                        ...prevObra!,
                        status: updatedObra.status,
                    }));
                } else {
                    console.error("Failed to update obra")
                }
            })
            .catch((error) => {
                console.error("Error updating profile:", error)
            })
    }


    if (!obra) {
        return <CircularProgress />;
    }
    // @ts-ignore
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
                            sx={{
                                width: "100%",
                                height: "100%",
                                cursor: isEditing ? "pointer" : "default",
                            }}
                            onClick={isEditing ? () => document.getElementById('contained-button-file')?.click() : undefined}
                        />
                        {isEditing && (
                            <input
                                accept="image/*"
                                style={{ display: 'none' }}
                                id="contained-button-file"
                                type="file"
                                onChange={handleFileChange}
                            />
                        )}
                    </Grid>
                    <Grid item xs={12} md={8}>
                        {obra.role === "admin" && !isEditing && (obra.status === "on going" || obra.status === "completed") && (
                            <Box display="flex" justifyContent="flex-end" alignItems="center">
                                <Tooltip title="Registos Pendentes">
                                    <IconButton color="primary" onClick={handleClickPendingRegisters}>
                                        <Badge badgeContent={pendingRegisters.registers.length} color="warning">
                                            <PendingActionsIcon sx={{ color: 'black' }} />
                                        </Badge>
                                    </IconButton>
                                </Tooltip>
                            </Box>
                        )}
                        <List>
                            <ListItem>
                                <ListItemText primary="Nome" secondary={
                                    isEditing ? (
                                        <TextField
                                            fullWidth
                                            name="name"
                                            value={editedObra?.name || ""}
                                            onChange={handleChange}
                                        />
                                    ) : (
                                        obra.name
                                    )
                                } primaryTypographyProps={{ style: { color: '#0000FF' } }} />
                            </ListItem>
                            <Divider />
                            <ListItem>
                                <ListItemText primary="Localização" secondary={
                                    isEditing ? (
                                        <TextField
                                            fullWidth
                                            name="location"
                                            value={editedObra?.location || ""}
                                            onChange={handleChange}
                                        />
                                    ) : (
                                        obra.location
                                    )
                                } primaryTypographyProps={{ style: { color: '#0000FF' } }} />
                            </ListItem>
                            <Divider />
                            <ListItem>
                                <ListItemText primary="Descrição" secondary={
                                    isEditing ? (
                                        <TextField
                                            fullWidth
                                            name="description"
                                            value={editedObra?.description || ""}
                                            onChange={handleChange}
                                        />
                                    ) : (
                                        obra.description
                                    )
                                } primaryTypographyProps={{ style: { color: '#0000FF' } }} />
                            </ListItem>
                            <Divider />
                            <ListItem>
                                <ListItemText primary="Data de Início" secondary={
                                    isEditing ? (
                                        <TextField
                                            fullWidth
                                            name="startDate"
                                            type={"date"}
                                            value={formatDate(editedObra?.startDate)|| ""}
                                            onChange={handleChange}
                                        />
                                    ) : (
                                        formatDate(obra.startDate)
                                    )
                                } primaryTypographyProps={{ style: { color: '#0000FF' } }}/>
                            </ListItem>
                            <Divider />
                            <ListItem>
                                <ListItemText primary="Data de Fim" secondary={
                                    isEditing ? (
                                        <TextField
                                            fullWidth
                                            name="endDate"
                                            type={"date"}
                                            value={formatDate(editedObra?.endDate) || ""}
                                            onChange={handleChange}
                                        />
                                    ) : (
                                        formatDate(obra.endDate)
                                    )
                                } primaryTypographyProps={{ style: { color: '#0000FF' } }}/>
                            </ListItem>
                            <Divider />
                            <ListItem>
                                <ListItemText primary="Status" secondary={
                                        obra.status
                                } primaryTypographyProps={{ style: { color: '#0000FF' } }}/>
                            </ListItem>
                            <Divider />
                            <ListItem>
                                <ListItemText primary="Função" secondary={
                                    isEditing ? (
                                        <>
                                            <InputLabel id="function-label">Função</InputLabel>
                                            <Select
                                                labelId="function-label"
                                                id="function"
                                                name="function"
                                                value={editedObra?.function || ""}
                                                onChange={handleSelectChange}
                                                required
                                                fullWidth
                                            >
                                                {func.map((role, index) => (
                                                    <MenuItem key={index} value={role}>
                                                        {role}
                                                    </MenuItem>
                                                ))}
                                            </Select>
                                        </>
                                    ) : (
                                        obra.function
                                    )
                                } primaryTypographyProps={{ style: { color: '#0000FF' } }}/>
                            </ListItem>
                        </List>
                        <Box mt={2}>
                            {!isEditing && (
                                <>
                                    <Button variant="contained" color="primary" onClick={handleClickRegistos}>
                                        Registos
                                    </Button>
                                    {obra.role === "admin" && (
                                        <Button variant="contained" color="secondary" sx={{ ml: 2 }} onClick={handleClickFuncionarios}>
                                            Funcionários
                                        </Button>
                                    )}
                                    {obra.role === "admin" && (obra.status === "on going" || obra.status === "completed") && (
                                        <>
                                            <Button variant="contained" color="secondary" sx={{ ml: 2 }} onClick={handleClickAddFuncionario}>
                                                Adicionar Funcionário
                                            </Button>
                                            <Button variant="contained" color="secondary" sx={{ ml: 2 }} onClick={handleClickEditObra}>
                                                Editar
                                            </Button>
                                        </>
                                    )}
                                    {obra.role === "admin" && (obra.status === "on going" || obra.status === "completed") && (
                                        <IconButton variant="contained" sx={{ ml: 2 }} onClick={handleSuspendOrRecover} title="Suspender">
                                            <ClearIcon sx={{ color: 'red' }}/>
                                        </IconButton>
                                    )}
                                    {obra.role === "admin" && (obra.status === "recoverable") && (
                                        <IconButton variant="contained" sx={{ ml: 2 }} onClick={handleSuspendOrRecover} title="Recuperar">
                                            <ArrowCircleUpIcon sx={{ color: 'green' }}/>
                                        </IconButton>
                                    )}
                                </>
                            )}
                            {isEditing && obra.role === "admin" && (
                                <>
                                    <Button variant="contained" color="primary" onClick={handleSaveObra} sx={{ marginRight: 1 }}>
                                        Guardar alterações
                                    </Button>
                                    <Button variant="contained" color="error" onClick={handleCancelEdit}>
                                        Cancelar
                                    </Button>
                                </>
                            )}
                        </Box>
                    </Grid>
                </Grid>
            </CardContent>
        </Card>
    );
}