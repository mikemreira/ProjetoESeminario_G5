import React from "react";
import "./styles.css"
import { Link } from "react-router-dom";
import {useCurrentUser} from "./context/Authn.tsx";


function NavBar() {
    const currentUser = useCurrentUser()
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
}

export default NavBar;