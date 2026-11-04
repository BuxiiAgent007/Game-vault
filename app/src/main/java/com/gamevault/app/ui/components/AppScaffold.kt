package com.gamevault.app.ui.components

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gamevault.app.R
import com.gamevault.app.ui.navigation.Routes
import com.gamevault.app.ui.theme.DarkSurface
import com.gamevault.app.ui.theme.DarkSurfaceVariant
import com.gamevault.app.ui.theme.PurpleGlow
import com.gamevault.app.ui.theme.PurplePrimary
import com.gamevault.app.ui.theme.TextSecondary

data class BottomTab(
    val route: String,
    val icon: ImageVector,
    @StringRes val labelRes: Int
)

val bottomTabs = listOf(
    BottomTab(Routes.HOME, Icons.Filled.Home, R.string.home_title),
    BottomTab(Routes.SEARCH, Icons.Filled.Search, R.string.search_title),
    BottomTab(Routes.COLLECTION, Icons.Filled.Star, R.string.collection_title),
    BottomTab(Routes.CALENDAR, Icons.Filled.DateRange, R.string.calendar_title),
    BottomTab(Routes.PROFILE, Icons.Filled.Person, R.string.profile_title)
)

@Composable
fun AppScaffold(
    currentRoute: String,
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = 0.dp,
                modifier = Modifier.height(72.dp)
            ) {
                bottomTabs.forEach { tab ->
                    val selected = currentRoute == tab.route
                    val glowAlpha by animateFloatAsState(
                        targetValue = if (selected) 1f else 0f,
                        label = "glow"
                    )

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                navController.navigate(tab.route) {
                                    popUpTo(Routes.HOME) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (selected) PurpleGlow.copy(alpha = 0.35f * glowAlpha)
                                        else DarkSurfaceVariant.copy(alpha = 0f)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = null,
                                    tint = if (selected) PurplePrimary else TextSecondary
                                )
                            }
                        },
                        label = {
                            Text(
                                text = stringResource(tab.labelRes),
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (selected) PurplePrimary else TextSecondary
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = androidx.compose.ui.graphics.Color.Transparent
                        )
                    )
                }
            }
        },
        content = content
    )
}