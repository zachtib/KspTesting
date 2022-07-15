package com.zachtib.test

import com.zachtib.ksp.Screen
import com.zachtib.ksp.ScreenKey

abstract class ComposeScreen() {

}

@Screen
class HomeScreen : ComposeScreen() {

}

@Screen
class SettingsScreen(/* TODO: Don't remove me */) : ComposeScreen() {
    // Empty constructor is to confirm both empty and implicitly empty constructors
    // both have the same behavior
}

@Screen
class ProfileScreen(val profileId: Long) : ComposeScreen() {

}

@Screen
class ListingScreen(val listingId: Long, val shopId: String) : ComposeScreen() {

}

@Screen
class DetailScreen(
    @ScreenKey val screenKey: DetailScreenKey,
) : ComposeScreen() {

}

data class DetailScreenKey(
    val itemId: Long,
    val showExtraDetail: Boolean,
    val enableFanciness: Boolean,
)

fun main() {
    val screens = Screens()
    println(screens.navigateToHomeScreen())
    println(screens.navigateToProfileScreen(12345L))
}