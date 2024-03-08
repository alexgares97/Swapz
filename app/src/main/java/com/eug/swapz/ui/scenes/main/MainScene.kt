package com.eug.swapz.ui.scenes.main

import android.annotation.SuppressLint
import android.view.accessibility.AccessibilityEvent.MAX_TEXT_LENGTH
import androidx.annotation.DrawableRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eug.swapz.R
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.models.Article
import com.eug.swapz.ui.scenes.login.LoginFactory
import com.eug.swapz.ui.theme.SwapzTheme
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScene(viewModel: MainViewModel) {
    // Loads
    viewModel.fetch()

    // The Scaffold composable is used to create the top-level structure of the application.
    // It includes a TopAppBar with the application name as the title.
    // It also includes a button to sign out

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { viewModel.signOut() }) {
                        Box(
                            Modifier
                                .size(37.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Green) // Fondo verde
                        ) {
                            Icon(
                                Icons.Filled.ExitToApp,
                                contentDescription = null,
                                tint = Color.White, // Ícono en color blanco
                                modifier = Modifier
                                    .align(Alignment.Center) // Centrar el ícono dentro del botón
                                    .padding(10.dp)
                            )
                        }
                    }
                }
            )
        }
    ) {
        val articles by viewModel.articles.observeAsState(emptyList())

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
                       //     .background(Color.LightGray)
                            .clickable { viewModel.navigateToDetail(article) }
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(article.img),
                            contentDescription = null,
                            modifier = Modifier
                                .size(200.dp)
                                .padding(top = 70.dp, start = 12.dp, end = 12.dp)
                                .clip(RoundedCornerShape(8.dp))

                            ,
                            contentScale = ContentScale.Crop,//The line that will affect your image size and help you solve the problem.


                        )

                        // Adjust the bottom padding here

                        Row(verticalAlignment = Alignment.CenterVertically
                        ) {
                            /*val icon = getIconForCategory(article.name)
                            Icon(
                                painterResource(icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )*/

                            val max_title_length= 19 // Define your maximum text length threshold

                            Text(
                                text = if (article.title.length <= max_title_length) {
                                    article.title
                                } else {
                                    "${article.title.take(max_title_length)}..."
                                },
                                style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(start = 13.dp, end = 10.dp),
                                maxLines = 1
                            )
                        }
                        Text(
                            text = article.value.toString() + " €",
                            style = TextStyle(fontSize = 10.sp),
                            modifier = Modifier
                                .padding(start = 14.dp)
                        )
                    }
                }
            )
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

/*@DrawableRes
fun getIconForCategory(categoryName: String): Int {
    return when (categoryName) {
        "Estadios de football" -> R.drawable.ic_category1
        "Skateparks" -> R.drawable.ic_category2
        "Ferias" -> R.drawable.ic_category3
        "Discotecas" -> R.drawable.ic_category4
        "Snorkel" -> R.drawable.ic_category5
        "Fuentes" -> R.drawable.ic_category6
        "Asociaciones" -> R.drawable.ic_category7
        "Parques" -> R.drawable.ic_category8
        // Add more cases for other category names and corresponding icons
        else -> R.drawable.ic_default_category
    }
}*/