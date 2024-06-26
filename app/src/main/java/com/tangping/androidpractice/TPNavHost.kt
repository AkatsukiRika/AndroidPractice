package com.tangping.androidpractice

import android.app.Activity
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.tangping.androidpractice.ui.datastore.DataStoreDemoScreen
import com.tangping.androidpractice.ui.datastore.DataStoreDemoScreenCallback
import com.tangping.androidpractice.ui.gallery.ImageGalleryScreen
import com.tangping.androidpractice.ui.gallery.ImageGalleryScreenCallback
import com.tangping.androidpractice.ui.home.HomeScreen
import com.tangping.androidpractice.ui.home.HomeScreenCallback
import com.tangping.androidpractice.ui.memorize.MemoryRecallScreen
import com.tangping.androidpractice.ui.memorize.MemoryRecallScreenCallback
import com.tangping.androidpractice.ui.memorize.create.CreateMemoryCardsCallback
import com.tangping.androidpractice.ui.memorize.create.CreateMemoryCardsScreen
import com.tangping.androidpractice.ui.memorize.modify.ModifyMemoryCardsCallback
import com.tangping.androidpractice.ui.memorize.modify.ModifyMemoryCardsScreen
import com.tangping.androidpractice.ui.memorize.prepare.MemorizePreparationCallback
import com.tangping.androidpractice.ui.memorize.prepare.MemorizePreparationScreen
import com.tangping.androidpractice.ui.pager.PagerDemoActivity
import com.tangping.androidpractice.ui.scroll.ScrollEffectActivity
import com.tangping.androidpractice.ui.text.AutoSizeTextActivity

@Composable
fun TPNavHost(
    navController: NavHostController,
    activity: Activity,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeScreen.route,
        modifier = modifier
    ) {
        composable(route = HomeScreen.route) {
            val callback = object : HomeScreenCallback {
                override fun onImageGalleryClick() {
                    navController.navigate(ImageGallery.route) {
                        launchSingleTop = true
                    }
                }

                override fun onMemoryRecallClick() {
                    navController.navigate(MemorizePreparation.route) {
                        launchSingleTop = true
                    }
                }

                override fun onCreateMemoryCardsClick() {
                    navController.navigate(CreateMemoryCards.route) {
                        launchSingleTop = true
                    }
                }

                override fun onDataStoreDemoClick() {
                    navController.navigate(DataStoreDemo.route) {
                        launchSingleTop = true
                    }
                }

                override fun onPagerDemoClick() {
                    val intent = Intent(activity, PagerDemoActivity::class.java)
                    activity.startActivity(intent)
                }

                override fun onScrollEffectClick() {
                    val intent = Intent(activity, ScrollEffectActivity::class.java)
                    activity.startActivity(intent)
                }

                override fun onAutoSizeTextClick() {
                    val intent = Intent(activity, AutoSizeTextActivity::class.java)
                    activity.startActivity(intent)
                }
            }
            HomeScreen(callback)
        }

        composable(route = ImageGallery.route) {
            val callback = object : ImageGalleryScreenCallback {
                override fun onBtnCloseClick() {
                    navController.popBackStack(route = HomeScreen.route, inclusive = false)
                }
            }
            ImageGalleryScreen(callback)
        }

        composable(
            route = MemoryRecall.route,
            arguments = listOf(navArgument(MemoryRecall.PARAM_FILE_NAME) { type = NavType.StringType })
        ) {
            val callback = object : MemoryRecallScreenCallback {
                override fun onNavigateBack() {
                    navController.popBackStack(route = HomeScreen.route, inclusive = false)
                }
            }
            MemoryRecallScreen(
                callback,
                fileName = it.arguments?.getString(MemoryRecall.PARAM_FILE_NAME)
            )
        }

        composable(route = CreateMemoryCards.route) {
            val callback = object : CreateMemoryCardsCallback {
                override fun onNavigateBack() {
                    navController.popBackStack(route = HomeScreen.route, inclusive = false)
                }

                override fun goModifyScreen(fileName: String) {
                    val route = ModifyMemoryCards.route.substringBeforeLast("/") + "/$fileName"
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            }
            CreateMemoryCardsScreen(callback)
        }

        composable(
            route = ModifyMemoryCards.route,
            arguments = listOf(navArgument(ModifyMemoryCards.PARAM_FILE_NAME) { type = NavType.StringType })
        ) {
            val callback = object : ModifyMemoryCardsCallback {
                override fun onNavigateBack() {
                    navController.navigateUp()
                }
            }
            ModifyMemoryCardsScreen(
                callback,
                fileName = it.arguments?.getString(ModifyMemoryCards.PARAM_FILE_NAME)
            )
        }

        composable(
            route = MemorizePreparation.route
        ) {
            val callback = object : MemorizePreparationCallback {
                override fun onNavigateBack() {
                    navController.popBackStack(route = HomeScreen.route, inclusive = false)
                }

                override fun goRecallScreen(fileName: String) {
                    val route = MemoryRecall.route.substringBeforeLast("/") + "/$fileName"
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            }
            MemorizePreparationScreen(callback)
        }

        composable(
            route = DataStoreDemo.route
        ) {
            val callback = object : DataStoreDemoScreenCallback {
                override fun onNavigateBack() {
                    navController.popBackStack(route = HomeScreen.route, inclusive = false)
                }
            }
            DataStoreDemoScreen(callback)
        }
    }
}