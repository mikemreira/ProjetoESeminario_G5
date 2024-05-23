import * as React from 'react'
import { Link } from 'react-router-dom'

export default function Home() {
    
    return (
        <>
            <div>
                <Link to="/login">Login</Link>
            </div>
            
            <div>
                <Link to="/signup">Sign Up</Link>
            </div>

            <div>
                <Link to="/obras">Obras</Link>
            </div>
        </>
    )
}