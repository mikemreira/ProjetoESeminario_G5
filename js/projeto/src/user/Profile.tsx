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
    CircularProgress,
    Grid,
    Avatar,
    Button,
    Box,
    Divider,
} from "@mui/material";
import TextField from "@mui/material/TextField";
import {useSetAvatar} from "../context/Authn";
import {useNavigate} from "react-router-dom";

interface UserModel {
    id: number
    nome: string
    email: string
    morada: string
    foto: string | null
}


export default function Profile() {
    const setAvatar = useSetAvatar()
    const [cookies] = useCookies(["token"])
    const [user, setUser] = useState<UserModel>()
    const [isEditing, setIsEditing] = useState(false)
    const [editedUser, setEditedUser] = useState<UserModel | null>(null)

    useEffect(() => {
        fetch("/api/users/me", {
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
                }
            })
            .catch((error) => {
                console.error("Error fetching profile:", error)
            })
    }, [cookies.token, user?.foto])

    const handleEditProfile = () => {
        setIsEditing(true)
    }

    const handleSaveProfile = () => {
        console.log(JSON.stringify(editedUser))
        if (editedUser) {
            fetch("/api/users/me", {
                method: "PUT",
                headers: {
                    "Content-type": "application/json",
                    "Authorization": `Bearer ${cookies.token}`,
                },
                body: JSON.stringify(editedUser),
            })
                .then((res) => {
                    if (res.ok) {
                        setUser(editedUser)
                        if (editedUser.foto != null) {
                            setAvatar(editedUser.foto)
                            sessionStorage.setItem('avatar', editedUser.foto)
                        }
                        setIsEditing(false)
                    } else {
                        console.error("Failed to update profile")
                    }
                })
                .catch((error) => {
                    console.error("Error updating profile:", error)
                })
        }
    }

    const handleCancelEdit = () => {
        // @ts-ignore
        setEditedUser(user)
        setIsEditing(false)
    }

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target
        setEditedUser((prevUser) => ({
            ...prevUser!,
            [name]: value,
        }))
    }

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0]
        if (file) {
            const reader = new FileReader()
            reader.onloadend = () => {
                setEditedUser((prevUser) => ({
                    ...prevUser!,
                    foto: reader.result as string | null,
                }))
            }
            reader.readAsDataURL(file)
        }
    }

    const navigate = useNavigate()

    const handleChangePass = () => {
        navigate("/profile/changePassword")
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
                                        onChange={handleFileChange}
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
                                                        onChange={handleChange}
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
                                                        onChange={handleChange}
                                                    />
                                                ) : (
                                                    user.morada
                                                )
                                            }
                                            primaryTypographyProps={{ style: { color: "#0000FF" } }}
                                        />
                                    </ListItem>
                                    <Divider />
                                    <ListItem>
                                        <ListItemText
                                            primary="ID"
                                            secondary={user.id}
                                            primaryTypographyProps={{ style: { color: "#0000FF" } }}
                                        />
                                    </ListItem>
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
                                        <Button variant="contained" color="primary" onClick={handleEditProfile}>
                                            Editar
                                        </Button>
                                    )}
                                    <Button variant="contained" color="primary" sx={{ marginLeft: 1 }} onClick={handleChangePass}>
                                        Alterar Password
                                    </Button>
                                </Box>
                            </Grid>
                        </Grid>
                    </CardContent>
                </Card>
            )}
        </div>
    )
}