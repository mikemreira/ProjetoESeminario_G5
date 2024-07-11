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
                    Esta aplicação foi desenvolvida no âmbito da disciplina Projeto e Seminário do curso de Licenciatura em Engenharia Informática e  de Computadores do Isel no ano letivo 2023/2024.
                </p>
                <p>
                    Foi desenvolvida por:
                        <li>Miguel Moreira, 46092</li>
                        <li>João Rodrigues, 46489</li>
                        <li>João Viegas, 47208</li>
                </p>
                <p>
                    Esta aplicação permite a gestão de acessos a obras de construção civil.
                </p>
                <p>
                    Podes registar-te, autenticar-te, adicionar obras, convidar utilizadores para obras e registar acessos.
                </p>
            </div>
        </>
    )
}