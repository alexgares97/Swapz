package com.eug.swapz.ui.scenes.main

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
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
    var searchText by remember { mutableStateOf(String()) }

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


        Box(modifier = Modifier.fillMaxSize())

        {
            if (filteredArticles.isEmpty()) {
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
            }else {
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
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(50.dp)// Center icon within Box
                    )
                }
            }
        }
    }
}
@Composable
fun CustomTopAppBar(viewModel: MainViewModel, searchText: String, onSearchTextChanged: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Search Box
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFE0E0E0))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                BasicTextField(
                    value = searchText,
                    onValueChange = { onSearchTextChanged(it) }, // Use the callback to update searchText
                    textStyle = TextStyle(fontSize = 12.sp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )
            }
        }
        // Profile Icon
        IconButton(
            onClick = { viewModel.navigateToInventory() },
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Profile",
                modifier = Modifier.size(24.dp)
            )
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
@OptIn(ExperimentalAnimationApi::class)
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
@Preview
@Composable
fun test(){

}
