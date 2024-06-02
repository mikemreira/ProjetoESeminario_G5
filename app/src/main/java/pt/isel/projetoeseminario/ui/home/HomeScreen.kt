package pt.isel.projetoeseminario.ui.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import pt.isel.projetoeseminario.model.Obra
import pt.isel.projetoeseminario.model.ObrasOutputModel
import pt.isel.projetoeseminario.viewModels.FetchState
import pt.isel.projetoeseminario.viewModels.RegistoViewModel
import pt.isel.projetoeseminario.viewModels.UserViewModel
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(userViewModel: UserViewModel, registerViewModel: RegistoViewModel, token: String, onHomeScreen: () -> Unit) {
    val fetchObrasState = userViewModel.fetchProfileState.collectAsState()
    val fetchObrasResult = userViewModel.fetchObraResult.value

    LaunchedEffect(Unit) {
        onHomeScreen()
    }

    var expanded by remember { mutableStateOf(false) }
    var textfieldSize by remember { mutableStateOf(Size.Zero)}

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (fetchObrasState.value == FetchState.Loading) CircularProgressIndicator()
        else {
            Box {
                if (fetchObrasResult != null) {
                    var selected by remember { mutableStateOf(fetchObrasResult.obras[0]) }
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(vertical = 10.dp)
                        ) {
                            Button(
                                onClick = {
                                    registerViewModel.addUserRegisterEntrada("Bearer $token", LocalDateTime.now(), selected.oid)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF7FCB78
                                    )
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "Entrada")
                            }
                            Spacer(modifier = Modifier.width(8.dp)) // Adjust the space between buttons
                            Button(
                                onClick = {
                                    registerViewModel.addUserRegisterSaida("Bearer $token", LocalDateTime.now(), selected.oid)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFFF5DF68
                                    )
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "Saída")
                            }
                        }
                        OutlinedTextField(
                            value = selected.nome,
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .onGloballyPositioned { coordinates ->
                                    //This value is used to assign to the DropDown the same width
                                    textfieldSize = coordinates.size.toSize()
                                },
                            label = { Text("Select construction") },
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, "contentDescription",
                                    Modifier.clickable { expanded = !expanded })
                            }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                        ) {
                            fetchObrasResult.obras.forEach { label ->
                                DropdownMenuItem(text = { Text(text = label.nome) }, onClick = {
                                    selected = label
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}