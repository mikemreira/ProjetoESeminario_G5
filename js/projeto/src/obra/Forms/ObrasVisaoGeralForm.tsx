import React, { ChangeEvent } from "react";
import {
    Badge,
    Box,
    Button,
    Divider,
    IconButton,
    List,
    ListItem,
    ListItemText,
    Select,
    MenuItem,
    TextField,
    Tooltip,
    Typography
} from "@mui/material";
import PendingActionsIcon from '@mui/icons-material/PendingActions';
import ClearIcon from '@mui/icons-material/Clear';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import EditIcon from '@mui/icons-material/Edit';
import ArrowCircleUpIcon from '@mui/icons-material/ArrowCircleUp';
import DeleteIcon from '@mui/icons-material/Delete';
import NfcIcon from '@mui/icons-material/Nfc';

import {RegistosOutputModel, DateObject, Obra, UserRegistersAndObraOutputModel} from '../ObrasInfo';

interface VisaoGeralProps {
    obra: Obra;
    editedObra: Obra | undefined;
    pendingRegisters: UserRegistersAndObraOutputModel;
    isEditing: boolean;
    handleChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
    handleSelectChange: (event: ChangeEvent<{ name?: string; value: unknown }>) => void;
    handleFileChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
    handleClickPendingRegisters: (pageNumber: number) => void;
    handleNFC: () => void;
    handleClickEditObra: () => void;
    handleSuspendOrRecover: (status: string) => void;
    handleSaveObra: () => void;
    handleCancelEdit: () => void;
    pageNumber: number;
}

const func = [
    'Ajudante', 'Apontador', 'Armador de ferro', 'Arvorado', 'Calceteiro', 'Canalizador', 'Carpinteiro', 'Chefe de equipa', 'Condutor Manobrador', 'Diretor de serviços', 'Eletricista', 'Encarregado', 'Escriturário', 'Estucador', 'Ferramenteiro', 'Gruista', 'Impermeabilizador', 'Ladrilhador', 'Marteleiro', 'Montador de andaimes', 'Pedreiro', 'Pintor', 'Serralheiro', 'Servente', 'Soldador', 'Técnico de manutenção', 'Tubista', 'Outro'
]

const formatDate = (dateObj: DateObject | null | undefined): string => {
    if (!dateObj || !dateObj.value$kotlinx_datetime) {
        return "Sem data definida";
    }
    return dateObj.value$kotlinx_datetime.split('T')[0];
}

export default function ObraVisaoGeralForm({
    obra,
    editedObra,
    pendingRegisters,
    isEditing,
    handleChange,
    handleSelectChange,
    handleClickPendingRegisters,
    handleNFC,
    handleClickEditObra,
    handleSuspendOrRecover,
    handleSaveObra,
    handleCancelEdit,
    pageNumber
}: VisaoGeralProps) {
    const handleSuspendObra = () => handleSuspendOrRecover("recoverable");
    const handleCompletedObra = () => handleSuspendOrRecover("completed");
    const handleRecoverObra = () => handleSuspendOrRecover("on going");
    const handleDeleteObra = () => handleSuspendOrRecover("deleted");

    return (
        <>
            {obra.role === "admin" && !isEditing && (obra.status === "on going") && (
                <Box display="flex" justifyContent="flex-end" alignItems="center">
                    <Tooltip title="Registos Pendentes">
                        <IconButton color="primary" onClick={() => handleClickPendingRegisters(pageNumber)}>
                            <Badge badgeContent={pendingRegisters.registers ? pendingRegisters.registers.length : 0} color="warning">
                                <PendingActionsIcon sx={{ color: 'black' }} />
                            </Badge>
                        </IconButton>
                    </Tooltip>
                </Box>
            )}
            <Typography variant="h4" component="h2" gutterBottom>
                Informações da Obra
            </Typography>
            <List>
                <ListItem>
                    <ListItemText primary="Nome" secondary={
                        isEditing ? (
                            <TextField
                                fullWidth
                                name="name"
                                value={editedObra?.name || ""}
                                onChange={handleChange}
                            />
                        ) : (
                            obra.name
                        )
                    } primaryTypographyProps={{ style: { color: '#0000FF' } }} />
                </ListItem>
                <Divider />
                <ListItem>
                    <ListItemText primary="Localização" secondary={
                        isEditing ? (
                            <TextField
                                fullWidth
                                name="location"
                                value={editedObra?.location || ""}
                                onChange={handleChange}
                            />
                        ) : (
                            obra.location
                        )
                    } primaryTypographyProps={{ style: { color: '#0000FF' } }} />
                </ListItem>
                <Divider />
                <ListItem>
                    <ListItemText primary="Descrição" secondary={
                        isEditing ? (
                            <TextField
                                fullWidth
                                name="description"
                                value={editedObra?.description || ""}
                                onChange={handleChange}
                            />
                        ) : (
                            obra.description
                        )
                    } primaryTypographyProps={{ style: { color: '#0000FF' } }} />
                </ListItem>
                <Divider />
                <ListItem>
                    <ListItemText primary="Data de Início" secondary={
                        isEditing ? (
                            <TextField
                                fullWidth
                                name="startDate"
                                type={"date"}
                                value={editedObra?.startDate || ""}
                                onChange={handleChange}
                            />
                        ) : (
                            formatDate(obra.startDate)
                        )
                    } primaryTypographyProps={{ style: { color: '#0000FF' } }} />
                </ListItem>
                <Divider />
                <ListItem>
                    <ListItemText primary="Data de Fim" secondary={
                        isEditing ? (
                            <TextField
                                fullWidth
                                name="endDate"
                                type={"date"}
                                value={editedObra?.endDate || ""}
                                onChange={handleChange}
                            />
                        ) : (
                            formatDate(obra.endDate)
                        )
                    } primaryTypographyProps={{ style: { color: '#0000FF' } }} />
                </ListItem>
                <Divider />
                <ListItem>
                    <ListItemText primary="Estado" secondary={
                        obra.status
                    } primaryTypographyProps={{ style: { color: '#0000FF' } }} />
                </ListItem>
                <Divider />
                <ListItem>
                    <ListItemText primary="Função" secondary={
                        isEditing ? (
                            <>
                                <Select
                                    labelId="function-label"
                                    id="function"
                                    name="function"
                                    value={editedObra?.function || ""}
                                    onChange={handleSelectChange}
                                    required
                                    fullWidth
                                >
                                    {func.map((role, index) => (
                                        <MenuItem key={index} value={role}>
                                            {role}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </>
                        ) : (
                            obra.function
                        )
                    } primaryTypographyProps={{ style: { color: '#0000FF' } }} />
                </ListItem>
            </List>
            <Box mt={2}>
                {!isEditing && (
                    <>
                        {obra.role === "admin" && obra.status === "on going" && (
                            <>
                                <IconButton variant="contained" color="primary" sx={{ ml: 2 }} onClick={handleClickEditObra} title="Editar">
                                    <EditIcon />
                                </IconButton>
                                <IconButton variant="contained" sx={{ ml: 2 }} onClick={handleSuspendObra} title="Suspender">
                                    <ClearIcon sx={{ color: 'red' }} />
                                </IconButton>
                                <IconButton variant="contained" sx={{ ml: 2 }} onClick={handleCompletedObra} title="Terminar" >
                                    <CheckCircleIcon sx={{ color: 'green' }} />
                                </IconButton>
                            </>
                        )}
                        {obra.role === "admin" && (obra.status === "recoverable") && (
                            <>
                                <IconButton variant="contained" sx={{ ml: 2 }} onClick={handleRecoverObra} title="Recuperar">
                                    <ArrowCircleUpIcon sx={{ color: 'green' }} />
                                </IconButton>
                                <IconButton variant="contained" sx={{ ml: 2 }} onClick={handleDeleteObra} title="Eliminar">
                                    <DeleteIcon sx={{ color: 'red' }} />
                                </IconButton>
                            </>
                        )}
                        {obra.role === "admin" && (
                            <IconButton variant="contained" sx={{ ml: 2 }} onClick={handleNFC} title="NFC">
                                <NfcIcon sx={{ color: 'black' }} />
                            </IconButton>
                        )}
                    </>
                )}
                {isEditing && (
                    <>
                        <Button variant="outlined" color="primary"  sx={{
                            m: 1,
                            borderColor: 'red',
                            color: 'red',
                            '&:hover': {
                                borderColor: 'darkred',
                                color: 'darkred',
                            }
                        }}  onClick={handleCancelEdit}>
                            Cancelar
                        </Button>
                        <Button variant="contained" color="primary" onClick={handleSaveObra}>
                            Guardar
                        </Button>
                    </>
                )}
            </Box>
        </>
    );
};
