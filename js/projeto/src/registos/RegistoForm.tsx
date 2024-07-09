import * as React from 'react';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import {Box, InputLabel, MenuItem, Select, Typography} from "@mui/material";
import {useEffect, useState} from "react";
import {useCookies} from "react-cookie";

interface RegistoFormProps {
    open: boolean,
    onHandleOpen: () => void;
    onHandleClose: () => void;
}

interface ObraOptions {
    oid: number;
    name: string;
    status: 'completed' | 'pending' | 'rejected';
}

interface ObrasOptionsOutputModel {
    obras: ObraOptions[];
}


export default function RegistoForm(props: RegistoFormProps) {
    const [cookies] = useCookies(["token"]);
    const [obras, setObras] = useState<ObrasOptionsOutputModel | null>(null);
    const [selectedObras, setSelectedObras] = useState<number | undefined>(undefined);
    const [requiredDateTime, setRequiredDateTime] = useState<string>('');
    const [optionalDateTime, setOptionalDateTime] = useState<string>('');

    useEffect(() => {
        fetch("/api/obras/ongoing", {
            method: "GET",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`,
            },
        })
            .then(res => {
                console.log(res);
                if (res.ok) {
                    return res.json();
                } else if (res.status == 404) {
                    return { obras: []}
                } else {
                    return null;
                }

            })
            .then(body => {
                if (body) {
                    setObras(body);
                } else {
                    setObras({ obras: [] });
                }
            })
            .catch(error => {
                console.error("Error fetching obras: ", error);
                setObras({ obras: [] });
            });
    }, [cookies.token, props.open])


    return (
        <React.Fragment>
            <Dialog
                open={props.open}
                onClose={props.onHandleClose}
                PaperProps={{
                    component: 'form',
                    onSubmit: (event: React.FormEvent<HTMLFormElement>) => {
                        event.preventDefault();
                        const formData = new FormData(event.currentTarget);
                        const formJson = Object.fromEntries((formData as any).entries());
                        const id = formJson.obra;
                        console.log(selectedObras + " -> " + requiredDateTime + " -> " + optionalDateTime);
                        fetch(`/api/obras/${selectedObras}/register`, {
                            method: "POST",
                            headers: {
                                "Content-type": "application/json",
                                "Authorization": `Bearer ${cookies.token}`,
                            },
                            body: JSON.stringify({
                                startTime: requiredDateTime,
                                endTime: optionalDateTime
                            })
                        }).then(res => {
                            if (res.ok) props.onHandleClose()
                            props.onHandleClose()
                        })
                        event.stopPropagation()
                    },
                }}
            >
                <DialogTitle>Subscrever</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Escolha a obra e o horário de entrada e saída.
                    </DialogContentText>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                        <InputLabel id="obra">Selecione a obra</InputLabel>
                        {obras ? (
                                obras.obras.length > 0 ? (
                                    <Select
                                        value={selectedObras || ''}
                                        onChange={(e) => setSelectedObras(e.target.value as number)}
                                        label="Select Option 1"
                                    >
                                        {obras.obras.map((obra) =>
                                            <MenuItem key={obra.oid} value={obra.oid}>
                                                {obra.name}
                                            </MenuItem>
                                        )}
                                    </Select>
                                ) : (
                                    <Typography variant="body2" color="error">
                                        Sem obras disponíveis.
                                    </Typography>
                                )
                            ) : (
                                <Typography variant="body2" color="error">
                                    Erro ao carregar obras.
                                </Typography>
                            )}

                        <TextField
                            id="entrada"
                            label="Entrada"
                            type="datetime-local"
                            required
                            value={requiredDateTime}
                            onChange={(e) => setRequiredDateTime(e.target.value)}
                            InputLabelProps={{
                                shrink: true,
                            }}
                        />

                        <TextField
                            id="saida"
                            label="Saida"
                            type="datetime-local"
                            required
                            value={optionalDateTime}
                            onChange={(e) => setOptionalDateTime(e.target.value)}
                            InputLabelProps={{
                                shrink: true,
                            }}
                        />
                    </Box>

                </DialogContent>
                <DialogActions>
                    <Button onClick={props.onHandleClose}>Cancelar</Button>
                    <Button type="submit">Submeter</Button>
                </DialogActions>
            </Dialog>
        </React.Fragment>
    );
}
