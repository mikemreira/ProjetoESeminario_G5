import "./styles.css";
import { Link } from "react-router-dom";
import { useCurrentUser } from "./context/Authn.tsx";
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



function NavBar() {
    const currentUser = useCurrentUser();
    const [anchorElUser, setAnchorElUser] = React.useState(null);

    const handleOpenUserMenu = (event) => {
        setAnchorElUser(event.currentTarget);
    };


    const handleCloseUserMenu = () => {
        setAnchorElUser(null);
    };

    return (
        <AppBar position="fixed" >
            <Container maxWidth="xl">
                <Toolbar disableGutters>
                    <Typography
                        variant="h6"
                        noWrap
                        sx={{
                            mr: 3,
                            display: { xs: 'none', md: 'flex' },
                            fontFamily: 'monospace',
                            fontWeight: 700,
                            letterSpacing: '.3rem',
                            color: 'inherit',
                            textDecoration: 'none',
                        }}
                    >
                        Registo de acessos
                    </Typography>
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
                        <><Box sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' } }}></Box></>
                    )}

                    { currentUser ? (
                        <Box sx={{ flexGrow: 0 }}>
                            <Tooltip title="Open settings">
                                <IconButton onClick={handleOpenUserMenu} sx={{ p: 0 }}>
                                    <Avatar alt="User Avatar" src="https://yt3.googleusercontent.com/VRA217M9QJ55sa5yE7TwNOxomQKvCwZ48I6WCiQ5DEEpD7jSYQVS5DuSVv8Kw6KGmoe-27d-mg=s900-c-k-c0x00ffffff-no-rj" />
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
                                    Profile
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
                        <Box sx={{ flexGrow: -1 , display: { xs: 'none', md: 'flex' } }}>
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
                    )
                    }
                </Toolbar>
            </Container>
        </AppBar>
    );
}

export default NavBar;

/*
return (
    <nav className="navbar">
        <ul className="nav-links left">
            <li><Link to="/">Home</Link></li>
        </ul>
        <ul className="nav-links right">
            {currentUser ? (
                <>
                    <li><Link to="/obras">Obras</Link></li>
                    <li><Link to="/logout">Log Out</Link></li>
                </>
            ) : (
                <>
                    <li><Link to="/login">Log In</Link></li>
                    <li><Link to="/signup">Signup</Link></li>
                </>
            )}
        </ul>
    </nav>
  );
 */