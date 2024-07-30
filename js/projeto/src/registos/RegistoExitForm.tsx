import * as React from 'react';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import {Box} from "@mui/material";
import {useState} from "react";
import {useCookies} from "react-cookie";
import {path} from "../App";
import {Registo} from "./Registos";

interface RegistoFormProps {
    open: boolean,
    onHandleClose: (reload: boolean) => void;
    registo: Registo | null;
}

export default function RegistoExitForm(props: RegistoFormProps) {
    const [cookies] = useCookies(["token"]);
    const [requiredDateTime, setRequiredDateTime] = useState<string>('');
    const obra_id = props.registo?.id_obra === undefined ? props.registo?.oid : props.registo?.id_obra;

    return (
        <React.Fragment>
            <Dialog
                open={props.open}
                onClose={() => props.onHandleClose(false)}
                PaperProps={{
                    component: 'form',
                    onSubmit: (event: React.FormEvent<HTMLFormElement>) => {
                        event.preventDefault();
                        fetch(`${path}/registos/incompletos`, {
                            method: "PUT",
                            headers: {
                                "Content-type": "application/json",
                                "Authorization": `Bearer ${cookies.token}`,
                            },
                            body: JSON.stringify({
                                endTime: requiredDateTime,
                                registerId: props.registo?.id,
                                oid: obra_id
                            })
                        }).then(res => {
                            console.log(res)
                            if (res.ok) {
                                props.onHandleClose(true);
                            } else {
                                props.onHandleClose(false);
                            }
                        })
                        event.stopPropagation()
                    },
                }}
            >
                <DialogTitle>Finalizar registo.</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Indique o horário de saída.
                    </DialogContentText>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                        <TextField
                            id="saida"
                            label="Saida"
                            type="datetime-local"
                            required
                            value={requiredDateTime}
                            onChange={(e) => setRequiredDateTime(e.target.value)}
                            InputLabelProps={{
                                shrink: true,
                            }}
                        />

                    </Box>

                </DialogContent>
                <DialogActions>
                    <Button onClick={() => props.onHandleClose(false)}>Cancelar</Button>
                    <Button type="submit">Submeter</Button>
                </DialogActions>
            </Dialog>
        </React.Fragment>
    );
}
