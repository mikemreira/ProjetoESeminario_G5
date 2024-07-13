import {Paper, Stack} from "@mui/material";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import TextField from "@mui/material/TextField";
import React, {useState} from "react";
import {useCookies} from "react-cookie";
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import CancelIcon from '@mui/icons-material/Cancel';
import SaveIcon from '@mui/icons-material/Save';
import {path} from "../../App";

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
        fetch(`${path}/obras/${oid}/nfc`, {
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
            </Box>
            {isEditing ? (
                <TextField
                    fullWidth
                    label="NFC ID"
                    variant="outlined"
                    name="newNfc"
                    value={values.newNfc}
                    onChange={handleInputChange}
                    sx={{ mt: 2 }}
                />
            ) : (
                <Paper sx={{ p: 2, mt: 2, backgroundColor: '#CACCD0', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                    <Box>
                        <Typography variant="body2" color="textSecondary">NFC ID</Typography>
                        <Typography variant="body1">{nfc}</Typography>
                    </Box>
                    <IconButton color="primary" title={"Configurar NFC"} onClick={() => setIsEditing(true)}>
                        <EditIcon />
                    </IconButton>
                </Paper>
            )}
            {isEditing && (
                <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2 }}>
                    <>
                        <IconButton sx={{ color: 'red' }} title={"Cancelar"} onClick={() => setIsEditing(false)}>
                            <CancelIcon />
                        </IconButton>
                        <IconButton color={"primary"} title={"Guardar"} onClick={handleEditNFC}>
                            <SaveIcon />
                        </IconButton>
                    </>
                </Box>
            )}
        </Stack>
    );

}



