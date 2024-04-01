package com.eug.swapz

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eug.swapz.datasources.ArticlesDataSource
import com.eug.swapz.datasources.SessionDataSource
import com.eug.swapz.ui.scenes.addarticle.AddArticleFactory
import com.eug.swapz.ui.scenes.articleDetail.ArticleDetailFactory
import com.eug.swapz.ui.scenes.intro.IntroFactory
import com.eug.swapz.ui.scenes.login.LoginFactory
import com.eug.swapz.ui.scenes.main.MainSceneFactory
import com.eug.swapz.ui.theme.SwapzTheme
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalMaterial3Api
@Composable
fun MyApp() {
    val navController = rememberNavController()
    val sessionDataSource = SessionDataSource()
    val articlesDataSource = ArticlesDataSource(database = FirebaseDatabase.getInstance())
    //WelcomeScene
    val introFactory = IntroFactory(navController)
    //LoginScene
    val loginFactory = LoginFactory(navController, sessionDataSource)
    val mainSceneFactory =
        MainSceneFactory(navController, sessionDataSource, articlesDataSource)
    //registerSceneFactory = RegisterSceneFactory(navController, sessionDataSource, capsulesDataSource)
    val articleDetailFactory = ArticleDetailFactory(navController, articlesDataSource, sessionDataSource)
    val addArticleFactory = AddArticleFactory(navController, sessionDataSource)

    val startDestination =
        if (sessionDataSource.isLoggedIn()) AppRoutes.MAIN.value else AppRoutes.INTRO.value

    SwapzTheme {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
        ) {

            composable(
                AppRoutes.INTRO.value
            ) {
                introFactory.create(null)
            }

            composable(
                AppRoutes.LOGIN.value
            ) {
                loginFactory.create(null)
            }

            composable(
                AppRoutes.MAIN.value
            ) {
                mainSceneFactory.create(null)

            }

            /*composable(
                AppRoutes.REGISTER.value
            ) {
                //registerSceneFactory.create(null)
            }*/

            composable(
                route = AppRoutes.ARTICLE_DETAIL.value + "/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType }),

            ) {
                //Forcing not be null, this is a bad practice
                val id: String = it.arguments?.getString("id")!!
                articleDetailFactory.create(id = id)
            }
            composable(
                AppRoutes.ADD_ARTICLE.value
            ){
                addArticleFactory.create(null)
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    SwapzTheme {
        MyApp()
    }
}
