import {ChangeEvent, FormEvent, useState} from "react";
import * as React from "react";
import { useCookies } from "react-cookie";
import Box from '@mui/material/Box';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Alert from '@mui/material/Alert';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import { styled } from '@mui/material/styles';
import {colors, FormControl, InputLabel, MenuItem, Select, Snackbar} from "@mui/material";
import {Navigate, useParams} from "react-router-dom"

interface InviteValues {
    email: string
    function: string
}

const roles = [
    'Ajudante', 'Apontador', 'Armador de ferro', 'Arvorado', 'Calceteiro', 'Canalisador', 'Carpinteiro', 'Chefe de equipa', 'Condutor Manobrador', 'Diretor de serviços', 'Eletricista', 'Encarregado', 'Escriturário', 'Estucador',  'Ferramenteiro', 'Gruista', 'Impermiabilizador', 'Ladrilhador', 'Marteleiro', 'Montador de andaimes', 'Pedreiro', 'Pintor', 'Serralheiro', 'Servente', 'Soldador', 'Técnico de manutenção', 'Tubista', 'Outro'
];

export default function InviteToObra() {
    const [cookies] = useCookies(["token"]);
    const [values, setValues] = useState<InviteValues>({
        email: "",
        function: ""
    });


    const [submitted, setSubmitted] = useState(false);
    const [valid, setValid] = useState(false);
    const [error, setError] = useState<string | undefined>(undefined);
    const [redirect, setRedirect] = useState<JSX.Element | null>(null);
    const { oid } = useParams<{ oid: string }>();
    const [success, setSuccess] = useState(false);

    const handleInputChange = (event: ChangeEvent<HTMLInputElement>) => {
        const { name, value } = event.target;
        setValues((values) => ({
            ...values,
            [name]: value
        }));
    };


    const handleSelectChange = (event: ChangeEvent<{ value: unknown }>) => {
        setValues((values) => ({
            ...values,
            function: event.target.value as string
        }));
    };

    const handleSubmit = (e: FormEvent) => {
        e.preventDefault();

        console.log("Form values:", values);

        fetch(`/api/obras/${oid}/invite`, {
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
                setSuccess(true);
                // setRedirect(<Navigate to="/obras" replace={true} />)
                //setRedirect(<Navigate to="/obras/" state={{ success: true }} replace={true} />);
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
                <h1 className="black-text">Convidar funcionário</h1>
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
                <Button type="submit" variant="contained" color="primary" sx={{ m: 1 }}>
                    Convidar
                </Button>
                {submitted && !valid && <Alert severity="error" sx={{ m: 1 }}>{error}</Alert>}
                {submitted && valid && <Alert severity="success" sx={{ m: 1 }}>Funcionário convidado com sucesso!</Alert>}
            </Box>
        </div>
    );
}
