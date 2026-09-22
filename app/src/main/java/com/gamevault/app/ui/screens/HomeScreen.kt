package com.gamevault.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.gamevault.app.R
import com.gamevault.app.data.model.Game
import com.gamevault.app.ui.components.AppScaffold
import com.gamevault.app.ui.components.ErrorMessage
import com.gamevault.app.ui.components.GameCard
import com.gamevault.app.ui.components.LoadingBox
import com.gamevault.app.ui.components.SectionTitle
import com.gamevault.app.ui.navigation.Routes
import com.gamevault.app.ui.theme.DarkSurfaceVariant
import com.gamevault.app.ui.theme.PurplePrimary
import com.gamevault.app.ui.theme.TextPrimary
import com.gamevault.app.ui.theme.TextSecondary
import com.gamevault.app.ui.viewmodel.HomeContent
import com.gamevault.app.ui.viewmodel.HomeViewModel
import com.gamevault.app.util.UiState

@Composable
fun HomeScreen(navController: NavHostController) {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.homeState.collectAsState()

    AppScaffold(currentRoute = Routes.HOME, navController = navController) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // ── Header ──
            Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "Discover",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Find your next adventure",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            // ── Fake search bar (routes to Search) ──
            Surface(
                color = DarkSurfaceVariant,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { navController.navigate(Routes.SEARCH) }
            ) {
                Row(
                    Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.search_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            when (val s = state) {
                is UiState.Loading -> LoadingBox()
                is UiState.Error -> ErrorMessage(
                    message = friendlyError(s.message),
                    onRetry = viewModel::load
                )
                is UiState.Success -> HomeContentBlock(s.data, navController)
            }
        }
    }
}

@Composable
private fun HomeContentBlock(content: HomeContent, navController: NavHostController) {
    LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
        // Trending
        item {
            SectionTitle(
                text = stringResource(R.string.trending),
                action = { SeeAllButton { navController.navigate(Routes.SEARCH) } }
            )
        }
        item { GameRow(content.trending, navController) }

        // Upcoming
        if (content.upcoming.isNotEmpty()) {
            item {
                SectionTitle(
                    text = stringResource(R.string.upcoming_releases),
                    action = { SeeAllButton { navController.navigate(Routes.CALENDAR) } }
                )
            }
            item { GameRow(content.upcoming, navController) }
        }

        // Recommended
        if (content.recommended.isNotEmpty()) {
            item {
                SectionTitle(
                    text = stringResource(R.string.recommended_for_you),
                    action = { SeeAllButton { navController.navigate(Routes.SEARCH) } }
                )
            }
            item { GameRow(content.recommended, navController) }
        }
    }
}

@Composable
private fun GameRow(games: List<Game>, navController: NavHostController) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(games, key = { it.rawgId }) { game ->
            GameCard(
                game = game,
                onClick = { navController.navigate(Routes.detail(game.rawgId)) },
                modifier = Modifier.width(150.dp)
            )
        }
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun SeeAllButton(onClick: () -> Unit) {
    Text(
        text = "See all",
        style = MaterialTheme.typography.labelMedium,
        color = PurplePrimary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.clickable { onClick() }
    )
}

@Composable
fun friendlyError(message: String): String =
    if (message == "RAWG_API_KEY") stringResource(R.string.error_rawg_key) else message