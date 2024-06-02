package pt.isel.projetoeseminario.ui.useroperations.signup

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pt.isel.projetoeseminario.ui.useroperations.EmailField
import pt.isel.projetoeseminario.ui.useroperations.PasswordField
import pt.isel.projetoeseminario.ui.useroperations.UsernameField
import pt.isel.projetoeseminario.viewModels.FetchState
import pt.isel.projetoeseminario.viewModels.UserViewModel

@Composable
fun SignUpForm(viewModel: UserViewModel, onLogInClick: () -> Unit) {
    var credentials by remember {
        mutableStateOf(Credentials())
    }

    val localContext = LocalContext.current
    val signupState by viewModel.signupState.collectAsState()

    Surface {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
        ) {
            UsernameField(credentials.username, { username -> credentials = credentials.copy(username = username)}, modifier = Modifier.fillMaxWidth())
            EmailField(value = credentials.email, onValueChange = { email -> credentials = credentials.copy(email = email) }, modifier = Modifier.fillMaxWidth())
            PasswordField(
                credentials.password,
                { password -> credentials = credentials.copy(password = password) },
                modifier = Modifier.fillMaxWidth(),
                isLogin = false
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    viewModel.signup(credentials.username, credentials.email, credentials.password)
                },
                enabled = credentials.username.isNotEmpty() && credentials.password.isNotEmpty(),
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                when (signupState) {
                    is FetchState.Loading ->
                        CircularProgressIndicator()
                    is FetchState.Error -> {
                        Toast.makeText(localContext, "Something went wrong", Toast.LENGTH_LONG).show()
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = null,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        Text("Sign Up")
                    }
                    is FetchState.Idle -> {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = null,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        Text("Sign Up")
                    }
                    is FetchState.Success ->
                        onLogInClick()
                }
            }
            Row {
                Text(text = "Already have an account?")
                Text(text = "Log in", textDecoration = TextDecoration.Underline, color = Color.Blue, modifier = Modifier.clickable { onLogInClick() })
            }
        }
    }
}

data class Credentials (
    val username: String = "",
    val email: String = "",
    val password: String = "",
    var remember: Boolean = false
)

@Preview
@Composable
private fun SignUpFormPreview() {
    SignUpForm(viewModel(), {})
}