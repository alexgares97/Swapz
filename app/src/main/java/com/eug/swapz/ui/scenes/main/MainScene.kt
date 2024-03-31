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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eug.swapz.R
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.ui.scenes.login.LoginFactory
import com.eug.swapz.ui.theme.SwapzTheme
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.eug.swapz.models.Article


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScene(viewModel: MainViewModel) {
    val articles by viewModel.articles.observeAsState(emptyList())
    var filteredArticles by remember { mutableStateOf(emptyList<Article>()) }
    var searchText by remember { mutableStateOf("") }
    // Loads
    LaunchedEffect(viewModel) {
        viewModel.fetch()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                actions = {
                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text("Buscar en Swapz") },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .height(56.dp) // Increase the height to make it more visible
                            .fillMaxWidth(), // Take full width of the screen
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                // Perform search when the user hits the Done button on the keyboard
                                performSearch(articles, searchText) { searchResults ->
                                    filteredArticles = searchResults
                                }
                            }
                        )
                    )
                    IconButton(onClick = { viewModel.home() }) {
                        Box(
                            Modifier
                                .size(37.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Green)
                        ) {
                            Icon(
                                Icons.Filled.Home,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                    IconButton(onClick = { viewModel.signOut() }) {
                        Box(
                            Modifier
                                .size(37.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Green)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            )
        }

    ) {

        Box(modifier = Modifier.fillMaxSize())
        {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 128.dp)
            ) {
                items(
                    items = articles,
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF6200EE)) // Purple color
                    .padding(5.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.Center, // Center horizontally
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Green)
                        .clickable { viewModel.navigateToAddArticle() }
                ) {
                    Icon(
                        Icons.Filled.AddBox,
                        contentDescription = null,
                       // tint = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                            .size(50.dp)// Center icon within Box
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
        // Filter articles based on title relevance
        article.title.contains(query, ignoreCase = true)
    }
    onSearchResults(searchResults)
}

