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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.ui.scenes.login.LoginFactory
import com.eug.swapz.ui.theme.SwapzTheme
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.eug.swapz.models.Article
import com.eug.swapz.R


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
                backgroundColor = Color(0xFF86C5E4)
            )
        }

    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()
            .padding(innerPadding)
        )
        {
            if (articles.isEmpty()) {
                EmptyInventoryView(viewModel)
            }
                LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(8.dp))

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
                                painter = rememberAsyncImagePainter(article.carrusel[0]),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(120.dp)
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop,
                            )


                            val max_title_length = 25
                            val max_desc_length = 55

                            Text(
                                text = if (article.title.length <= max_title_length) {
                                    article.title
                                } else {
                                    "${article.title.take(max_title_length)}..."
                                },
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(start = 13.dp, end = 10.dp),
                                maxLines = 1
                            )
                            Text(
                                text = if (article.desc.length <= max_desc_length) article.desc
                                else "${article.desc.take(max_desc_length)}...",
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
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(
                                        0xFFFC655A
                                    )
                                    ),
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
                    title = { Text(text = "Confirma la eliminación") },
                    text = { Text(text = "¿Estas seguro que deseas eliminar este artículo?") },
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
@Composable
fun EmptyInventoryView(viewModel: InventoryViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(R.drawable.empty_inventory_box),
                contentDescription = "No Messages",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "¡Tu inventario está vacío!",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Comienza a añadir artículos para intercambiar.",
                style = TextStyle(fontSize = 16.sp),
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            Button(
                onClick = { viewModel.navigateToAddArticle() },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF2F96D8))
            ) {
                Text(text = "Agregar artículo", color = Color.White)
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
