import {ChangeEvent, FormEvent, useState} from "react";
import * as React from "react";
import { useCookies } from "react-cookie";
import Box from '@mui/material/Box';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Alert from '@mui/material/Alert';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import { styled } from '@mui/material/styles';

interface ObraValues {
    name: string;
    location: string;
    description: string;
    start_date: string;
    end_date: string;
    foto: string;
    status: string;
}

export default function AddObra() {
    const [cookies] = useCookies(["token"]);
    const [values, setValues] = useState<ObraValues>({
        name: "",
        location: "",
        description: "",
        start_date: "",
        end_date: "",
        foto: "",
        status: "Ongoing"
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


    const [submitted, setSubmitted] = useState(false);
    const [valid, setValid] = useState(false);
    const [error, setError] = useState<string | undefined>(undefined);

    const handleInputChange = (event: ChangeEvent<HTMLInputElement>) => {
        const { name, value } = event.target;
        setValues((values) => ({
            ...values,
            [name]: value
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
            } else {
                setValid(false);
            }
            return res.json();
        }).then(body => {
            if (!valid) {
                setError(body.error);
                console.log(body.error);
            }
        }).catch(error => {
            setError(error.message);
        });
    };

    return (
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
            <h1>Adicionar Obra</h1>
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
                id="start_date"
                label="Data de Início"
                type="date"
                InputLabelProps={{
                    shrink: true,
                }}
                value={values.start_date}
                onChange={handleInputChange}
                name="start_date"
            />
            <TextField

                id="end_date"
                label="Data de Fim"
                type="date"
                InputLabelProps={{
                    shrink: true,
                }}
                value={values.end_date}
                onChange={handleInputChange}
                name="end_date"
            />
            <TextField
                id="foto"
                label="Foto"
                value={values.foto}
                onChange={handleInputChange}
                name="foto"
            />
            <Button
                component="label"
                role={undefined}
                variant="contained"
                tabIndex={-1}
                startIcon={<CloudUploadIcon sx={{ color: 'white' }} />}
            >
                Add Foto
                <VisuallyHiddenInput type="file" />
            </Button>
            <Button type="submit" variant="contained" color="primary" sx={{ m: 1 }}>
                Adicionar
            </Button>
            {submitted && valid && <Alert severity="success" sx={{ m: 1 }}>Obra adicionada com sucesso!</Alert>}
            {submitted && !valid && <Alert severity="error" sx={{ m: 1 }}>{error}</Alert>}
        </Box>
    );
}
