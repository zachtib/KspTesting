package com.zachtib.test.screens

import com.zachtib.ksp.Screen
import com.zachtib.test.ComposeScreen

@Screen
class ProfileScreen(val profileId: Long) : ComposeScreen() {
    override fun Content(): String {
        return "ProfileScreen for User $profileId"
    }
}