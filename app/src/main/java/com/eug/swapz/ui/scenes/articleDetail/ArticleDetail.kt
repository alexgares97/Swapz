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
import androidx.compose.material.ButtonColors
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.eug.swapz.R
import com.google.android.gms.location.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ArticleDetail(viewModel: ArticleDetailViewModel) {
    val article = viewModel.article ?: return
    var onClickImg by remember { mutableStateOf(false) }
    val images = article.carrusel ?: emptyList()
    val pagerState = rememberPagerState(pageCount = { images.size })
    var userId = article.user
    var showConfirmationDialog by remember { mutableStateOf(false) } // State for showing the dialog
    val otherUserName by viewModel.otherUserName.observeAsState("")
    val otherUserPhoto by viewModel.otherUserPhoto.observeAsState("")

    // Conditionally show the top bar only if image is not clicked
    viewModel.fetchUserName(userId)
    if (!onClickImg) {
        Scaffold(
            topBar = {
                TopAppBar(
                    actions = {
                        Image(
                            painter = rememberAsyncImagePainter(otherUserPhoto),
                            contentDescription = "User Icon",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(25.dp)),
                            contentScale = ContentScale.Crop
                        )
                        if(userId != viewModel.getCurrentUserId())
                            Button(
                                onClick = { showConfirmationDialog = true },
                                modifier = Modifier.padding(horizontal = 55.dp)
                                    .align(Alignment.CenterVertically),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green),


                                ) {
                                Text(text = "Intercambiar", color = Color.White)
                            }
                    },
                    title = { Text(text = otherUserName?:"") },

                )
            }


        ) { innerPadding ->
            // Column content
            // Use innerPadding for content padding
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(10.dp)
                ) {
                    // Content of the composable
                    Image(
                        painter = rememberAsyncImagePainter(article.carrusel?.get(0)),
                        contentDescription = null,
                        modifier = Modifier
                            .clickable { onClickImg = true }
                            .fillMaxWidth()
                    )
                    Text(
                        text = article.title,
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp, top = 10.dp)
                    )
                    Text(
                        text = "Descripción: ${article.desc}",
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Justify),
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    )
                    Text(
                        text = "Estado: ${article.status}",
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Text(
                        text = "Categoria: ${article.cat}",
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Text(
                        text = "Valor: ${article.value} €",
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

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
                    IconButton(onClick = { viewModel.home() }) {
                        Icon(
                            Icons.Filled.Home,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp) // Tamaño del ícono aumentado
                        )
                    }
                    IconButton(onClick = { viewModel.navigateToChatList() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Chat,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
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
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = { viewModel.signOut() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            if (showConfirmationDialog) {
                AlertDialog(
                    onDismissRequest = {
                    },
                    title = {
                        Text(text = "Confirmar intercambio")
                    },
                    text = {
                        Text(text = "¿Estás seguro que deseas intercambiar este artículo?")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.startExchange(userId,article)
                            }
                        ) {
                            Text("Confirmar")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                            }
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    } else {
        // Show the image gallery when image is clicked
        Column(modifier = Modifier.background(Color.Black)) {
            // Content of the image gallery
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

            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
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
                pagerState = pagerState,
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