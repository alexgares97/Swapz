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
                                        showCancelDialog = true
                                    },
                                    modifier = Modifier
                                        .padding(end = 16.dp)
                                        .align(Alignment.CenterVertically),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "Finalizar",
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
                    Text(
                        text = article.title,
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold), // Incrementa el tamaño de la fuente
                        modifier = Modifier.padding(bottom = 8.dp, top = 10.dp)
                    )
                    Text(
                        text = article.desc,
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Justify),
                        modifier = Modifier
                            .padding(vertical = 8.dp)
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
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Row(
                    modifier = Modifier
                        .width(390.dp)
                        .height(40.dp) // Ajusta la altura para mayor presencia visual
                        .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF2F96D8), Color(0xFF1A73E8))
                            )
                        )
                        .shadow(8.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) // Sombra para mayor realismo
                        .padding(horizontal = 24.dp)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.home() }) {
                        Icon(
                            Icons.Filled.Home,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp) // Ajusta el tamaño del ícono
                        )
                    }
                    IconButton(onClick = { viewModel.navigateToChatList() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Chat,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = { viewModel.navigateToAddArticle() }) {
                        Icon(
                            Icons.Filled.AddBox,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    IconButton(onClick = { viewModel.navigateToInventory() }) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(24.dp),
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { viewModel.signOut() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
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
            if (showCancelDialog) {
                AlertDialog(
                    onDismissRequest = { showCancelDialog = false },
                    title = { Text(text = "Confirmar cancelación") },
                    text = {
                            Text(text = "¿Estás seguro que deseas cancelar el intercambio de ${article.title}? Se eliminará la conversación")
                           },
                    confirmButton = {
                        Button(onClick = {
                            viewModel.cancelExchange(userId, article.id ?: "")
                            showCancelDialog = false
                        }) {
                            Text("Confirmar")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showCancelDialog = false }) {
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
