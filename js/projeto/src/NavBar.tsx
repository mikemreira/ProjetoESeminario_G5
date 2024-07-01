import "./styles.css";
import { Link, useNavigate } from "react-router-dom";
import { useAvatar, useCurrentUser } from "./context/Authn.tsx";
import * as React from 'react';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import Menu from '@mui/material/Menu';
import Container from '@mui/material/Container';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import Tooltip from '@mui/material/Tooltip';
import MenuItem from '@mui/material/MenuItem';
import { Badge } from "@mui/material";
import NotificationsIcon from '@mui/icons-material/Notifications';
import { useEffect, useState } from "react";
import { useCookies } from "react-cookie";
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CheckIcon from '@mui/icons-material/Check';
import DoDisturbIcon from '@mui/icons-material/DoDisturb';
import CloseIcon from '@mui/icons-material/Close';

interface DateObject {
    year: number;
    dayOfMonth: number;
    month: string;
    dayOfWeek: string;
    dayOfYear: number;
    monthNumber: number;
    value$kotlinx_datetime: string;
}

interface Obra {
    oid: number;
    name: string;
    location: string;
    description: string;
    startDate: DateObject | null;
    endDate: DateObject | null;
    status: string;
    foto: string | null;
    function: string;
}

interface InviteResponse {
    oid: number;
    response: string;
}

interface InvitesModel {
    obrasAndRole: Obra[];
}

function NavBar() {
    const [cookies] = useCookies(["token"]);
    const currentUser = useCurrentUser();
    const currentUserAvatar = useAvatar();
    const [anchorElUser, setAnchorElUser] = React.useState(null);
    const [anchorElNotifications, setAnchorElNotifications] = React.useState(null);
    const [invites, setInvites] = useState<InvitesModel>({ obrasAndRole: [] });

    useEffect(() => {
        fetch(`/api/convites`, {
            method: "GET",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`
            },
        }).then((res) => {
            if (res.ok) return res.json();
            else return null;
        }).then((body) => {
            if (body) {
                setInvites(body);
            }
        }).catch(error => {
            console.error("Error fetching notifications: ", error);
        });
    }, [cookies.token]);

    const handleOpenUserMenu = (event) => {
        setAnchorElUser(event.currentTarget);
    };

    const handleCloseUserMenu = () => {
        setAnchorElUser(null);
    };

    const handleOpenNotificationsMenu = (event) => {
        setAnchorElNotifications(event.currentTarget);
    };

    const handleCloseNotificationsMenu = () => {
        setAnchorElNotifications(null);
    };

    const navigate = useNavigate();

    const handleClickHome = () => {
        navigate(`/`);
    };

    const handleAcceptOrRejectInvite = (oid: number, response: string) => {
        fetch(`/api/convites`, {
            method: 'PUT',
            headers: {
                'Content-type': 'application/json',
                'Authorization': `Bearer ${cookies.token}`
            },
            body: JSON.stringify({ oid: oid, response: response })
        })
            .then((res) => {
                if (res.ok) return res.json();
                throw new Error('Falha ao aceitar o convite.');
            })
            .then(() => {
                setInvites(prevInvites => ({
                    ...prevInvites,
                    obrasAndRole: prevInvites.obrasAndRole.filter(invite => invite.oid !== oid)
                }));
            })
            .catch(() => {
                navigate(`/obras`)
            });
    };

    return (
        <AppBar position="fixed">
            <Container maxWidth="xl">
                <Toolbar disableGutters>
                    <Button
                        onClick={handleClickHome}
                        sx={{
                            mr: 3,
                            display: { xs: 'none', md: 'flex' },
                            fontFamily: 'monospace',
                            fontWeight: 700,
                            letterSpacing: '.3rem',
                            color: 'inherit',
                            textDecoration: 'none',
                            fontSize: '1.5rem',
                        }}
                    >
                        Registo de acessos
                    </Button>
                    {currentUser ? (
                        <Box sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' } }}>
                            <Button
                                className="custom-link"
                                component={Link}
                                to="/obras"
                                sx={{ my: 2, color: 'white', display: 'inline' }}
                            >
                                Obras
                            </Button>
                            <Button
                                className="custom-link"
                                component={Link}
                                to="/registos"
                                sx={{ my: 2, color: 'white', display: 'inline' }}
                            >
                                Registos
                            </Button>
                        </Box>
                    ) : (
                        <Box sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' } }}></Box>
                    )}

                    {currentUser ? (
                        <Box sx={{ flexGrow: 0, display: 'flex', alignItems: 'center' }}>
                            <Tooltip title="Notificações">
                                <IconButton onClick={handleOpenNotificationsMenu} sx={{ p: 1, color: 'white', mr: 1 }}>
                                    <Badge badgeContent={invites.obrasAndRole.length} color="warning">
                                        <NotificationsIcon sx={{ color: 'white' }} />
                                    </Badge>
                                </IconButton>
                            </Tooltip>
                            <Menu
                                id="notifications-menu"
                                anchorEl={anchorElNotifications}
                                anchorOrigin={{
                                    vertical: 'top',
                                    horizontal: 'right',
                                }}
                                keepMounted
                                transformOrigin={{
                                    vertical: 'top',
                                    horizontal: 'right',
                                }}
                                open={Boolean(anchorElNotifications)}
                                onClose={handleCloseNotificationsMenu}
                            >
                                {invites.obrasAndRole.length > 0 ? (
                                    invites.obrasAndRole.map((invite) => (
                                        <MenuItem key={invite.oid}>
                                            Convite para a obra {invite.name} com a função: {invite.function}
                                            <IconButton onClick={(e) => { e.stopPropagation(); handleAcceptOrRejectInvite(invite.oid, "accepted") }}>
                                                <CheckIcon sx={{ color: 'green' }}/>
                                            </IconButton>
                                            <IconButton onClick={(e) => { e.stopPropagation(); handleAcceptOrRejectInvite(invite.oid, "rejected") }}>
                                                <CloseIcon sx={{ color: 'red' }}/>
                                            </IconButton>
                                        </MenuItem>
                                    ))
                                ) : (
                                    <MenuItem>Sem notificações</MenuItem>
                                )}
                            </Menu>
                            <Tooltip title="Abrir definições">
                                <IconButton onClick={handleOpenUserMenu} sx={{ p: 0 }}>
                                    <Avatar alt="User Avatar" src={currentUserAvatar ? currentUserAvatar : "https://cdn-icons-png.flaticon.com/512/3135/3135715.png"} />
                                </IconButton>
                            </Tooltip>
                            <Menu
                                sx={{ mt: '45px' }}
                                id="menu-appbar"
                                anchorEl={anchorElUser}
                                anchorOrigin={{
                                    vertical: 'top',
                                    horizontal: 'right',
                                }}
                                keepMounted
                                transformOrigin={{
                                    vertical: 'top',
                                    horizontal: 'right',
                                }}
                                open={Boolean(anchorElUser)}
                                onClose={handleCloseUserMenu}
                            >
                                <MenuItem
                                    component={Link}
                                    to="/profile"
                                    onClick={handleCloseUserMenu}
                                >
                                    Perfil
                                </MenuItem>
                                <MenuItem
                                    component={Link}
                                    to="/logout"
                                    onClick={handleCloseUserMenu}
                                >
                                    Logout
                                </MenuItem>
                            </Menu>
                        </Box>
                    ) : (
                        <Box sx={{ flexGrow: -1, display: { xs: 'none', md: 'flex' } }}>
                            <Button
                                component={Link}
                                to="/login"
                                sx={{ my: 2, color: 'white', display: 'block' }}
                            >
                                Login
                            </Button>
                            <Button
                                component={Link}
                                to="/signup"
                                sx={{ my: 2, color: 'white', display: 'block' }}
                            >
                                Signup
                            </Button>
                        </Box>
                    )}
                </Toolbar>
            </Container>
        </AppBar>
    );
}

export default NavBar;
