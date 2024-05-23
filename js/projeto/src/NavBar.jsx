import React from "react";
import "./styles.css"
import { Link } from "react-router-dom";

function NavBar() {
  return (
    <nav className="navbar">
        <ul className="nav-links left">
            <li><Link to="/">Home</Link></li>
        </ul>
        <ul className="nav-links right">
            <li><Link to="/login">Log In</Link></li>
            <li><Link to="/signup">Signup</Link></li>
        </ul>
    </nav>
  );
}

export default NavBar;