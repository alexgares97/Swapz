import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.eug.swapz.models.Chat
import com.eug.swapz.ui.scenes.chatList.ChatListViewModel

@Composable
fun ChatList(viewModel: ChatListViewModel) {
    // Observe the list of chat messages from the ViewModel
    val chatListState = viewModel.chatListState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Toolbar
        TopAppBar(
            title = { Text(text = "Chats") },
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = Color.White
        )

        // Chat list
        LazyColumn {
            items(chatListState.value.size) { index ->
                val chat = chatListState.value[index]
                ChatListItem(chat = chat, onClick = {
                    // Navigate to chat detail screen when a chat item is clicked
                    viewModel.navigateToChat(chatId = chat.id)
                })
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
            Text(text = chat.userName)
            Text(text = chat.lastMessage)
        }
    }
}
