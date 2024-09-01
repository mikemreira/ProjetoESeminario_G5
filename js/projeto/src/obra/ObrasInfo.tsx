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
import ObraFuncionariosForm from "./Forms/ObraFuncionariosForm";
import ObraRegistosOfAllPendingUsersForm from "./Forms/ObraRegistosOfAllPendingUsersForm";
import ObraRegistosOfUserForm from "./Forms/ObraRegistosOfUserForm";
import ObraFuncionarioInfoForm from "./Forms/ObraFuncionarioInfoForm";
import ObraNFCForm from "./Forms/ObraNFCForm";
import {path} from "../App";
import {handleChange, handleFileChange, handleSelectObraChange, table} from "../Utils";
// @ts-ignore
import emptyFoto from "../assets/noImage.png";

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

export interface RegistoAndUser {
    userName: string;
    id: number;
    oid: number;
    uid: number;
    startTime: DateObject;
    endTime: DateObject | null;
    status: string;
}

export interface UserRegistersAndObraOutputModel {
    registers: RegistoAndUser[],
    constructionStatus: string,
    meRoute: string,
    pendingRoute: string,
    allRoute: string,
    unfinishedRoute: string,
    registersSize?: number
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

export const pageSize = 5;

export default function ObrasInfo() {
    const [cookies] = useCookies(["token"]);
    const [obra, setObra] = useState<Obra>()
    const { oid } = useParams<{ oid: string }>();
    const navigate = useNavigate();
    const [isEditing, setIsEditing] = useState(false);
    const [editedObra, setEditedObra] = useState<Obra>();
    const [tabIndex, setTabIndex] = useState(0);
    const [state, setState] = useState<states>({ value: "geral" })

    const handleNextPage = () => {
        if (page) {
            setPage(page + 1)
        }
    }

    const handlePreviousPage = () => {
        if (page > 1) {
            setPage(page - 1)
        }
    }

    const handleFilterReset = () => {
        setInitialDate(null)
        setEndDate(null)
        setPage(1)
    }

    const handlePageChange = (pageNumber: number) => {
        setPage(pageNumber)
    }

    const handleFirstPage = () => {
        setPage(1)
    }

    const handleLastPage = () => {
        setPage(totalPages)
    }

    /*
     * Obras
     */

    const handleGetObra = () => {
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
    }

    useEffect(() => {
        handleGetObra()
        if (obra?.role === "admin") {
            fetchIsPendingRegisters(1)
        }
    }, [cookies.token, oid, obra?.role]);

    const [page, setPage] = useState<number>(1);
    const [totalPages, setTotalPages] = useState(1);

    const [initialDate, setInitialDate] = useState<string | null>(null);
    const [endDate, setEndDate] = useState<string | null>(null);

    const fetchIsPendingRegisters = (pageNumber: number) => {
        const params = new URLSearchParams({ page: String(pageNumber) });
        if (initialDate) params.append("initialDate", initialDate);
        if (endDate) params.append("endDate", endDate);
        const queryString = params.toString();
        console.log("fez este fetch")
        fetch(`${path}/obras/${oid}/registos/pendente?${queryString}`, {
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
    }

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

    const handleChangeObra = handleChange(setEditedObra)
    const handeFileChangeObra = handleFileChange(setEditedObra)
    const handleSelectChangeObra = handleSelectObraChange(setEditedObra)

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

    const [registos, setRegistos] = useState<UserRegistersAndObraOutputModel>({
        registers: [],
        constructionStatus: "",
        meRoute: "",
        pendingRoute: "",
        allRoute: "",
        unfinishedRoute: "",
        registersSize: 0,
    })
    const [searchParams, setSearchParams] = useSearchParams();
    const [openForm, setOpenForm] = useState(false);

    const handleGetRegistersMine = (pageNumber: number) => {
        const params = new URLSearchParams({ page: String(pageNumber) });
        if (initialDate) params.append("initialDate", initialDate);
        if (endDate) params.append("endDate", endDate);
        const queryString = params.toString();
        console.log("queryString: " + queryString)
        fetch(`${path}/obras/${oid}/registos/me?${queryString}`, {
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
                setTotalPages(Math.ceil(body.registersSize / pageSize))
            }
        }).catch(error => {
            console.error("Error fetching registos: ", error)
        })
    }

    const handleGetRegistersAll = (pageNumber: number) => {
        const params = new URLSearchParams({ page: String(pageNumber) });
        if (initialDate) params.append("initialDate", initialDate);
        if (endDate) params.append("endDate", endDate);
        const queryString = params.toString();
        console.log("queryString: " + queryString)
        fetch(`${path}/obras/${oid}/registos?${queryString}`, {
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
                setTotalPages(Math.ceil(body.registersSize / pageSize))
            }
        }).catch(error => {
            console.error("Error fetching registos: ", error)
        })
    }

    const handleGetRegistersIncomplete = (pageNumber: number) => {
        const params = new URLSearchParams({ page: String(pageNumber) });
        if (initialDate) params.append("initialDate", initialDate);
        if (endDate) params.append("endDate", endDate);
        const queryString = params.toString();
        console.log("queryString: " + queryString)
        fetch(`${path}/obras/${oid}/registos/incompletos?${queryString}`, {
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
                setTotalPages(Math.ceil(body.registersSize / pageSize))
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
            handleGetRegistersMine(page)
        }
    };

    const tableRegisters = table(columns, registos.registers);

    /*
     * Pending Registers
     */
    const [pendingRegisters, setPendingRegisters] = useState<UserRegistersAndObraOutputModel>({
        registers: [],
        constructionStatus: "",
        meRoute: "",
        pendingRoute: "",
        allRoute: "",
        unfinishedRoute: "",
        registersSize: 0,
       });
    const [open, setOpen] = useState(false);

    const handleGetPendingRegisters = (pageNumber: number) => {
        const params = new URLSearchParams({ page: String(pageNumber) });
        if (initialDate) params.append("initialDate", initialDate);
        if (endDate) params.append("endDate", endDate);
        const queryString = params.toString();
        console.log("queryString: " + queryString)
        fetch(`${path}/obras/${oid}/registos/pendente?${queryString}`, {
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
                console.log("body: " + body.registersSize)
                setPendingRegisters(body)
                setTotalPages(Math.ceil(body.registersSize / pageSize))
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

    const tablePendingRegisters = table(columns, pendingRegisters.registers);

    /*
     * Registo Funcionario
     */
    const [registosFuncionario, setRegistosFuncionario] = useState<UserRegistersAndObraOutputModel>({
        registers: [],
        constructionStatus: "",
        meRoute: "",
        pendingRoute: "",
        allRoute: "",
        unfinishedRoute: "",
        registersSize: 0,
    })
    const [username, setUsername] = useState<string>("")
    const [selectedUser, setSelectedUser] = useState<number>(0)

    const handleGetUserRegisters = (pageNumber: number, uid: number) => {
        setSelectedUser(uid)
        const params = new URLSearchParams({ page: String(pageNumber) });
        if (initialDate) params.append("initialDate", initialDate);
        if (endDate) params.append("endDate", endDate);
        const queryString = params.toString();
        fetch(`${path}/obras/${oid}/registos/${uid}?${queryString}`, {
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
                setUsername(body.registers[0].userName)
                setTotalPages(Math.ceil(body.registersSize / pageSize))
            }
        }).catch(error => {
            console.error("Error fetching registos: ", error)
        })
    }

    const tableRegistersFuncionario = table(columns, registosFuncionario.registers);

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
                            src={obra.foto || emptyFoto}
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
                                onChange={handeFileChangeObra}
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
                                <Tab label="Registos" onClick={() => handleGetRegistersAll(page)} sx={{ border: 'none'}}/>
                            )}
                            {obra.role === "admin" && (
                                <Tab label="Membros" onClick={handleGetFuncionarios} sx={{ border: 'none'}}/>
                            )}
                            {obra.role === "funcionario" && (
                                <Tab label="Registos" onClick={() => handleGetRegistersMine(page)} sx={{ border: 'none'}}/>
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
                                handleChange={handleChangeObra}
                                handleSelectChange={handleSelectChangeObra}
                                handleFileChange={handeFileChangeObra}
                                handleClickPendingRegisters={handleGetPendingRegisters}
                                handleNFC={handleNFC}
                                handleClickEditObra={handleClickEditObra}
                                handleSuspendOrRecover={handleSuspendOrRecover}
                                handleSaveObra={handleSaveObra}
                                handleCancelEdit={handleCancelEdit}
                                pageNumber={page}
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
                             obra={obra}
                             totalPages={totalPages}
                             setTotalPages={setTotalPages}
                             handleNextPage={handleNextPage}
                             handlePreviousPage={handlePreviousPage}
                             handleFilterReset={handleFilterReset}
                             handlePageChange={handlePageChange}
                             handleFirstPage={handleFirstPage}
                             handleLastPage={handleLastPage}
                             page={page}
                             initialDate={initialDate}
                             setInitialDate={setInitialDate}
                             endDate={endDate}
                             setEndDate={setEndDate}
                            />
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
                                totalPages={totalPages}
                                handleNextPage={handleNextPage}
                                handlePreviousPage={handlePreviousPage}
                                handleFilterReset={handleFilterReset}
                                handlePageChange={handlePageChange}
                                handleFirstPage={handleFirstPage}
                                handleLastPage={handleLastPage}
                                page={page}
                                initialDate={initialDate}
                                setInitialDate={setInitialDate}
                                endDate={endDate}
                                setEndDate={setEndDate}
                                handleGetPendingRegisters={handleGetPendingRegisters}
                            />
                        )}
                        {state.value === "registoFuncionario" && obra.role === "admin" && (
                            <ObraRegistosOfUserForm
                                obra={obra}
                                selectedUser={selectedUser}
                                table={tableRegistersFuncionario}
                                username={username}
                                handleGetUserRegisters={handleGetUserRegisters}
                                handleCloseForm={handleCloseForm}
                                openForm={openForm}
                                totalPages={totalPages}
                                setTotalPages={setTotalPages}
                                handleNextPage={handleNextPage}
                                handlePreviousPage={handlePreviousPage}
                                handleFilterReset={handleFilterReset}
                                handlePageChange={handlePageChange}
                                handleFirstPage={handleFirstPage}
                                handleLastPage={handleLastPage}
                                page={page}
                                initialDate={initialDate}
                                setInitialDate={setInitialDate}
                                endDate={endDate}
                                setEndDate={setEndDate}
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
    )
}