package com.zachtib.test.screens

import com.zachtib.ksp.Screen
import com.zachtib.test.ComposeScreen

@Screen
class ListingScreen(val listingId: Long, val shopName: String) : ComposeScreen() {
    override fun Content(): String {
        return "ListingScreen for Listing $listingId and Shop $shopName"
    }
}
