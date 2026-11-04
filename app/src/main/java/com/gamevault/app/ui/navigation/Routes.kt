package com.gamevault.app.ui.navigation

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val SEARCH = "search"
    const val COLLECTION = "collection"
    const val CALENDAR = "calendar"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val DETAIL = "detail/{rawgId}"

    fun detail(rawgId: Int) = "detail/$rawgId"
}
