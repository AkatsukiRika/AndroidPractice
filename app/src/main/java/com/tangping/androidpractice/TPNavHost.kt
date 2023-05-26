package com.tangping.androidpractice

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tangping.androidpractice.ui.gallery.ImageGalleryScreen
import com.tangping.androidpractice.ui.gallery.ImageGalleryScreenCallback
import com.tangping.androidpractice.ui.home.HomeScreen
import com.tangping.androidpractice.ui.home.HomeScreenCallback

@Composable
fun TPNavHost(
    navController: NavHostController,
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
    }
}