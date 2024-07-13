import {Stack} from "@mui/material";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import React, {useState} from "react";
import {useCookies} from "react-cookie";

interface ObraNFCFormProps {
    nfc: string | null;
    oid: string | null;
    setNfc: (nfc: string) => void;
}

interface ObraNFCInputModel {
    newNfc: string;
}

export default function ObraNFCForm({
    nfc,
    oid,
    setNfc
}: ObraNFCFormProps ) {
    const [isEditing, setIsEditing] = useState(false);
    const [cookies] = useCookies(['token']);
    const [values, setValues] = useState<ObraNFCInputModel>({newNfc: ""})

    const handleEditNFC = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        fetch(`/api/obras/${oid}/nfc`, {
            method: 'PUT',
            headers: {
                'Content-type': 'application/json',
                'Authorization': `Bearer ${cookies.token}`
            },
            body: values.newNfc
        })
            .then((res) => {
                if (res.ok) return res.json();
                else return null;
            })
            .then((body) => {
                setIsEditing(false);
                setNfc(values.newNfc);
            })
            .catch(error => {
                console.error("Error fetching: ", error);
            });
    }

    const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        event.preventDefault()
        const { name, value } = event.target;
        setValues((values) => ({
            ...values,
            [name]: value
        }));
    };


    return (
        <Stack sx={{ m: '2rem 0' }}>
            <Box
                sx={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                }}
            >
                <Typography variant="h4" color="black">NFC</Typography>
                <Box>
                    {isEditing ? (
                        <>
                            <Button variant="outlined" sx={{
                                m: 1,
                                borderColor: 'red',
                                color: 'red',
                                '&:hover': {
                                    borderColor: 'darkred',
                                    color: 'darkred',
                                }
                            }} onClick={() => setIsEditing(false)}>
                                Cancelar
                            </Button>
                            <Button variant="contained" color="primary" onClick={handleEditNFC}>
                                Guardar
                            </Button>
                        </>

                    ) : (
                        <Button variant="contained" color="primary" onClick={() => setIsEditing(true)}>
                            Editar
                        </Button>
                    )}
                </Box>

            </Box>
            {isEditing ? (
                <TextField
                    fullWidth
                    label="NFC"
                    variant="outlined"
                    name="newNfc"
                    value={values.newNfc}
                    onChange={handleInputChange}
                />
            ) : (
                <Typography variant="body1">{nfc}</Typography>
            )}
        </Stack>
    );

}



