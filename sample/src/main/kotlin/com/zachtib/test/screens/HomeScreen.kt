package com.zachtib.test.screens

import com.zachtib.ksp.Screen
import com.zachtib.test.ComposeScreen

@Screen
class HomeScreen : ComposeScreen() {
    override fun Content(): String {
        return "HomeScreen"
    }
}