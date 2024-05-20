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
import com.eug.swapz.ui.scenes.chat.ChatFactory
import com.eug.swapz.ui.scenes.chatList.ChatListFactory
import com.eug.swapz.ui.scenes.editarticle.EditArticleFactory
import com.eug.swapz.ui.scenes.intro.IntroFactory
import com.eug.swapz.ui.scenes.inventory.InventoryFactory
import com.eug.swapz.ui.scenes.login.LoginFactory
import com.eug.swapz.ui.scenes.main.MainSceneFactory
import com.eug.swapz.ui.scenes.register.RegisterFactory
import com.eug.swapz.ui.scenes.filters.FilterFactory
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
    val registerFactory = RegisterFactory(navController, sessionDataSource)
    val articleDetailFactory = ArticleDetailFactory(navController, articlesDataSource, sessionDataSource)
    val addArticleFactory = AddArticleFactory(navController, sessionDataSource, articlesDataSource)
    val inventoryFactory = InventoryFactory(navController, sessionDataSource, articlesDataSource)
    val filterFactory = FilterFactory(navController,sessionDataSource,articlesDataSource)
    val chatFactory = ChatFactory(navController, sessionDataSource, articlesDataSource)
    val chatListFactory = ChatListFactory(navController,sessionDataSource)
    val editArticleFactory = EditArticleFactory(navController, sessionDataSource, articlesDataSource)
    val startDestination = if (sessionDataSource.isLoggedIn()) AppRoutes.MAIN.value else AppRoutes.INTRO.value


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

            composable(
                AppRoutes.REGISTER.value
            ) {
                registerFactory.create(null)
            }

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
            composable(
                AppRoutes.INVENTORY.value
            ){
                inventoryFactory.create(null)
            }

            composable(
                route = "${AppRoutes.FILTER.value}/{category}", // Include the category parameter
                arguments = listOf(navArgument("category") { type = NavType.StringType })
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category")
                // Ensure category is not null before proceeding
                category?.let { categoryId ->
                    filterFactory.create(categoryId)
                }
            }
            /*composable(
                route = "${AppRoutes.CHAT.value}/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")
                // Ensure userId is not null before proceeding
                userId?.let { otherUserId ->
                    chatFactory.create(otherUserId)
                }
            }*/
            composable(
                route = "${AppRoutes.CHAT.value}/{userId}/{articleId}/{chatId}",
                arguments = listOf(
                    navArgument("userId") { type = NavType.StringType },
                    navArgument("articleId") { type = NavType.StringType },
                    navArgument("chatId"){ type = NavType.StringType}
                )
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")
                val articleId = backStackEntry.arguments?.getString("articleId")
                val chatId = backStackEntry.arguments?.getString("chatId")


                // Ensure userId and articleId are not null before proceeding
                if (userId != null && articleId != null && chatId != null) {
                    chatFactory.createWithArticleId(userId, articleId, chatId)
                }
            }
            composable(
                AppRoutes.CHAT_LIST.value
            ){
                chatListFactory.create(null)
            }
            composable(
                route = "${AppRoutes.CHAT.value}/{userId}/{chatId}",
                arguments = listOf(
                    navArgument("userId") { type = NavType.StringType },
                    navArgument("chatId") { type = NavType.StringType }
                )
            ){
                backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")
                val chatId = backStackEntry.arguments?.getString("chatId")
                if (userId != null && chatId != null) {
                    chatFactory.createFromList(userId, chatId)
                }
            }
            composable(
                route = "${AppRoutes.EDIT_ARTICLE.value}/{articleId}",
                arguments = listOf(navArgument("articleId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val articleId = backStackEntry.arguments?.getString("articleId")
                if (articleId != null) {
                    editArticleFactory.create(articleId)
                }
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
