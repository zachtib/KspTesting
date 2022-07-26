package com.zachtib.test.screens

import com.zachtib.ksp.Screen
import com.zachtib.ksp.ScreenKey
import com.zachtib.test.ComposeScreen

data class DetailScreenKey(
    val itemId: Long,
    val showExtraDetail: Boolean,
    val enableFanciness: Boolean,
)

@Screen
class DetailScreen(
    @ScreenKey val screenKey: DetailScreenKey,
) : ComposeScreen() {
    override fun Content(): String {
        return "DetailScreen for key=$screenKey"
    }
}