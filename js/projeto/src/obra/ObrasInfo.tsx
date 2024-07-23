import {ChangeEvent, useEffect, useState} from "react";
import { useCookies } from "react-cookie";
import * as React from "react";
import { useNavigate, useParams, useSearchParams} from "react-router-dom";
import {
    Card,
    CardContent,
    Typography,
    CircularProgress,
    Grid,
    Avatar,
    Button,
    Box,
    Tabs,
    Tab, Stack
} from "@mui/material";
import IconButton from "@mui/material/IconButton";
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import ObrasVisaoGeralForm from "./Forms/ObrasVisaoGeralForm";
import ObraRegistosForm from "./Forms/ObraRegistosForm";
import {useMaterialReactTable} from "material-react-table";
import ObraFuncionariosForm from "./Forms/ObraFuncionariosForm";
import ObraRegistosOfAllPendingUsersForm from "./Forms/ObraRegistosOfAllPendingUsersForm";
import ObraRegistosOfUserForm from "./Forms/ObraRegistosOfUserForm";
import ObraFuncionarioInfoForm from "./Forms/ObraFuncionarioInfoForm";
import ObraNFCForm from "./Forms/ObraNFCForm";
import {path} from "../App";

export interface DateObject {
    year: number;
    dayOfMonth: number;
    month: string;
    dayOfWeek: string;
    dayOfYear: number;
    monthNumber: number;
    value$kotlinx_datetime: string;
}

export interface Obra {
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

export interface Registo {
    id: number;
    id_utilizador: number;
    id_obra: number;
    nome: string;
    entrada: DateObject;
    saida: DateObject | null;
    status: string | null;
}

export interface RegistosOutputModel {
    registers: Registo[],
    meRoute: string,
    pendingRoute: string,
    allRoute: string
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

const columns = [
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
        header: 'Estado',
    }
];

export interface UserModel {
    id: number
    nome: string
    email: string
    morada: string
    func: string
    foto: string | null
}

export interface UserOutputModel {
    users: UserModel[]
}

interface states {
    value: string
}

export default function ObrasInfo() {
    const [cookies] = useCookies(["token"]);
    const [obra, setObra] = useState<Obra>()
    const { oid } = useParams<{ oid: string }>();
    const navigate = useNavigate();
    const [isEditing, setIsEditing] = useState(false);
    const [editedObra, setEditedObra] = useState<Obra>();
    const [tabIndex, setTabIndex] = useState(0);
    const [state, setState] = useState<states>({ value: "geral" })

    /*
     * Obras
     */

    useEffect(() => {
        fetch(`${path}/obras/${oid}`, {
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
                setObra(body);
                setEditedObra(body);
            })
            .catch((error) => {
                console.error("Error fetching obra:", error);
            });
    }, [cookies.token, oid]);

    useEffect(() => {
        fetch(`${path}/obras/${oid}/registos/pendente`, {
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
            setPendingRegisters(body);

        }).catch((error) => {
            console.error("Error fetching registos pendentes:", error);
        });
    }, [cookies.token, oid]);

    const handleVisaoGeral = () => {
        fetch(`${path}/obras/${oid}`, {
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
                setObra(body);
                setEditedObra(body);
                setState({ value: "geral" })
            })
            .catch((error) => {
                console.error("Error fetching obra:", error);
            });
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
        if (editedObra) {
            const editedObraForUpdate = {
                ...editedObra,
                startDate: editedObra.startDate?.value$kotlinx_datetime || null,
                endDate: editedObra.endDate?.value$kotlinx_datetime || null,
            };
            fetch(`${path}/obras/${oid}`, {
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
            startDate: obra.startDate?.value$kotlinx_datetime || null,
            endDate: obra.endDate?.value$kotlinx_datetime || null,
            status: status || "on going",
        };
        fetch(`${path}/obras/${oid}`, {
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

    const handleTabChange = (event: React.ChangeEvent<{}>, newValue: number) => {
        setTabIndex(newValue);
    };

    /*
     *  Registers
     */
    const [registos, setRegistos] = useState<RegistosOutputModel>({
        allRoute: "",
        meRoute: "", pendingRoute: "", registers: [] })
    const [searchParams, setSearchParams] = useSearchParams();
    const [openForm, setOpenForm] = useState(false);
    const [loading, setLoading] = useState(true);

    const handleGetRegistersMine = () => {
        const page = searchParams.get('page') || '0'
        fetch(`${path}/obras/${oid}/registos/me?page=${page}`, {
            method: "GET",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`
            },
        }).then((res) => {
            setState({ value: "registo" })
            if (res.ok) return res.json()
            else return null
        }).then((body) => {
            if (body) {
                setRegistos(body)
                setLoading(false)
            }
        }).catch(error => {
            console.error("Error fetching registos: ", error)
        })
    }

    const handleGetRegistersAll = () => {
        const page = searchParams.get('page') || '0'
        fetch(`${path}/obras/${oid}/registos?page=${page}`, {
            method: "GET",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`
            },
        }).then((res) => {
            setState({ value: "registo" })
            if (res.ok) return res.json()
            else return null
        }).then((body) => {
            if (body) {
                setRegistos(body)
                setLoading(false)
            }
        }).catch(error => {
            console.error("Error fetching registos: ", error)
        })
    }

    const handleClickOpenForm = () => {
        setOpenForm(true);
    };

    const handleCloseForm = (reload: boolean) => {
        setOpenForm(false);
        if (reload) {
            handleGetRegistersMine()
            handleGetRegistersAll()
            handleGetPendingRegisters()
        }
    };

    const tableRegisters = useMaterialReactTable({
        columns,
        data: registos.registers,
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

    /*
     * Pending Registers
     */
    const [pendingRegisters, setPendingRegisters] = useState<RegistosOutputModel>({
        allRoute: "",
        meRoute: "",
        pendingRoute: "",
        registers: [] });
    const [open, setOpen] = useState(false);

    const handleGetPendingRegisters = () => {
        const page = searchParams.get('page') || '0'
        fetch(`${path}/obras/${oid}/registos/pendente?page=${page}`, {
            method: "GET",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`
            },
        }).then((res) => {
            setState({ value: "pendente" })
            if (res.ok) return res.json()
            else return null
        }).then((body) => {
            if (body) {
                setPendingRegisters(body)
                setLoading(false)
            }
        }).catch(error => {
            console.error("Error fetching registos: ", error)
        })
    }

    const handleAcceptOrRejectPendingRegister = (registerId: number, userId: number, response: string) => {
        fetch(`${path}/obras/${oid}/registos`, {
            method: 'PUT',
            headers: {
                'Content-type': 'application/json',
                'Authorization': `Bearer ${cookies.token}`
            },
            body: JSON.stringify({ registerId: registerId, userId: userId, response: response })
        })
            .then((res) => {
                if (res.ok) return res;
                else return null;
            })
            .then((body) => {
                setPendingRegisters((prevRegistos) => ({
                    ...prevRegistos,
                    registers: prevRegistos.registers.map((registo) =>
                        registo.id === registerId ? { ...registo, status: response } : registo
                    )
                }))
                navigate(`/obras/${oid}`)
            })
            .catch(error => {
                console.error("Error fetching: ", error);
            });
    };

    const handleClickOpen = () => {
        setOpen(true);
    };

    const tablePendingRegisters = useMaterialReactTable({
        columns,
        data: pendingRegisters.registers,
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

    /*
     * Registo Funcionario
     */
    const [registosFuncionario, setRegistosFuncionario] = useState<RegistosOutputModel>({
        allRoute: "",
        meRoute: "",
        pendingRoute: "",
        registers: [] })
    const [username, setUsername] = useState<string>("")

    const handleGetUserRegisters = (uid: number) => {
        const page = searchParams.get('page') || '0'
        fetch(`${path}/obras/${oid}/registos/${uid}?page=${page}`, {
            method: "GET",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`
            },
        }).then((res) => {
            setState({ value: "registoFuncionario" })
            if (res.ok) return res.json()
            else return null
        }).then((body) => {
            if (body) {
                setRegistosFuncionario(body)
                setLoading(false)
                setUsername(body.registers[0].userName)
            }
        }).catch(error => {
            console.error("Error fetching registos: ", error)
        })
    }

    const tableRegistersFuncionario = useMaterialReactTable({
        columns,
        data: registosFuncionario.registers,
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

    /*
     *  Funcionarios
     */
    const [users, setUsers] = useState<UserOutputModel>({ users: [] });

    const handleClickAddFuncionario = () => {
        navigate(`/obras/${oid}/funcionario/invite`)
    }

    const handleGetFuncionarios = () => {
        fetch(`${path}/obras/${oid}/users`, {
            method: "GET",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`
            },
        }).then((res) => {
            setState({ value: "funcionarios" })
            if (res.ok) return res.json()
            else return null
        }).then((body) => {
            if (body) {
                setUsers(body)
                setLoading(false)
            }
        }).catch(error => {
            console.error("Error fetching registos: ", error)
        })
    }

    const handleRemoveUser = (uid: number) => {
        fetch(`${path}/obras/${oid}/user/${uid}`, {
            method: "DELETE",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`
            },
        }).then((res) => {
            if (res.ok) {
                console.log("User removed")
            } else {
                console.error("Failed to remove user")
            }
        }).catch((error) => {
            console.error("Error removing user:", error)
        })
    }

    /*
     * Funcionario Info
     */
    const [user, setUser] = useState<UserModel>(
        {
            id: 0,
            nome: "",
            email: "",
            morada: "",
            func: "",
            foto: ""
        }
    )

    const handleGetFuncionarioInfo = (uid: number) => {
        fetch(`${path}/obras/${oid}/user/${uid}`, {
            method: "GET",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`,
            },
        })
            .then((res) => {
                setState({ value: "funcionarioInfo" })
                if (res.ok) {
                    return res.json()
                } else {
                    return null
                }
            })
            .then((body) => {
                if (body) {
                    setUser(body);
                }
            })
            .catch((error) => {
                console.error("Error fetching profile:", error)
            })
    }

    if (!cookies.token) {
        return (
            <Stack sx={{ m: '5rem 0', alignItems: 'center' }}>
                <Typography variant="h4" color="error">Erro de autenticação</Typography>
                <Typography variant="body1" color="error">Precisa de estar autenticado para acessar a esta página.</Typography>
                <Button variant="contained" color="primary" onClick={() => navigate("/login")}>
                    Login
                </Button>
            </Stack>
        )
    }

    /*
     *  NFC
     */

    const [nfc, setNfc] = useState<string>("");

    const handleNFC = () => {
        fetch(`${path}/obras/${oid}/nfc`, {
            method: "GET",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`,
            },
        })
            .then((res) => {
                setState({ value: "nfc" })
                if (res.ok) {
                    return res.json();
                } else {
                    throw new Error('Failed to fetch nfc');
                }
            })
            .then((body) => {
                setNfc(body.nfcResponse)
            })
            .catch((error) => {
                console.error("Error fetching nfc:", error);
            });
    }

    if (!obra) {
        return <CircularProgress />;
    }
    // @ts-ignore
    return (
        <Card  sx={{ minWidth: '133vh', minHeight: '85vh', height: 'auto', margin: 'auto', marginTop: '2rem' }}>
            <CardContent>
                <Box display="flex" justifyContent="flex-start">
                    <IconButton onClick={navigateBack} title={"Voltar"}>
                        <ArrowBackIcon />
                    </IconButton>
                </Box>
                <Grid container spacing={2}>
                    <Grid item xs={12} md={4} sx={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-center', borderRight: '1px solid #ccc' }}>
                        <Typography variant="h4" color={"black"} sx={{ alignSelf: 'flex-center', mb: 2 }}>
                            {obra.name}
                        </Typography>
                        <Avatar
                            alt={obra.name}
                            src={obra.foto || "https://t-obra.com/wp-content/uploads/2019/09/graca16.jpg"}
                            variant="rounded"
                            sx={{
                                width: "40vh",
                                height: "40vh",
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
                            sx={{
                                border: 'none'
                            }}
                        >
                            <Tab label="Visão Geral" onClick={handleVisaoGeral} sx={{ border: 'none'}}/>

                            {obra.role === "admin" && (
                                <Tab label="Registos" onClick={handleGetRegistersAll} sx={{ border: 'none'}}/>
                            )}
                            {obra.role === "admin" && (
                                <Tab label="Membros" onClick={handleGetFuncionarios} sx={{ border: 'none'}}/>
                            )}
                            {obra.role === "funcionario" && (
                                <Tab label="Registos" onClick={handleGetRegistersMine} sx={{ border: 'none'}}/>
                            )}

                        </Tabs>
                    </Grid>
                    <Grid item xs={12} md={8}>
                        {state.value === "geral" && (
                            <ObrasVisaoGeralForm
                                obra={obra}
                                editedObra={editedObra}
                                pendingRegisters={pendingRegisters}
                                isEditing={isEditing}
                                handleChange={handleChange}
                                handleSelectChange={handleSelectChange}
                                handleFileChange={handleFileChange}
                                handleClickPendingRegisters={handleGetPendingRegisters}
                                handleNFC={handleNFC}
                                handleClickEditObra={handleClickEditObra}
                                handleSuspendOrRecover={handleSuspendOrRecover}
                                handleSaveObra={handleSaveObra}
                                handleCancelEdit={handleCancelEdit}
                            />
                        )}
                        {state.value === "registo" && (
                            <ObraRegistosForm
                             handleClickOpenForm={handleClickOpenForm}
                             handleCloseForm={handleCloseForm}
                             table={tableRegisters}
                             openForm={openForm}
                             registo={registos}
                             setRegistos={setRegistos}
                             obra={obra}/>
                        )}
                        {state.value === "funcionarios" && obra.role === "admin" && (
                            <ObraFuncionariosForm
                                users={users}
                                handleViewProfile={handleGetFuncionarioInfo}
                                handleViewUserRecords={handleGetUserRegisters}
                                handleClickAddFuncionario={handleClickAddFuncionario}
                                handleRemoveUser={handleRemoveUser}
                            />
                        )}
                        {state.value === "pendente" && obra.role === "admin" &&(
                            <ObraRegistosOfAllPendingUsersForm
                                table={tablePendingRegisters}
                                handleAcceptOrRejectPendingRegister={handleAcceptOrRejectPendingRegister}
                            />
                        )}
                        {state.value === "registoFuncionario" && obra.role === "admin" && (
                            <ObraRegistosOfUserForm
                                table={tableRegistersFuncionario}
                                username={username}
                            />
                        )}
                        {state.value === "funcionarioInfo" && obra.role === "admin" && (
                            <ObraFuncionarioInfoForm
                                 user={user}
                            />
                        )}
                        {state.value === "nfc" && obra.role === "admin" && (
                            <ObraNFCForm
                                nfc={nfc ? nfc : "Sem NFC associado"}
                                oid={obra.oid.toString()}
                                setNfc={setNfc}
                            />
                        )}
                    </Grid>
                </Grid>
            </CardContent>
        </Card>
    );
}