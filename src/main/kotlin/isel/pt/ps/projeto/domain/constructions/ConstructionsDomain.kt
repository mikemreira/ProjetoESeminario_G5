package isel.pt.ps.projeto.domain.constructions

import kotlinx.datetime.LocalDate

class ConstructionsDomain {
    fun checkValidConstruction(name: String, location: String, description: String, startDate: LocalDate, endDate: LocalDate?, status: String): Boolean{
        return !(name.isEmpty() || location.isEmpty() || description.isEmpty() || status.isEmpty() || startDate.toString().isEmpty())
    }
}