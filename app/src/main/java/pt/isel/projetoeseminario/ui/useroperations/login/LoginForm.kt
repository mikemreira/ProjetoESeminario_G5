package pt.isel.projetoeseminario.ui.useroperations.login

import android.content.Context
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import pt.isel.projetoeseminario.ui.useroperations.PasswordField
import pt.isel.projetoeseminario.ui.useroperations.UsernameField
import pt.isel.projetoeseminario.viewModels.FetchState
import pt.isel.projetoeseminario.viewModels.UserViewModel

@Composable
fun LoginForm(registerNewUser: () -> Unit, onLogIn: () -> Unit, viewModel: UserViewModel) {
    var credentials by remember {
        mutableStateOf(Credentials())
    }

    val sharedPreferences = LocalContext.current.getSharedPreferences("users", Context.MODE_PRIVATE)
    val loginState by viewModel.loginState.collectAsState()

    Surface {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
        ) {
            UsernameField(credentials.email, { username -> credentials = credentials.copy(email = username)}, modifier = Modifier.fillMaxWidth())
            PasswordField(
                credentials.password,
                { password -> credentials = credentials.copy(password = password) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    try {
                        viewModel.login(credentials.email, credentials.password)
                    } catch(e: Exception) {
                    }
                },
                enabled = credentials.email.isNotEmpty() && credentials.password.isNotEmpty() && (loginState == FetchState.Idle || loginState is FetchState.Error),
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                when (loginState) {
                    is FetchState.Loading ->
                        CircularProgressIndicator()
                    is FetchState.Success -> {
                        onLogIn()
                    }
                    is FetchState.Error -> {
                        Toast.makeText(LocalContext.current, "Log in invalid", Toast.LENGTH_LONG)
                            .show()
                        Icon(imageVector = Icons.AutoMirrored.Filled.Login, contentDescription = null, modifier = Modifier.padding(horizontal = 4.dp))
                        Text(text = "Log In")
                    }
                    is FetchState.Idle -> {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Login,
                            contentDescription = null,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        Text("Log In")
                    }
                }
            }
            Text(text = "Register new user", textDecoration = TextDecoration.Underline, modifier = Modifier.clickable(onClick = registerNewUser).padding(4.dp), color = Color.Blue)
        }
    }
}

data class Credentials (
    val email: String = "",
    val password: String = ""
)

@Composable
fun LabeledCheckbox(
    label: String,
    onCheckedChanged: () -> Unit,
    isChecked: Boolean
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onCheckedChanged)
            .padding(4.dp)
    ) {
        Checkbox(checked = isChecked, onCheckedChange = null)
        Spacer(modifier = Modifier.size(6.dp))
        Text(text = label)
    }
}

@Preview
@Composable
private fun LoginFormPreview() {
    LoginForm({}, {}, viewModel())
}