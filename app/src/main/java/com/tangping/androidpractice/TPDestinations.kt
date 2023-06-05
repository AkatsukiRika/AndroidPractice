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

object MemoryRecall : TPDestination {
    const val PARAM_FILE_NAME = "fileName"

    override val route: String
        get() = "memory_recall/{$PARAM_FILE_NAME}"
}

object MemorizePreparation : TPDestination {
    override val route: String
        get() = "memorize_preparation"
}

object CreateMemoryCards : TPDestination {
    override val route: String
        get() = "create_memory_cards"
}

object ModifyMemoryCards : TPDestination {
    const val PARAM_FILE_NAME = "fileName"

    override val route: String
        get() = "modify_memory_cards/{$PARAM_FILE_NAME}"
}