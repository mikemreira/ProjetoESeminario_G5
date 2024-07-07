package isel.pt.ps.projeto.models.constructions

data class ConstructionOutputModelAndHrefsEdit (
    val constructionAndRoleOutputModel: ConstructionOutputModel,
    val usersRoute: String,
    val registersRoute: String,
    val editRoute: String
)
