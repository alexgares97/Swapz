package com.eug.swapz.ui.scenes.intro

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.eug.swapz.ui.theme.SwapzTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.eug.swapz.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalMaterial3Api
@Composable
fun IntroScene(viewModel: IntroViewModel) {
    val loadingTime = 2000L // Tiempo de carga en milisegundos

    LaunchedEffect(Unit) {
        delay(loadingTime)
        viewModel.navigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF173868), // RGBA(23, 56, 104, 255)
                        Color(0xFF2E5C8C)
                    ),

                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo2_sombra), // Ruta de la imagen en la carpeta res/drawable
                contentDescription = "Logo",
                modifier = Modifier
                    .size(400.dp) // Tamaño personalizado del logo
                    .padding(bottom = 32.dp) // Espacio de relleno en la parte inferior del logo
            )
        }
    }
}
//}


@OptIn(ExperimentalAnimationApi::class)
@Preview(showBackground = true)
@Composable
fun IntroScenePreview() {
    SwapzTheme {
        IntroFactory(
            navController = rememberNavController()
        ).create(null)
    }
}