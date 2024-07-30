import {ChangeEvent, FormEvent, useState} from "react";
import * as React from "react";
import { useCookies } from "react-cookie";
import Box from '@mui/material/Box';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Alert from '@mui/material/Alert';
import {CircularProgress, FormControl, InputLabel, MenuItem, Select, Snackbar, Stack, Typography} from "@mui/material";
import {Navigate, useNavigate, useParams} from "react-router-dom"
import {path} from "../App";

interface InviteValues {
    email: string
    function: string
    role: string
}

const func = [
    'Ajudante', 'Apontador', 'Armador de ferro', 'Arvorado', 'Calceteiro', 'Canalizador', 'Carpinteiro', 'Chefe de equipa', 'Condutor Manobrador', 'Diretor de serviços', 'Eletricista', 'Encarregado', 'Escriturário', 'Estucador',  'Ferramenteiro', 'Gruista', 'Impermeabilizador', 'Ladrilhador', 'Marteleiro', 'Montador de andaimes', 'Pedreiro', 'Pintor', 'Serralheiro', 'Servente', 'Soldador', 'Técnico de manutenção', 'Tubista', 'Outro'
];

const roles = [
    { value: 'admin', label: 'Administrador' },
    { value: 'funcionario', label: 'Membro' }
]

export default function InviteToObra() {
    const [cookies] = useCookies(["token"]);
    const [values, setValues] = useState<InviteValues>({
        email: "",
        function: "",
        role: ""
    });

    const [submitted, setSubmitted] = useState(false);
    const [valid, setValid] = useState(false);
    const [error, setError] = useState<string | undefined>(undefined);
    const [redirect, setRedirect] = useState<JSX.Element | null>(null);
    const { oid } = useParams<{ oid: string }>();
    const [success, setSuccess] = useState(false);
    const navigate = useNavigate()

    const handleInputChange = (event: ChangeEvent<HTMLInputElement>) => {
        const { name, value } = event.target
        setValues((values) => ({
            ...values,
            [name]: value
        }))
    }

    const handleFunctionChange = (event: { target: { value: string; }; }) => {
        setValues((values) => ({
            ...values,
            function: event.target.value as string
        }))
    }

    const handleRoleChange = (event: { target: { value: string; }; }) => {
        setValues((values) => ({
            ...values,
            role: event.target.value as string
        }))
    }

    const handleSubmit = (e: FormEvent) => {
        e.preventDefault();
        fetch(`${path}/obras/${oid}/convite`, {
            method: "POST",
            headers: {
                'Content-type': 'application/json',
                'Authorization': `Bearer ${cookies.token}`
            },
            body: JSON.stringify(values)
        }).then(res => {
            setSubmitted(true);
            if (res.ok) {
                setValid(true);
                setSuccess(true);
                navigate(-1)
            } else {
                setValid(false);
            }
            return res.json();
        }).then(body => {
            if (!valid) {
                setError(body.error);
                console.log(body.error);
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
                <h1 className="black-text">Convidar membro</h1>
                <TextField
                    required
                    id="email"
                    label="Email"
                    value={values.email}
                    onChange={handleInputChange}
                    name="email"
                />
                <FormControl sx={{ m: 1, width: '25ch' }}>
                    <InputLabel id="function-label">Função</InputLabel>
                    <Select
                        labelId="function-label"
                        id="function"
                        value={values.function}
                        onChange={handleFunctionChange}
                        label="Função"
                        name="function"
                    >
                        {func.map((func) => (
                            <MenuItem key={func} value={func}>
                                {func}
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>
                <FormControl sx={{ m: 1, width: '25ch' }}>
                    <InputLabel id="role-label">Papel</InputLabel>
                    <Select
                        labelId="role-label"
                        id="role"
                        value={values.role}
                        onChange={handleRoleChange}
                        label="Papel"
                        name="role"
                    >
                        {roles.map((role) => (
                            <MenuItem key={role.value} value={role.value}>
                                {role.label}
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>
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
                    <Button type="submit" variant="contained" color="primary" sx={{ m: 1 }}>
                        Convidar
                    </Button>
                </Box>
                {success && (
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
                {submitted && valid && <Alert severity="success" sx={{ m: 1 }}>Membro convidado com sucesso!</Alert>}
            </Box>
        </div>
    );
}
