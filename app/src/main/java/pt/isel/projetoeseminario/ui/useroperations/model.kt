package pt.isel.projetoeseminario.ui.useroperations

import android.util.Patterns
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDownCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UsernameField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val leadingIcon = @Composable {
        Icon(Icons.Default.Person, contentDescription = "", tint = MaterialTheme.colorScheme.primary)
    }

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        leadingIcon = leadingIcon,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions (
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        ),
        placeholder = { Text(text = "Enter your username") },
        label = { Text("Username") },
        singleLine = true
    )
}

fun validateEmailField(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()

@Composable
fun EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val leadingIcon = @Composable {
        Icon(Icons.Default.Email, contentDescription = "", tint = MaterialTheme.colorScheme.primary)
    }

    var isValid by remember { mutableStateOf(true) }

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        leadingIcon = leadingIcon,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions (
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
                isValid = validateEmailField(value)
            }
        ),
        placeholder = { Text(text = "Enter your e-mail") },
        label = { Text("E-mail") },
        singleLine = true,
        isError = !isValid
    )
}

@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isLogin: Boolean = true
) {
    var isPasswordVisible by remember {
        mutableStateOf(false)
    }

    val leadingIcon = @Composable {
        Icon(Icons.Default.Key, contentDescription = "", tint = MaterialTheme.colorScheme.primary)
    }
    val trailingIcon = @Composable {
        IconButton(onClick = { isPasswordVisible = !isPasswordVisible}) {
            Icon(
                if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                contentDescription = ""
            )
        }
    }
    Column(modifier = modifier) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            /*keyboardActions = KeyboardActions(
                onDone = {
                    submit()
                }
            ),*/
            placeholder = { Text(text = "Enter your password") },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            supportingText = @Composable {
                if(!isLogin)
                    PasswordRequirementsUI(passwordRequirementsMet = PasswordRequirements(
                        hasMinLength = value.length >= 6,
                        hasUpperCase = value.any { it.isUpperCase() },
                        hasLowerCase = value.any { it.isLowerCase() },
                        hasDigit = value.any { it.isDigit() }
                    ))
            }
        )
    }

}

@Composable
fun PasswordRequirementsUI(passwordRequirementsMet: PasswordRequirements) {
    Column {
        PasswordRequirementItem("At least 6 characters", passwordRequirementsMet.hasMinLength)
        PasswordRequirementItem("At least one uppercase letter", passwordRequirementsMet.hasUpperCase)
        PasswordRequirementItem("At least one lowercase letter", passwordRequirementsMet.hasLowerCase)
        PasswordRequirementItem("At least one digit", passwordRequirementsMet.hasDigit)
    }
}

@Composable
fun PasswordRequirementItem(requirement: String, isMet: Boolean) {
    Row {
        Icon(
            imageVector = if (isMet) Icons.Filled.CheckCircle else Icons.Filled.ArrowDropDownCircle,
            contentDescription = null,
            tint = if (isMet) Color(0xFF7ABF62) else Color(0xFFEC5454),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = requirement, color = if (isMet) Color(0xFF7ABF62) else Color(0xFFEC5454), fontSize = 12.sp)
    }
}

data class PasswordRequirements(
    val hasMinLength: Boolean = false,
    val hasUpperCase: Boolean = false,
    val hasLowerCase: Boolean = false,
    val hasDigit: Boolean = false
) {
    fun allMet() = hasMinLength && hasUpperCase && hasLowerCase && hasDigit
}