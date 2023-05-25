package com.tangping.androidpractice

interface TPDestination {
    val route: String
}

object HomeScreen : TPDestination {
    override val route: String
        get() = "home_screen"
}

object ImageGallery : TPDestination {
    override val route: String
        get() = "image_gallery"
}