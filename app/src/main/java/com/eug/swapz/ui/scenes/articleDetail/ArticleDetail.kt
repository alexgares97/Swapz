package com.eug.swapz.ui.scenes.articleDetail

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.text.style.TextOverflow
import com.eug.swapz.ui.scenes.main.NavigationItem

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ArticleDetail(viewModel: ArticleDetailViewModel) {
    val article = viewModel.article ?: return
    var onClickImg by remember { mutableStateOf(false) }
    val images = article.carrusel ?: emptyList()
    val otherUserName by viewModel.otherUserName.observeAsState("")
    val otherUserPhoto by viewModel.otherUserPhoto.observeAsState("")
    val userId = article.user
    val hasStartedExchange by viewModel.hasStartedExchange.observeAsState(false)
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    val currentUserId = viewModel.getCurrentUserId()
    val chatId = viewModel.getChatId(userId, currentUserId ?: "")

    viewModel.checkIfExchangeStarted(userId, article.id?:"")
    viewModel.fetchUserName(userId)
    if (!onClickImg) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                                .clickable{viewModel.navigateToProfile(userId)}
                        ) {
                            BackIcon(onClick = { viewModel.home() })

                            Image(
                                painter = rememberAsyncImagePainter(otherUserPhoto),
                                contentDescription = "User Icon",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = otherUserName ?: "",
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
                    actions = {
                        if (userId != viewModel.getCurrentUserId()) {
                            if (hasStartedExchange) {
                                Button(
                                    onClick = {
                                        viewModel.navigateToChat(chatId,userId)
                                    },
                                    modifier = Modifier
                                        .padding(end = 16.dp)
                                        .align(Alignment.CenterVertically),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green),
                                    shape = RoundedCornerShape(12.dp)
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
                                        .padding(end = 16.dp)
                                        .align(Alignment.CenterVertically),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF2F96D8)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "Intercambiar",
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    },
                    backgroundColor = Color(0xFF86C5E4),
                    contentColor = Color.White,
                    modifier = Modifier.shadow(4.dp) // Agrega sombra suave
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(10.dp)

                ) {
                    if (images.isNotEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(images[0]),
                            contentDescription = null,
                            contentScale = ContentScale.Crop, // Ajusta la escala para llenar mejor el espacio
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onClickImg = true }
                                .shadow(4.dp, RoundedCornerShape(8.dp)) // Agrega sombra para mayor profundidad
                        )
                    }
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .shadow(8.dp, shape = RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = article.title,
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333)
                            ),
                            modifier = Modifier.padding(bottom = 8.dp, top = 10.dp)
                        )
                        Text(
                            text = article.desc,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF666666),
                                textAlign = TextAlign.Justify
                            ),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Estado: ${article.status}",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF666666)
                                ),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Category,
                                contentDescription = null,
                                tint = Color(0xFF03A9F4),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Categoría: ${article.cat}",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF666666)
                                ),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Valor: ${article.value} €",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF666666)
                                ),
                                modifier = Modifier.padding(start = 8.dp)
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
            if (showConfirmationDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmationDialog = false },
                    title = { Text(text = "Confirmar solicitud") },
                    text = { Text(text = "¿Estás seguro que deseas solicitar el intercambio de ${article.title}") },
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
    } else {
        Column(modifier = Modifier.background(Color.Black)) {
            IconButton(
                onClick = { onClickImg = false },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            HorizontalPager(
                state = rememberPagerState(pageCount = { images.size }),
                modifier = Modifier.weight(1f)
            ) { page ->
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = rememberAsyncImagePainter(images[page]),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            PagerIndicator(
                pagerState = rememberPagerState(pageCount = { images.size }),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    indicatorSize: Int = 12,
    indicatorSpacing: Int = 8,
    activeColor: Color = MaterialTheme.colors.primary,
    inactiveColor: Color = Color.Gray
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pagerState.pageCount) { index ->
            Spacer(modifier = Modifier.width(indicatorSpacing.dp))
            Indicator(
                color = if (index == pagerState.currentPage) activeColor else inactiveColor,
                size = indicatorSize
            )
        }
    }
}

@Composable
private fun Indicator(
    color: Color,
    size: Int
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .size(size.dp)
            .background(color)
    )
}
@Composable
fun BackIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    androidx.compose.material.IconButton(onClick = onClick, modifier = modifier) {
        androidx.compose.material.Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back"
        )
    }
}