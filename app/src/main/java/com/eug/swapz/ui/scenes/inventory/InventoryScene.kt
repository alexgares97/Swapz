package com.eug.swapz.ui.scenes.inventory

import android.annotation.SuppressLint
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.ui.scenes.login.LoginFactory
import com.eug.swapz.ui.theme.SwapzTheme
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.eug.swapz.models.Article

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScene(viewModel: InventoryViewModel) {
    val articles by viewModel.articles.observeAsState(emptyList())
    val username by viewModel.username.collectAsState()
    val category by viewModel.category.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    var articleToDelete by remember { mutableStateOf<Article?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetch()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = username ?: "Inventory", color = Color.White) }, // Use the retrieved username as the title }, // Accessing viewModel.username correctly
                backgroundColor = Color(0xFF2F96D8)
            )
        }

    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()
            .padding(innerPadding)
        )
        {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 128.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                items(
                    items = articles,
                    itemContent = { article ->
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .shadow(4.dp, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.navigateToDetail(article) }
                                .padding(8.dp) // Added padding for the card
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(article.carrusel?.get(0)),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(120.dp)
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop,
                            )


                            val max_title_length = 19 // Define your maximum text length threshold
                            Text(
                                text = if (article.title.length <= max_title_length) {
                                    article.title
                                } else {
                                    "${article.title.take(max_title_length)}..."
                                },
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(start = 13.dp, end = 10.dp),
                                maxLines = 1
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(
                                    onClick = { viewModel.navigateToEditArticle(article) },
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp, vertical = 4.dp)
                                        .weight(1f),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF2F96D8)),
                                    contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp) // Adjust padding for smaller button
                                ) {
                                    Text(text = "Editar", color = Color.White, fontSize = 10.sp) // Smaller text
                                }
                                Button(
                                    onClick = {
                                        articleToDelete = article
                                        showDialog = true
                                    },
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp, vertical = 4.dp)
                                        .weight(1f),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF2F96D8)),
                                    contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp) // Adjust padding for smaller button
                                ) {
                                    Text(text = "Eliminar", color = Color.White, fontSize = 10.sp) // Smaller text
                                }
                            }
                        }
                    }
                )
            }
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showDialog = false
                        articleToDelete = null
                    },
                    title = { Text(text = "Confirm Deletion") },
                    text = { Text(text = "Are you sure you want to delete this article?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                // Delete the article
                                articleToDelete?.let { article ->
                                    viewModel.deleteArticle(article.id)
                                }
                                showDialog = false
                                articleToDelete = null
                            }
                        ) {
                            Text(text = "Confirm")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                showDialog = false
                                articleToDelete = null
                            }
                        ) {
                            Text(text = "Cancel")
                        }
                    }
                )
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
                IconButton(onClick = { viewModel.navigateToMain() }) {
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
    }
}

@Preview(showBackground = true)
@Composable
fun MainScenePreview() {
    SwapzTheme {
        LoginFactory(
            navController = rememberNavController(),
            sessionDataSource = SessionDataSource()
        )
    }
}
