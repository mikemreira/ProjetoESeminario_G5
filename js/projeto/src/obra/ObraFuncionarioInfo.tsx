
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
import {useParams} from "react-router-dom";

interface UserModel {
    id: number
    nome: string
    email: string
    morada: string
    func: string
    foto: string | null
}

export default function ObraFuncionarioInfo() {
    const [cookies] = useCookies(["token"])
    const [user, setUser] = useState<UserModel>()
    const { oid } = useParams<{ oid: string }>();
    const { uid } = useParams<{ uid: string }>();

    useEffect(() => {
        fetch(`/api/obras/${oid}/user/${uid}`, {
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
                }
            })
            .catch((error) => {
                console.error("Error fetching profile:", error)
            })
    }, [cookies.token, user?.foto])


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
                                    src={user?.foto || "https://cdn-icons-png.flaticon.com/512/3135/3135715.png"}
                                    variant="rounded"
                                    sx={{
                                        width: "100%",
                                        height: "100%",
                                        cursor: "default",
                                    }}
                                />
                            </Grid>
                            <Grid item xs={12} md={8}>
                                <List>
                                    <ListItem>
                                        <ListItemText
                                            primary="Nome"
                                            secondary={user.nome}
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
                                            secondary={user.morada}
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
                            </Grid>
                        </Grid>
                    </CardContent>
                </Card>
            )}
        </div>
    )
}