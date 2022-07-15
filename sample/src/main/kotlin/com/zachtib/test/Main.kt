package com.zachtib.test


fun main() {
    val screens = Screens()

    listOf(
        screens.navigateToHomeScreen(),
        screens.navigateToSettingsScreen(),
        screens.navigateToProfileScreen(profileId = 12345L),
        screens.navigateToListingScreen(listingId = 123, shopName = "Neat Shop"),
        screens.navigateToDetailScreen(
            itemId = 1234L,
            showExtraDetail = false,
            enableFanciness = true,
        ),
        screens.navigateToOrderHistoryScreen(
            currentUserId = 123L,
            ordersToShow = 10,
            showCompletedOrders = true,
            showCancelledOrders = false,
        ),
    ).forEach { screen: ComposeScreen ->
        println("Showing: ${screen.Content()}")
    }
}