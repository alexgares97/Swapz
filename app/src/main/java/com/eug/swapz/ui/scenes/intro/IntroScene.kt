package com.eug.swapz.ui.scenes.intro

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.eug.swapz.ui.theme.SwapzTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
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
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_background), // Ruta de la imagen en la carpeta res/drawable
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