import {Avatar, Card, CardContent, Divider, Grid, List, ListItem, ListItemText, Typography} from "@mui/material";
import * as React from "react";
import { UserModel } from '../ObrasInfo';

interface ObraFuncionarioInfoFormProps {
    user: UserModel
}

export default function ObraFuncionarioInfoForm({ user }: ObraFuncionarioInfoFormProps) {
    return (
     <>
         <Card sx={{ boxShadow: 3 }}>
             <CardContent>
                 <Typography variant="h3" gutterBottom>
                     Perfil
                 </Typography>
                 <Grid container spacing={2}>
                     <Grid item xs={12} md={4}>
                         <Avatar
                             alt={user.nome}
                             src={user?.foto || "https://cdn-icons-png.flaticon.com/512/3135/3135715.png"}
                             variant="rounded"
                             sx={{
                                 width: "100%",
                                 height: "100%",
                                 cursor: "default",
                             }}
                         />
                     </Grid>
                     <Grid item xs={12} md={8}>
                         <List>
                             <ListItem>
                                 <ListItemText
                                     primary="Nome"
                                     secondary={user.nome}
                                     primaryTypographyProps={{ style: { color: "#0000FF" } }}
                                 />
                             </ListItem>
                             <Divider />
                             <ListItem>
                                 <ListItemText
                                     primary="Email"
                                     secondary={user.email}
                                     primaryTypographyProps={{ style: { color: "#0000FF" } }}
                                 />
                             </ListItem>
                             <Divider />
                             <ListItem>
                                 <ListItemText
                                     primary="Morada"
                                     secondary={user.morada}
                                     primaryTypographyProps={{ style: { color: "#0000FF" } }}
                                 />
                             </ListItem>
                             <Divider />
                         </List>
                     </Grid>
                 </Grid>
             </CardContent>
         </Card>
     </>
    )
}