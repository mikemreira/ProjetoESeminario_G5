package isel.pt.ps.projeto.models.constructions

data class ConstructionOutputModelAndHrefs(
    val constructionAndRoleOutputModel: ConstructionAndRoleOutputModel,
    val usersRoute: String,
    val registersRoute: String,
    val editRoute: String
)
