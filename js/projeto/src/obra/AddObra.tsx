import {ChangeEvent, FormEvent, useState} from "react";
import * as React from "react";
import { useCookies } from "react-cookie";
import Box from '@mui/material/Box';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Alert from '@mui/material/Alert';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import { styled } from '@mui/material/styles';
import {FormControl, InputLabel, MenuItem, Select, Snackbar, Stack} from "@mui/material";
import {Navigate, useNavigate} from "react-router-dom"
import Typography from "@mui/material/Typography";

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

export default function AddObra() {
    const [cookies] = useCookies(["token"]);
    const [submitted, setSubmitted] = useState(false);
    const [valid, setValid] = useState(false);
    const [error, setError] = useState<string | undefined>(undefined);
    const [redirect, setRedirect] = useState<JSX.Element | null>(null);
    const [open, setOpen] = React.useState(false);
    const navigate = useNavigate();
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

    const VisuallyHiddenInput = styled('input')({
        clip: 'rect(0 0 0 0)',
        clipPath: 'inset(50%)',
        height: 1,
        overflow: 'hidden',
        position: 'absolute',
        bottom: 0,
        left: 0,
        whiteSpace: 'nowrap',
        width: 1,
    });

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

    const handleSelectChange = (event: { target: { value: string; }; }) => {
        setValues((values) => ({
            ...values,
            function: event.target.value as string
        }));
    };

    const handleSubmit = (e: FormEvent) => {
        e.preventDefault();

        fetch("/api/obras", {
            method: "POST",
            headers: {
                'Content-type': 'application/json',
                'Authorization': `Bearer ${cookies.token}`
            },
            body: JSON.stringify(values)
        }).then(res => {
            setSubmitted(true);
            if (res.status === 201) {
                setValid(true);
                setRedirect(<Navigate to="/obras" state={{ success: true }} replace={true} />);
            } else {
                setValid(false);
            }
            return res.json();
        }).then(body => {
            if (!valid) {
                setError(body.error);
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
                onChange={handleInputChange}
                name="name"
            />
            <TextField
                required
                id="location"
                label="Localização"
                value={values.location}
                onChange={handleInputChange}
                name="location"
            />
            <TextField
                required
                id="description"
                label="Descrição"
                value={values.description}
                onChange={handleInputChange}
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
                onChange={handleInputChange}
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
                onChange={handleInputChange}
                name="endDate"
            />
            <FormControl sx={{ m: 1, width: '25ch' }}>
                <InputLabel id="function-label">Função</InputLabel>
                <Select
                    labelId="function-label"
                    id="function"
                    value={values.function}
                    onChange={handleSelectChange}
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
                    onChange={handleFileChange}
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
            {submitted && !valid && <Alert severity="error" sx={{ m: 1 }}>{error}</Alert>}
        </Box>
        </div>
    );
}
