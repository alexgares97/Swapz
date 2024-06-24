package com.eug.swapz.ui.scenes.profile

import androidx.compose.material.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.ui.scenes.login.LoginFactory
import com.eug.swapz.ui.theme.SwapzTheme
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.eug.swapz.models.Article
import com.eug.swapz.ui.scenes.main.NavigationItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScene(viewModel: ProfileViewModel) {
    val articles by viewModel.articles.observeAsState(emptyList())
    val name by viewModel.otherUserName.observeAsState("")
    val userId = viewModel.node
    val photo by viewModel.otherUserPhoto.observeAsState("")
    val hasStartedExchangeMap by viewModel.hasStartedExchangeMap.collectAsState()
    var showConfirmationDialog by remember { mutableStateOf(false) }
    val currentUserId = viewModel.getCurrentUserId()

    LaunchedEffect(Unit) {
        viewModel.fetch()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(photo),
                            contentDescription = "User Icon",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = name ?: "",
                            style = MaterialTheme.typography.h6.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                backgroundColor = Color(0xFF86C5E4)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                items(articles) { article ->
                    viewModel.checkIfExchangeStarted(article.id ?: "")
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .shadow(4.dp, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.navigateToDetail(article) }
                            .padding(8.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(article.carrusel[0]),
                            contentDescription = null,
                            modifier = Modifier
                                .height(120.dp)
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        val maxTitleLength = 19
                        val maxDescLength = 55
                        Text(
                            text = if (article.title.length <= maxTitleLength) article.title else "${article.title.take(maxTitleLength)}...",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(start = 13.dp, end = 10.dp),
                            maxLines = 1
                        )
                        Text(
                            text = if (article.desc.length <= maxDescLength) article.desc else "${article.desc.take(maxDescLength)}...",
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Justify),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Text(
                            text = "Estado: ${article.status}",
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Text(
                            text = "Categoría: ${article.cat}",
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Text(
                            text = "Valor: ${article.value} €",
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val hasStartedExchange = hasStartedExchangeMap[article.id] ?: false
                            Log.d("ProfileScene", "Article ID: ${article.id}, hasStartedExchange: $hasStartedExchange")

                            if (hasStartedExchange) {
                                Button(
                                    onClick = {
                                        val chatId = viewModel.getChatId(userId, currentUserId ?: "")
                                        viewModel.navigateToChat(chatId, userId)
                                    },
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp, vertical = 4.dp)
                                        .weight(1f),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green),
                                    contentPadding = PaddingValues(
                                        vertical = 4.dp,
                                        horizontal = 8.dp
                                    )
                                ) {
                                    Text(
                                        text = "Chat",
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                }
                            } else {
                                Button(
                                    onClick = { showConfirmationDialog = true },
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp, vertical = 4.dp)
                                        .weight(1f),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF2F96D8)),
                                    contentPadding = PaddingValues(
                                        vertical = 4.dp,
                                        horizontal = 8.dp
                                    )
                                ) {
                                    Text(
                                        text = "Intercambiar",
                                        color = Color.White,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                    if (showConfirmationDialog) {
                        AlertDialog(
                            onDismissRequest = { showConfirmationDialog = false },
                            title = { Text(text = "Confirmar solicitud") },
                            text = { Text(text = "¿Estás seguro que deseas solicitar el intercambio de ${article.title}?") },
                            confirmButton = {
                                Button(onClick = {
                                    viewModel.startExchange(userId, article)
                                    showConfirmationDialog = false
                                }) {
                                    Text("Confirmar")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showConfirmationDialog = false }) {
                                    Text("Cancelar")
                                }
                            }
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF2F96D8).copy(alpha = 0.9f), Color(0xFF1A73E8).copy(alpha = 0.9f))
                        )
                    )
                    .shadow(12.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationItem(
                    icon = Icons.Filled.Home,
                    label = "Inicio",
                    onClick = { viewModel.navigateToMain() },
                    iconSize = 24.dp
                )
                NavigationItem(
                    icon = Icons.AutoMirrored.Filled.Chat,
                    label = "Chats",
                    onClick = { viewModel.navigateToChatList() },
                    iconSize = 24.dp
                )
                NavigationItem(
                    icon = Icons.Filled.AddCircle,
                    label = "Añadir",
                    onClick = { viewModel.navigateToAddArticle() },
                    iconSize = 25.dp
                )
                NavigationItem(
                    icon = Icons.Filled.Inventory,
                    label = "Inventario",
                    onClick = { viewModel.navigateToInventory() },
                    iconSize = 24.dp
                )
                NavigationItem(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    label = "Salir",
                    onClick = { viewModel.signOut() },
                    iconSize = 24.dp
                )
            }
        }
    }
}
