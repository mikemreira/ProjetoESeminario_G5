import * as React from 'react';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import { Box, InputLabel, MenuItem, Select } from "@mui/material";
import { useState, ChangeEvent } from "react";
import { useCookies } from "react-cookie";

interface AddObrasFormProps {
    open: boolean,
    onHandleOpen: () => void;
    onHandleClose: () => void;
}

interface ObraValues {
    name: string
    location: string
    description: string
    startDate: string
    endDate: string | null
    foto: string | null
    status: string | null
    function: string
}

const roles = [
    'Ajudante', 'Apontador', 'Armador de ferro', 'Arvorado', 'Calceteiro', 'Canalisador', 'Carpinteiro', 'Chefe de equipa', 'Condutor Manobrador', 'Diretor de serviços', 'Eletricista', 'Encarregado', 'Escriturário', 'Estucador',  'Ferramenteiro', 'Gruista', 'Impermiabilizador', 'Ladrilhador', 'Marteleiro', 'Montador de andaimes', 'Pedreiro', 'Pintor', 'Serralheiro', 'Servente', 'Soldador', 'Técnico de manutenção', 'Tubista', 'Outro'
];

export default function AddObrasForm(props: AddObrasFormProps) {
    const [cookies] = useCookies(["token"]);
    const [values, setValues] = useState<ObraValues>({
        name: "",
        location: "",
        description: "",
        startDate: "",
        endDate: null,
        foto: "",
        status: null,
        function: ""
    });
    const [requiredName, setRequiredName] = useState<string>('');
    const [requiredDescription, setRequiredDescripiton] = useState<string>('');
    const [requiredLocation, setRequiredLocation] = useState<string>('');
    const [requiredDateTime, setRequiredDateTime] = useState<string>('');
    const [optionalDateTime, setOptionalDateTime] = useState<string>('');

    const handleInputChange = (event: ChangeEvent<HTMLInputElement>) => {
        const { name, value } = event.target;
        setValues((values) => ({
            ...values,
            [name]: value
        }));
    };

    const handleFileChange = (event: ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0];
        if (file) {
            const reader = new FileReader();
            reader.onloadend = () => {
                setValues((values) => ({
                    ...values,
                    foto: reader.result as string
                }));
            };
            reader.readAsDataURL(file);
        }
    };

    const handleSelectChange = (event: ChangeEvent<{ value: unknown }>) => {
        setValues((values) => ({
            ...values,
            function: event.target.value as string
        }));
    };

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
                        fetch("/api/obras", {
                            method: "POST",
                            headers: {
                                "Content-type": "application/json",
                                "Authorization": `Bearer ${cookies.token}`,
                            },
                            body: JSON.stringify({
                                name: requiredName,
                                description: requiredDescription,
                                location: requiredLocation,
                                startTime: requiredDateTime,
                                endTime: optionalDateTime
                            })
                        }).then(res => {
                            if (res.ok) props.onHandleClose();
                            props.onHandleClose()
                        }).catch(error => {
                            console.error("Error adding obra: ", error);
                        });
                        event.stopPropagation();
                    },
                }}
            >
                <DialogTitle>Adicionar nova obra</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Para adicionar uma nova obra, preencha os campos abaixo.
                    </DialogContentText>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                        <TextField
                            id="nome"
                            name="nome"
                            label="Nome"
                            value={requiredName}
                            onChange={(e) => setRequiredName(e.target.value)}
                            required
                            InputLabelProps={{
                                shrink: true,
                            }}
                        />
                        <TextField
                            id="descricao"
                            name="descricao"
                            label="Descrição"
                            value={requiredDescription}
                            onChange={(e) => setRequiredDescripiton(e.target.value)}
                            required
                            InputLabelProps={{
                                shrink: true,
                            }}
                        />
                        <TextField
                            required
                            id="location"
                            label="Localização"
                            value={requiredLocation}
                            onChange={(e) => setRequiredLocation(e.target.value)}
                            name="location"
                            InputLabelProps={{
                                shrink: true,
                            }}
                        />
                        <TextField
                            required
                            id="startDate"
                            label="Data de Início"
                            type={"date"}
                            InputLabelProps={{
                                shrink: true,
                            }}
                            value={requiredDateTime}
                            onChange={(e) => setRequiredDateTime(e.target.value)}
                            name="startDate"
                        />
                        <TextField
                            id="endDate"
                            label="Data de Fim"
                            type="date"
                            InputLabelProps={{
                                shrink: true,
                            }}
                            value={optionalDateTime}
                            onChange={(e) => setOptionalDateTime(e.target.value)}
                            name="endDate"
                        />
                        <InputLabel id="function-label">Função</InputLabel>
                        <Select
                            labelId="function-label"
                            id="function"
                            name="function"
                            value={values.function}
                            onChange={handleSelectChange}
                            required
                        >
                            {roles.map((role, index) => (
                                <MenuItem key={index} value={role}>
                                    {role}
                                </MenuItem>
                            ))}
                        </Select>
                        <input
                            accept="image/*"
                            id="foto"
                            type="file"
                            onChange={handleFileChange}
                            style={{ display: 'none' }}
                        />
                        <label htmlFor="foto">
                            <Button variant="contained" color="primary" component="span">
                                Adicionar foto
                            </Button>
                        </label>
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={props.onHandleClose}>Cancel</Button>
                    <Button type="submit">Submit</Button>
                </DialogActions>
            </Dialog>
        </React.Fragment>
    );
}