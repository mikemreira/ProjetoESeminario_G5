package pt.isel.projetoeseminario.ui.useroperations.profile

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.isel.projetoeseminario.R
import pt.isel.projetoeseminario.model.Obra
import pt.isel.projetoeseminario.model.ObrasOutputModel
import pt.isel.projetoeseminario.viewModels.FetchState
import pt.isel.projetoeseminario.viewModels.UserViewModel

@Composable
fun PerfilScreen(viewModel: UserViewModel, token: String) {
    val fetchProfileState = viewModel.fetchProfileState.collectAsState()
    val fetchResult = viewModel.fetchProfileResult.value
    val fetchObraResult = viewModel.fetchObraResult.value

    LaunchedEffect(key1 = token) {
        viewModel.getUserDetails(token)
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (fetchProfileState.value == FetchState.Loading) CircularProgressIndicator()
            else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ProfileImage(imageResource = R.drawable.builder)
                    ProfileInfo(title = "Username", value = fetchResult?.nome ?: "Default")
                    ProfileInfo(title = "E-mail", value = fetchResult?.email ?: "Default")
                    Log.d("OBRA", fetchObraResult?.obras.toString())
                    if (fetchObraResult != null)
                        HorizontalCardSlider(cardItems = fetchObraResult)
                }
            }
        }
    }
}

@Composable
fun ProfileInfo(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )
    }
}

@Composable
fun ProfileImage(imageResource: Int) {
    Image(
        painter = painterResource(id = imageResource),
        contentDescription = null,
        modifier = Modifier
            .size(120.dp)
            .background(Color.LightGray, shape = CircleShape)
            .clip(CircleShape)
            .border(2.dp, Color.DarkGray, CircleShape)
            .padding(4.dp),
        contentScale = ContentScale.Crop
    )
}

data class CardItem(val title: String, val description: String)

@Composable
fun HorizontalCardSlider(cardItems: ObrasOutputModel) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(cardItems.obras) { cardItem ->
            CardItemView(cardItem)
        }
    }
}

@Composable
fun CardItemView(cardItem: Obra) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(250.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = cardItem.nome,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = cardItem.descricao,
                fontSize = 16.sp
            )
        }
    }
}
