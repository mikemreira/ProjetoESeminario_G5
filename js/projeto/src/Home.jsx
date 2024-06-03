import * as React from 'react'
import { Link } from 'react-router-dom'
import NavBar from './NavBar'

export default function Home() {
    return (
        <>
            <NavBar />
            <div className="form-home">
                <h1>Bem vindo ao Registo de Acessos</h1>
                <p>
                    Aqui poderás registar os teus acessos e visualizar o histórico dos mesmos.
                </p>
            </div>
        </>
    )
}