import { useEffect, useState } from "react"
import { useCookies } from "react-cookie"
import * as React from "react"

interface DateObject {
    year: number
    dayOfMonth: number
    month: string
    dayOfWeek: string
    dayOfYear: number
    monthNumber: number
    value$kotlinx_datetime: string
}

interface Obra {
    oid: number
    nome: string
    localizacao: string
    descricao: string
    data_inicio: DateObject | null
    data_fim: DateObject | null
    status: string
}

interface ObrasOutputModel {
    obras: Obra[]
}

// Utility function to convert date object to string
const formatDate = (dateObj: DateObject | null): string => {
    return dateObj ? dateObj.value$kotlinx_datetime : "N/A"
}

export default function Obras() {
    const [cookies] = useCookies(["token"])
    const [obras, setObras] = useState<ObrasOutputModel>({ obras: [] })

    useEffect(() => {
        fetch("/api/obras", {
            method: "GET",
            headers: {
                "Content-type": "application/json",
                "Authorization": `Bearer ${cookies.token}`,
            },
        })
            .then((res) => {
                if (res.ok) {
                    return res.json()
                } else {
                    return null
                }
            })
            .then((body) => {
                if (body) {
                    setObras(body)
                    console.log(body)
                }
            })
            .catch((error) => {
                console.error("Error fetching obras:", error)
            })
    }, [cookies.token])


    return (
        <div>
            <h1>Obras</h1>
            <table>
                <thead>
                <tr>
                    <th>Nome</th>
                    <th>Localização</th>
                    <th>Descrição</th>
                    <th>Data de Início</th>
                    <th>Data de Fim</th>
                    <th>Status</th>
                </tr>
                </thead>
                <tbody>
                {obras.obras.map((obra) => (
                    <tr key={obra.oid}>
                        <td>{obra.nome}</td>
                        <td>{obra.localizacao}</td>
                        <td>{obra.descricao}</td>
                        <td>{formatDate(obra.data_inicio)}</td>
                        <td>{formatDate(obra.data_fim)}</td>
                        <td>{obra.status}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    )
}
