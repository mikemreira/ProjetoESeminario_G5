package pt.isel.projetoeseminario.ui.useroperations.signup

import androidx.compose.runtime.Composable
import pt.isel.projetoeseminario.viewModels.UserViewModel

@Composable
fun SignUpScreen(viewModel: UserViewModel, onLogInClick: () -> Unit) {
    SignUpForm(viewModel, onLogInClick)
}