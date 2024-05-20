package com.eug.swapz.ui.scenes.main

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.eug.swapz.R
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.models.Article
import com.eug.swapz.ui.scenes.filters.FilterViewModel
import com.eug.swapz.ui.scenes.login.LoginFactory
import com.eug.swapz.ui.theme.SwapzTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScene(viewModel: MainViewModel) {
    val articles by viewModel.articles.observeAsState(emptyList())
    var filteredArticles by remember { mutableStateOf(emptyList<Article>()) }
    var searchText by remember { mutableStateOf(String()) }
    val articlesByCategory = articles.groupBy { it.cat }
    viewModel.fetch()
    DisposableEffect(searchText) {
        val onSearchResults: (List<Article>) -> Unit = { searchResults ->
            filteredArticles = searchResults
        }
        performSearch(articles, searchText, onSearchResults)
        onDispose {
            // Cleanup logic if needed
        }
    }
    Scaffold(
        topBar = {
            CustomTopAppBar(viewModel, searchText) { newText ->
                searchText = newText // Update searchText
            }
        }
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.fillMaxSize()) {
            if (filteredArticles.isEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .padding(top = 55.dp)
                        .padding(horizontal = 50.dp),
                    horizontalArrangement = Arrangement.spacedBy(40.dp),
                ) {
                    articlesByCategory.keys.forEach { category ->
                        val iconResourceId = getIconResourceIdForCategory(category ?: "")
                        Box(
                            modifier = Modifier
                                .clickable {
                                    viewModel.navigateToFilter(
                                        category ?: ""
                                    )
                                } // Navigate on click
                                .wrapContentWidth()
                                .padding(horizontal = 4.dp), // Add some padding to each box
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = iconResourceId),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp)) // Add padding between image and text
                                Text(
                                    text = category
                                        ?: "Unknown", // Display "Unknown" if category is null
                                    style = TextStyle(fontSize = 12.sp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 130.dp),
                    horizontalAlignment = Alignment.Start
                ) {

                    articlesByCategory.forEach { (category, categoryArticles) ->
                        item {
                            Text(
                                text = category ?: "Other",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                ),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        item {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                content = {
                                    items(categoryArticles) { article ->
                                        Box(
                                            Modifier
                                                .width(150.dp)
                                                .padding(end = 8.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { viewModel.navigateToDetail(article) }
                                        ) {
                                            Column(
                                                Modifier.fillMaxWidth(),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Image(
                                                    painter = rememberAsyncImagePainter(
                                                        article.carrusel?.get(0)
                                                    ),
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .size(110.dp)
                                                        .clip(RoundedCornerShape(8.dp)),
                                                    contentScale = ContentScale.Crop,
                                                )
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    val max_title_length = 19
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
                                                        modifier = Modifier.padding(
                                                            start = 13.dp,
                                                            end = 10.dp
                                                        ),
                                                        maxLines = 1
                                                    )
                                                }
                                                Text(
                                                    text = article.value.toString() + " €",
                                                    style = TextStyle(fontSize = 10.sp),
                                                    modifier = Modifier.padding(start = 14.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            } else {
                val articlesByCategory = articles.groupBy { it.cat }

                // Display search results if there are any
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 128.dp)
                ) {
                    items(
                        items = filteredArticles,
                        itemContent = { article ->
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { viewModel.navigateToDetail(article) }
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(article.carrusel?.get(0)),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(200.dp)
                                        .padding(top = 70.dp, start = 12.dp, end = 12.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop,
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val max_title_length =
                                        19 // Define your maximum text length threshold
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
                                }
                                Text(
                                    text = article.value.toString() + " €",
                                    style = TextStyle(fontSize = 10.sp),
                                    modifier = Modifier.padding(start = 14.dp)
                                )
                            }
                        }
                    )
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
    }
}

private fun performSearch(
    articles: List<Article>,
    query: String,
    onSearchResults: (List<Article>) -> Unit
) {
    val searchResults = articles.filter { article ->
        article.title.contains(query, ignoreCase = true)
    }
    onSearchResults(searchResults)
}

fun getIconResourceIdForCategory(categoryName: String): Int {
    return when (categoryName) {
        "Hogar" -> R.drawable.hogar
        "Deportes" -> R.drawable.atletismo
        "Moda" -> R.drawable.camisa
        "Otros" -> R.drawable.otros
        else -> R.drawable.otros // Provide a default icon if category doesn't match
    }
}

@Composable
fun CustomTopAppBar(
    viewModel: MainViewModel,
    searchText: String,
    onSearchTextChanged: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Search Box with Spacer
        Box(
            modifier = Modifier
                .weight(30F)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFE0E0E0))
                .clickable { /* Handle click event */ }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.width(1.dp))
                BasicTextField(
                    value = searchText,
                    onValueChange = { onSearchTextChanged(it) },
                    textStyle = TextStyle(fontSize = 16.sp),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
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
