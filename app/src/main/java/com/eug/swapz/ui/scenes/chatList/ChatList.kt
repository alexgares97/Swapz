import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

    DisposableEffect(Unit) {
        viewModel.fetchChatList()
        onDispose {
            // Clean up the listener when the composable is removed from the composition
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
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
                        //viewModel.navigateToChat(chatId = chat.id)
                    }
                }
            }
        }

        // Sign out button
        Button(
            onClick = { viewModel.signOut() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Sign Out")
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
            Text(text = chat.name)
            Text(text = chat.text)//align
            Image(
                painter = rememberAsyncImagePainter(chat.photoUrl),
                contentDescription = "User Icon",
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(25.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}
