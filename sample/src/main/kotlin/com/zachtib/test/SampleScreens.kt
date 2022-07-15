package com.zachtib.test

import com.zachtib.ksp.Screen

@Screen
class HomeScreen : ComposeScreen() {
    override fun Content(): String {
        return "HomeScreen"
    }
}

@Screen
class SettingsScreen(/* TODO: Don't remove me */) : ComposeScreen() {
    // Empty constructor is to confirm both empty and implicitly empty constructors
    // both have the same behavior
    override fun Content(): String {
        return "SettingsScreen"
    }
}

@Screen
class ProfileScreen(val profileId: Long) : ComposeScreen() {
    override fun Content(): String {
        return "ProfileScreen for User $profileId"
    }
}

@Screen
class ListingScreen(val listingId: Long, val shopName: String) : ComposeScreen() {
    override fun Content(): String {
        return "ListingScreen for Listing $listingId and Shop $shopName"
    }
}
