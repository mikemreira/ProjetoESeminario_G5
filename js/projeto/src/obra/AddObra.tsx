import {ChangeEvent, FormEvent, useState} from "react";
import * as React from "react";
import { useCookies } from "react-cookie";
import Box from '@mui/material/Box';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Alert from '@mui/material/Alert';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import { styled } from '@mui/material/styles';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';
import {colors} from "@mui/material";

interface ObraValues {
    name: string;
    location: string;
    description: string;
    startDate: string;
    endDate: string | null;
    foto: string | null
    status: string | null;
}

export default function AddObra() {
    const [cookies] = useCookies(["token"]);
    const [values, setValues] = useState<ObraValues>({
        name: "",
        location: "",
        description: "",
        startDate: "",
        endDate: null,
        foto: null,
        status: null
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

        console.log("Form values:", values);

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
        <div className="form-obras">
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
        </div>
    );
}
