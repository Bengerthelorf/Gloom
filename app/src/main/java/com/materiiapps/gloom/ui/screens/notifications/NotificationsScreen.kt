package com.materiiapps.gloom.ui.screens.notifications

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.materiiapps.gloom.R

class NotificationsScreen : Tab {
    override val options: TabOptions
        @Composable get() {
            val navigator = LocalTabNavigator.current
            val selected = navigator.current == this
            return TabOptions(
                0u,
                stringResource(R.string.navigation_inbox),
                rememberVectorPainter(if (selected) Icons.Filled.Notifications else Icons.Outlined.Notifications)
            )
        }

    @Composable
    override fun Content() = Screen()

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun Screen() {
        Scaffold(
            topBar = { TopBar() }
        ) {

        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun TopBar() {
        LargeTopAppBar(
            title = {
                Text(text = options.title)
            }
        )
    }
}