import {useCookies} from "react-cookie";
import {useEffect, useState} from "react";
import * as React from "react";
import {
    Card,
    CardContent,
    Typography,
    List,
    ListItem,
    ListItemText,
    Grid,
    Avatar,
    Button,
    Box,
    Divider, Stack, CircularProgress,
} from "@mui/material";
import TextField from "@mui/material/TextField";
import {useSetAvatar} from "../context/Authn";
import {useNavigate} from "react-router-dom";
import {path} from "../App";
import {handleChange, handleFileChange} from "../Utils";

export interface UserModel {
    id: number
    nome: string
    email: string
    morada: string
    fotoHref: string | null
    foto: string | null
}

export default function Profile() {
    const setAvatar = useSetAvatar()
    const [cookies] = useCookies(["token"])
    const [user, setUser] = useState<UserModel>()
    const [isEditing, setIsEditing] = useState(false)
    const [editedUser, setEditedUser] = useState<UserModel | null>(null)
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        fetch(`${path}/users/me`, {
            method: "GET",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`,
            },
        })
            .then((res) => {
                if (res.ok) {
                    return res.json()
                } else {
                    return null
                }
            })
            .then((body) => {
                if (body) {
                    setUser(body);
                    setEditedUser(body);
                    if (body.fotoHref) {
                        fetch(`${body.fotoHref}?type=thumbnail`, {
                            method: "GET",
                            headers: {
                                "Content-type": "application/json",
                                "Authorization": `Bearer ${cookies.token}`,
                            },
                        })
                            .then((res) => res.json())
                            .then((fotoBody) => {
                                setUser(
                                    (prevUser) => ({
                                        ...prevUser!,
                                        foto: fotoBody.foto,
                                    }) as UserModel
                                )
                                setEditedUser(
                                    (prevUser) => ({
                                        ...prevUser!,
                                        foto: fotoBody.foto,
                                    }) as UserModel
                                )
                                setAvatar(fotoBody.foto)
                                localStorage.setItem("avatar", fotoBody.foto)
                            })
                            .catch((error) => {
                                console.error("Error fetching thumbnail image:", error)
                            })
                            .finally(() => {
                                setLoading(false);
                            });
                    } else {
                        setLoading(false);
                    }
                } else {
                setLoading(false)
                }
            })
            .catch((error) => {
                console.error("Error fetching profile:", error)
                setLoading(false)
            })
    }, [cookies.token])

    const handleEditProfile = () => {
        setIsEditing(true)
    }

    const handleSaveProfile = () => {
        if (editedUser) {
            setLoading(true)
            fetch(`${path}/users/me`, {
                method: "PUT",
                headers: {
                    "Content-type": "application/json",
                    "Authorization": `Bearer ${cookies.token}`,
                },
                body: JSON.stringify(editedUser),
            })
                .then((res) => {
                    if (res.ok) {
                        return res.json()
                    } else {
                        console.error("Failed to update profile")
                        return null
                    }
                })
                .then((body) => {
                    if (body) {
                        setUser(body)
                        if (body.fotoHref) {
                            fetch(`${body.fotoHref}?type=thumbnail`, {
                                method: "GET",
                                headers: {
                                    "Content-type": "application/json",
                                    "Authorization": `Bearer ${cookies.token}`,
                                },
                            })
                                .then((res) => res.json())
                                .then((fotoBody) => {
                                    setEditedUser((prevUser) => ({
                                        ...prevUser!,
                                        foto: fotoBody.foto,
                                    }) as UserModel)
                                    setAvatar(fotoBody.foto)
                                    localStorage.setItem("avatar", fotoBody.foto)
                                })
                                .catch((error) => {
                                    console.error("Error fetching thumbnail image:", error)
                                })
                                .finally(() => {
                                    setLoading(false)
                                })
                        } else {
                            setLoading(false)
                        }
                        setIsEditing(false)
                    }
                })
                .catch((error) => {
                    console.error("Error updating profile:", error)
                    setLoading(false)
                })
        }
    }

    const handleCancelEdit = () => {
        // @ts-ignore
        setEditedUser(user)
        setIsEditing(false)
    }

    const handleChangeProfile = handleChange(setEditedUser)
    const handleFileChangeProfile = handleFileChange(setEditedUser)

    const navigate = useNavigate()

    const handleChangePass = () => {
        navigate("/profile/changePassword")
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

    return (
        <div>
            {user && (
                <Card sx={{ boxShadow: 3 }}>
                    <CardContent>
                        <Typography variant="h3" gutterBottom>
                            Perfil
                        </Typography>
                        <Grid container spacing={2}>
                            {loading ? (
                                <Box
                                    sx={{
                                        display: 'flex',
                                        justifyContent: 'center',
                                        alignItems: 'center',
                                        width: '130vh',
                                    }}
                                >
                                    <CircularProgress />
                                </Box>
                            ) : (
                                <>
                                    <Grid item xs={12} md={4}>
                                        <Avatar
                                            alt={user.nome}
                                            src={editedUser?.foto || "https://cdn-icons-png.flaticon.com/512/3135/3135715.png"}
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
                                                onChange={handleFileChangeProfile}
                                            />
                                        )}
                                    </Grid>
                                    <Grid item xs={12} md={8}>
                                        <List>
                                            <ListItem>
                                                <ListItemText
                                                    primary="Nome"
                                                    secondary={
                                                        isEditing ? (
                                                            <TextField
                                                                fullWidth
                                                                name="nome"
                                                                value={editedUser?.nome || ""}
                                                                onChange={handleChangeProfile}
                                                            />
                                                        ) : (
                                                            user.nome
                                                        )
                                                    }
                                                    primaryTypographyProps={{ style: { color: "#0000FF" } }}
                                                />
                                            </ListItem>
                                            <Divider />
                                            <ListItem>
                                                <ListItemText
                                                    primary="Email"
                                                    secondary={user.email}
                                                    primaryTypographyProps={{ style: { color: "#0000FF" } }}
                                                />
                                            </ListItem>
                                            <Divider />
                                            <ListItem>
                                                <ListItemText
                                                    primary="Morada"
                                                    secondary={
                                                        isEditing ? (
                                                            <TextField
                                                                fullWidth
                                                                name="morada"
                                                                value={editedUser?.morada || ""}
                                                                onChange={handleChangeProfile}
                                                            />
                                                        ) : (
                                                            user.morada
                                                        )
                                                    }
                                                    primaryTypographyProps={{ style: { color: "#0000FF" } }}
                                                />
                                            </ListItem>
                                            <Divider />
                                        </List>
                                        <Box mt={2}>
                                            {isEditing ? (
                                                <>
                                                    <Button variant="contained" color="primary" onClick={handleSaveProfile} sx={{ marginRight: 1 }} >
                                                        Guardar alterações
                                                    </Button>
                                                    <Button variant="contained" color="error" onClick={handleCancelEdit}>
                                                        Cancelar
                                                    </Button>
                                                </>
                                            ) : (
                                                <>
                                                    <Button variant="contained" color="primary" onClick={handleEditProfile}>
                                                        Editar
                                                    </Button>
                                                    <Button variant="contained" color="primary" sx={{ marginLeft: 1 }} onClick={handleChangePass}>
                                                        Alterar Password
                                                    </Button>
                                                </>
                                            )}
                                        </Box>
                                    </Grid>
                                </>
                            )}
                        </Grid>
                    </CardContent>
                </Card>
            )}
        </div>
    )
}