package com.zachtib.test.screens

import com.zachtib.ksp.Screen
import com.zachtib.test.ComposeScreen

@Screen
class SettingsScreen(/* TODO: Don't remove me */) : ComposeScreen() {
    // Empty constructor is to confirm both empty and implicitly empty constructors
    // both have the same behavior
    override fun Content(): String {
        return "SettingsScreen"
    }
}