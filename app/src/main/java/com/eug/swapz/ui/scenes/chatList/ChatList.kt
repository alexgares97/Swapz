import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.eug.swapz.models.Chat
import com.eug.swapz.ui.scenes.chatList.ChatListViewModel

@Composable
fun ChatList(viewModel: ChatListViewModel) {
    val chatListState = viewModel.chatList.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        viewModel.fetchChatList()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Toolbar
        TopAppBar(
            title = { Text(text = "Chats") },
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = Color.White
        )

        // Loading indicator
        if (chatListState.value.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        } else {
            // Chat list
            LazyColumn {
                items(chatListState.value) { chat ->
                    ChatListItem(chat = chat) {
                        // Navigate to chat detail screen when a chat item is clicked
                        viewModel.navigateToChat(chat.id, chat.otherUserId)
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .width(390.dp)
                .height(30.dp) // Ajustar la altura del footer
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)) // Bordes redondeados en la parte superior
                .widthIn(min = 280.dp, max = 360.dp) // Ajustar el ancho del Row

                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF2F96D8).copy(alpha = 0.9f), Color(0xFF1A73E8).copy(alpha = 0.9f))
                    )
                )
                .shadow(12.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) // Añadir sombra para dar efecto de elevación
                .padding(horizontal = 24.dp) // Padding horizontal
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly, // Espaciar elementos equitativamente
            verticalAlignment = Alignment.CenterVertically
        ){
            androidx.compose.material3.IconButton(onClick = { viewModel.home() }) {
                androidx.compose.material3.Icon(
                    Icons.Filled.Home,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp) // Tamaño del ícono aumentado
                )
            }
            androidx.compose.material3.IconButton(onClick = { viewModel.navigateToChatList() }) {
                androidx.compose.material3.Icon(
                    Icons.AutoMirrored.Filled.Chat,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            androidx.compose.material3.IconButton(onClick = { viewModel.navigateToAddArticle() }) {
                androidx.compose.material3.Icon(
                    Icons.Filled.AddBox,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            androidx.compose.material3.IconButton(onClick = { viewModel.navigateToInventory() }) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
            }

            androidx.compose.material3.IconButton(onClick = { viewModel.signOut() }) {
                androidx.compose.material3.Icon(
                    Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}


@Composable
fun ChatListItem(chat: Chat, onClick: () -> Unit) {
    // Display each chat item as a clickable card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Image(
                painter = rememberAsyncImagePainter(chat.photoUrl),
                contentDescription = "User Icon",
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(25.dp)),
                contentScale = ContentScale.Crop
            )
            Text(text = chat.name)

        }
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = chat.text)//align

        }

    }
}
