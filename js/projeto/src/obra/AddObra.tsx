import {ChangeEvent, FormEvent, useState} from "react";
import * as React from "react";
import { useCookies } from "react-cookie";
import Box from '@mui/material/Box';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Alert from '@mui/material/Alert';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import { styled } from '@mui/material/styles';
import {CircularProgress, FormControl, InputLabel, MenuItem, Select, Snackbar, Stack} from "@mui/material";
import {Navigate, useNavigate} from "react-router-dom"
import Typography from "@mui/material/Typography";
import {path} from "../App";
import {handleFileChange, handleInputChange, handleSelectChange, VisuallyHiddenInput} from "../Utils";

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
    'Ajudante', 'Apontador', 'Armador de ferro', 'Arvorado', 'Calceteiro', 'Canalizador', 'Carpinteiro', 'Chefe de equipa', 'Condutor Manobrador', 'Diretor de serviços', 'Eletricista', 'Encarregado', 'Escriturário', 'Estucador',  'Ferramenteiro', 'Gruista', 'Impermeabilizador', 'Ladrilhador', 'Marteleiro', 'Montador de andaimes', 'Pedreiro', 'Pintor', 'Serralheiro', 'Servente', 'Soldador', 'Técnico de manutenção', 'Tubista', 'Outro'
];

export default function AddObra() {
    const [cookies] = useCookies(["token"] || undefined);
    const [submitted, setSubmitted] = useState(false);
    const [valid, setValid] = useState(false);
    const [error, setError] = useState<string | undefined>(undefined);
    const [redirect, setRedirect] = useState<JSX.Element | null>(null);
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [values, setValues] = useState<ObraValues>({
        name: "",
        location: "",
        description: "",
        startDate: "",
        endDate: null,
        foto: null,
        status: null,
        function: ""
    });

    const handleInputChangeAddObra = handleInputChange(setValues);
    const handleFileChangeAddObra = handleFileChange(setValues);
    const handleSelectChangeAddObra = handleSelectChange(setValues);

    const handleSubmit = (e: FormEvent) => {
        e.preventDefault();
        setLoading(true);
        fetch(`${path}/obras`, {
            method: "POST",
            headers: {
                'Content-type': 'application/json',
                'Authorization': `Bearer ${cookies.token}`
            },
            body: JSON.stringify(values)
        }).then(res => {
            setSubmitted(true);
            console.log(res);
            if (res.status === 201) {
                setValid(true);
                setRedirect(<Navigate to="/obras" state={{ success: true }} replace={true} />);
            } else {
                setValid(false);
                setLoading(false);
            }
            return res.json();
        }).then(body => {
            if (!valid) {
                setError(body.error);
                setLoading(false);
            } else {
                setRedirect(<Navigate to="/obras" replace={true} />)
            }
        }).catch(error => {
            setError(error.message);
        });
    };

    const handleCancel = () => {
        navigate(-1)
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
        <div className="form-obras">
            {redirect}
        <Box
            component="form"
            sx={{
                '& .MuiTextField-root': { m: 1, width: '25ch' },
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center'
            }}
            noValidate
            autoComplete="off"
            onSubmit={handleSubmit}
        >
            <h1 className="black-text">Adicionar Obra</h1>
            <TextField
                required
                id="name"
                label="Nome"
                value={values.name}
                onChange={handleInputChangeAddObra}
                name="name"
            />
            <TextField
                required
                id="location"
                label="Localização"
                value={values.location}
                onChange={handleInputChangeAddObra}
                name="location"
            />
            <TextField
                required
                id="description"
                label="Descrição"
                value={values.description}
                onChange={handleInputChangeAddObra}
                name="description"
            />
            <TextField
                required
                id="startDate"
                label="Data de Início"
                type={"date"}
                InputLabelProps={{
                    shrink: true,
                }}
                value={values.startDate}
                onChange={handleInputChangeAddObra}
                name="startDate"
            />
            <TextField
                id="endDate"
                label="Data de Fim"
                type="date"
                InputLabelProps={{
                    shrink: true,
                }}
                value={values.endDate}
                onChange={handleInputChangeAddObra}
                name="endDate"
            />
            <FormControl sx={{ m: 1, width: '25ch' }}>
                <InputLabel id="function-label">Função</InputLabel>
                <Select
                    labelId="function-label"
                    id="function"
                    value={values.function}
                    onChange={handleSelectChangeAddObra}
                    label="Função"
                    name="function"
                >
                    {roles.map((role) => (
                        <MenuItem key={role} value={role}>
                            {role}
                        </MenuItem>
                    ))}
                </Select>
            </FormControl>
            <Button
                component="label"
                role={undefined}
                variant="contained"
                tabIndex={-1}
                startIcon={<CloudUploadIcon sx={{ color: 'white' }} />}
            >
                Adicionar Foto
                <VisuallyHiddenInput
                    type="file"
                    onChange={handleFileChangeAddObra}
                />
            </Button>
            <Box sx={{ display: 'flex', justifyContent: 'center', width: '100%' }}>
                <Button variant="outlined" color="primary"  sx={{
                    m: 1,
                    borderColor: 'red',
                    color: 'red',
                    '&:hover': {
                        borderColor: 'darkred',
                        color: 'darkred',
                    }
                }}  onClick={handleCancel}>
                    Cancelar
                </Button>
                <Button type="submit" variant="contained" sx={{ m: 1, backgroundColor: 'green', '&:hover': { backgroundColor: 'darkgreen' } }}>
                    Adicionar
                </Button>
            </Box>
            {loading && (
                <Box
                    sx={{
                        display: 'flex',
                        justifyContent: 'center',
                        alignItems: 'center',
                    }}
                >
                    <CircularProgress />
                </Box>
            )}
            {submitted && !valid && <Alert severity="error" sx={{ m: 1 }}>{error}</Alert>}
        </Box>
        </div>
    );
}
