package pt.isel.projetoeseminario.ui.useroperations.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import pt.isel.projetoeseminario.ui.theme.ProjetoESeminarioTheme
import pt.isel.projetoeseminario.viewModels.UserViewModel

@Composable
fun LogInScreen(registerNewUser: () -> Unit, onLogIn: () -> Unit, viewModel: UserViewModel) {
    LoginForm(registerNewUser, onLogIn, viewModel)
}

@Preview
@Composable
private fun LogInScreenPreview() {
    ProjetoESeminarioTheme {
        LogInScreen({}, {}, viewModel())
    }
}