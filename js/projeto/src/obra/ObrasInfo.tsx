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
    Tabs,
    Tab
} from "@mui/material";
import IconButton from "@mui/material/IconButton";
import PendingActionsIcon from '@mui/icons-material/PendingActions';
import Tooltip from "@mui/material/Tooltip";
import TextField from "@mui/material/TextField";
import RemoveCircleOutlineIcon from '@mui/icons-material/RemoveCircleOutline';
import ClearIcon from '@mui/icons-material/Clear';
import ArrowCircleUpIcon from '@mui/icons-material/ArrowCircleUp';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';

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
    oid: number ;
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

interface states {
    vale: string
}

export default function ObrasInfo() {
    const [cookies] = useCookies(["token"]);
    const [obra, setObra] = useState<ObraOutputModel | null>(null);
    const { oid } = useParams<{ oid: string }>();
    const navigate = useNavigate();
    const [pendingRegisters, setPendingRegisters] = useState<RegistosOutputModel>({ registers: [] });
    const [isEditing, setIsEditing] = useState(false);
    const [editedObra, setEditedObra] = useState<ObraOutputModel | null>(null);
    const [tabIndex, setTabIndex] = useState(0);
    const [registos, setRegistos] = useState<Registo[]>([])
    const [state, setState] = useState<states | null>(null)


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

    const teste = () => {
            fetch(`/api/obras/${oid}/registos/me`, {
                method: "GET",
                headers: {
                    "Content-type": "application/json",
                    "Authorization": `Bearer ${cookies.token}`
                },
            }).then((res) => {
                console.log(oid)
                console.log(status)
                setState("registo")
                if (res.ok) return res.json()
                else return null
            }).then((body) => {
                if (body) {
                    setRegistos(body.registers)
                    console.log(JSON.stringify(body))
                }
            }).catch(error => {
                console.error("Error fetching registos: ", error)
            })
    }

    const handleClickRegistos = () => {
        navigate(`/obras/${oid}/registers`)
    }

    const handleClickFuncionarios = () => {
        navigate(`/obras/${oid}/funcionarios`)
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

    const navigateBack = () => {
        navigate("/obras")
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
        console.log("EDIÇÃO : ", editedObra)
        console.log(isEditing)
        if (editedObra) {
            const editedObraForUpdate = {
                ...editedObra.constructionAndRoleOutputModel,
                startDate: editedObra.constructionAndRoleOutputModel.startDate?.value$kotlinx_datetime || null,
                endDate: editedObra.constructionAndRoleOutputModel.endDate?.value$kotlinx_datetime || null,
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

    const handleSuspendOrRecover = (status: string) => {
        if (!obra) return
        const updatedObra = {
            ...obra,
            startDate: obra.constructionAndRoleOutputModel.startDate?.value$kotlinx_datetime || null,
            endDate: obra.constructionAndRoleOutputModel.endDate?.value$kotlinx_datetime || null,
            status: status || "on going",
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

    const handleDeleteObra = () => {
        handleSuspendOrRecover("deleted")
        navigate("/obras")
    }

    const handleRecoverObra = () => {
        handleSuspendOrRecover("on going")
    }

    const handleSuspendObra = () => {
        handleSuspendOrRecover("recoverable")
    }

    const handleCompletedObra = () => {
        handleSuspendOrRecover("completed")
    }

    const handleTabChange = (event: React.ChangeEvent<{}>, newValue: number) => {
        setTabIndex(newValue);
    };

    const navigateToRegisters = () => {
        navigate(`/obras/${oid}/registers`)
    }

    const navigateToMembers = () => {
        navigate(`/obras/${oid}/funcionarios`)
    }

    if (!obra) {
        return <CircularProgress />;
    }
    // @ts-ignore
    return (
        <Card>
            <CardContent>
                <Box display="flex" justifyContent="flex-start">
                    <IconButton onClick={navigateBack} title={"Voltar"}>
                        <ArrowBackIcon />
                    </IconButton>
                </Box>
                <Typography variant="h4" component="h2" gutterBottom>
                    Informações da Obra
                </Typography>
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
                        <Tabs
                            value={tabIndex}
                            onChange={handleTabChange}
                            indicatorColor="primary"
                            textColor="primary"
                            orientation="vertical"
                        >
                            <Tab label="Visão Geral" />
                            <Tab label="Registos" onClick={navigateToRegisters}/>
                            <Tab label="Membros" onClick={navigateToMembers}/>
                        </Tabs>
                    </Grid>
                    <Grid item xs={12} md={8}>
                        {obra.constructionAndRoleOutputModel.role === "admin" && !isEditing && (obra.constructionAndRoleOutputModel.status === "on going") && (
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
                                            value={editedObra?.constructionAndRoleOutputModel.name || ""}
                                            onChange={handleChange}
                                        />
                                    ) : (
                                        obra.constructionAndRoleOutputModel.name
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
                                            value={editedObra?.constructionAndRoleOutputModel.location || ""}
                                            onChange={handleChange}
                                        />
                                    ) : (
                                        obra.constructionAndRoleOutputModel.location
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
                                            value={editedObra?.constructionAndRoleOutputModel.description || ""}
                                            onChange={handleChange}
                                        />
                                    ) : (
                                        obra.constructionAndRoleOutputModel.description
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
                                            value={formatDate(editedObra?.constructionAndRoleOutputModel.startDate)|| ""}
                                            onChange={handleChange}
                                        />
                                    ) : (
                                        formatDate(obra.constructionAndRoleOutputModel.startDate)
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
                                            value={formatDate(editedObra?.constructionAndRoleOutputModel.endDate) || ""}
                                            onChange={handleChange}
                                        />
                                    ) : (
                                        formatDate(obra.constructionAndRoleOutputModel.endDate)
                                    )
                                } primaryTypographyProps={{ style: { color: '#0000FF' } }}/>
                            </ListItem>
                            <Divider />
                            <ListItem>
                                <ListItemText primary="Estado" secondary={
                                        obra.constructionAndRoleOutputModel.status
                                } primaryTypographyProps={{ style: { color: '#0000FF' } }}/>
                            </ListItem>
                            <Divider />
                            <ListItem>
                                <ListItemText primary="Função" secondary={
                                    isEditing ? (
                                        <>
                                            <Select
                                                labelId="function-label"
                                                id="function"
                                                name="function"
                                                value={editedObra?.constructionAndRoleOutputModel.function || ""}
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
                                        obra.constructionAndRoleOutputModel.function
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
                                    {obra.constructionAndRoleOutputModel.role === "admin" && (
                                        <Button variant="contained" color="primary" sx={{ ml: 2 }} onClick={handleClickFuncionarios}>
                                            Membros
                                        </Button>
                                    )}
                                    {obra.constructionAndRoleOutputModel.role === "admin" && obra.constructionAndRoleOutputModel.status === "on going" && (
                                        <>
                                            <IconButton variant="contained" color="primary" sx={{ ml: 2 }} onClick={handleClickEditObra} title="Editar">
                                                <EditIcon />
                                            </IconButton>
                                            <IconButton variant="contained" sx={{ ml: 2 }} onClick={handleSuspendObra} title="Suspender">
                                                <ClearIcon sx={{ color: 'red' }}/>
                                            </IconButton>
                                            <IconButton variant="contained" sx={{ ml: 2 }} onClick={handleCompletedObra} title="Terminar" >
                                                <CheckCircleIcon sx={{ color: 'green'}}/>
                                            </IconButton>
                                        </>
                                    )}
                                    {obra.constructionAndRoleOutputModel.role === "admin" && (obra.constructionAndRoleOutputModel.status === "on going" || obra.constructionAndRoleOutputModel.status === "completed") && (
                                        <>


                                        </>

                                    )}
                                    {obra.constructionAndRoleOutputModel.role === "admin" && (obra.constructionAndRoleOutputModel.status === "recoverable") && (
                                        <>
                                            <IconButton variant="contained" sx={{ ml: 2 }} onClick={handleRecoverObra} title="Recuperar">
                                                <ArrowCircleUpIcon sx={{ color: 'green' }}/>
                                            </IconButton>
                                            <IconButton variant="contained" sx={{ ml: 2 }} onClick={handleDeleteObra} title="Apagar">
                                                <DeleteIcon sx={{ color: 'darkred' }}/>
                                            </IconButton>
                                        </>

                                    )}
                                </>
                            )}
                            {isEditing && obra.constructionAndRoleOutputModel.role === "admin" && (
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