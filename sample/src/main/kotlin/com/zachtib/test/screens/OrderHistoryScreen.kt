package com.zachtib.test.screens

import com.zachtib.ksp.AcceptsScreenKey
import com.zachtib.ksp.Screen
import com.zachtib.ksp.ScreenKey
import com.zachtib.test.ComposeScreen
import com.zachtib.test.ViewModel

data class OrderHistoryScreenKey(
    val currentUserId: Long,
    val ordersToShow: Int,
    val showCompletedOrders: Boolean,
    val showCancelledOrders: Boolean,
)

@Screen
class OrderHistoryScreen(
    @AcceptsScreenKey val viewModel: OrderHistoryViewModel,
) : ComposeScreen() {
    override fun Content(): String {
        return "OrderHistoryScreen with viewModel=${viewModel.orders()}"
    }
}

class OrderHistoryViewModel(
    @ScreenKey private val screenKey: OrderHistoryScreenKey,
) : ViewModel() {

    fun orders(): String {
        return "OrderHistoryViewModel with screenKey=$screenKey"
    }
}