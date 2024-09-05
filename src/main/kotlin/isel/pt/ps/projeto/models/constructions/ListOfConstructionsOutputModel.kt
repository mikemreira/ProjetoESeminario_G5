package isel.pt.ps.projeto.models.constructions

data class ListOfConstructionsOutputModel (
    val obras: List<ConstructionOutputModel>,
    val size: Int
)