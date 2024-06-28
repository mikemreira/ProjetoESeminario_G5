import { useEffect, useState } from "react";
import { useCookies } from "react-cookie";
import * as React from "react";
import {useTheme} from "@mui/material/styles";
import { useParams } from "react-router-dom";
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
}

const formatDate = (dateObj: DateObject | null): string => {
    return dateObj ? dateObj.value$kotlinx_datetime : "N/A";
}

export default function ObrasInfo() {
    const [cookies] = useCookies(["token"]);
    const [obra, setObra] = useState<Obra | null>(null);
    const { oid } = useParams<{ oid: string }>();

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
                setObra(body);
            })
            .catch((error) => {
                console.error("Error fetching obra:", error);
            });
    }, [cookies.token, oid]);

    const handleClickRegistos = (oid: number) => {  };

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
                        </List>
                        <Box mt={2}>
                            <Button variant="contained" color="primary" sx={{ mr: 2 }} onClick={() => handleClickRegistos(obra.oid)}>
                                Ver Registos
                            </Button>
                            {obra.role === "admin" && (
                                <Button variant="contained" color="secondary">
                                    Ver Registos dos Funcionários
                                </Button>
                            )}
                        </Box>
                    </Grid>
                </Grid>
            </CardContent>
        </Card>
    );
}