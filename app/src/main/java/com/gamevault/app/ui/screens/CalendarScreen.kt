package com.gamevault.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.gamevault.app.R
import com.gamevault.app.data.model.Game
import com.gamevault.app.ui.components.AppScaffold
import com.gamevault.app.ui.components.EmptyState
import com.gamevault.app.ui.components.ErrorMessage
import com.gamevault.app.ui.components.LoadingBox
import com.gamevault.app.ui.navigation.Routes
import com.gamevault.app.ui.theme.CardBorder
import com.gamevault.app.ui.theme.DarkSurface
import com.gamevault.app.ui.theme.DarkSurfaceVariant
import com.gamevault.app.ui.theme.PurplePrimary
import com.gamevault.app.ui.theme.TextPrimary
import com.gamevault.app.ui.theme.TextSecondary
import com.gamevault.app.ui.viewmodel.CalendarViewModel
import com.gamevault.app.util.UiState
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun CalendarScreen(navController: NavHostController) {
    val viewModel: CalendarViewModel = hiltViewModel()
    val state by viewModel.releases.collectAsState()

    AppScaffold(currentRoute = Routes.CALENDAR, navController = navController) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // Header
            Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = stringResource(R.string.calendar_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Upcoming game releases",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            when (val s = state) {
                is UiState.Loading -> LoadingBox()
                is UiState.Error -> ErrorMessage(
                    message = friendlyError(s.message),
                    onRetry = viewModel::load
                )
                is UiState.Success -> {
                    if (s.data.isEmpty()) {
                        EmptyState(
                            emoji = "📅",
                            title = "No upcoming releases",
                            message = "Check back later for new games.",
                            ctaText = "Refresh",
                            onCta = viewModel::load
                        )
                    } else {
                        val grouped = s.data
                            .groupBy { it.releaseDate?.substring(0, 7) ?: "TBA" }
                            .toSortedMap()

                        LazyColumn(contentPadding = PaddingValues(16.dp)) {
                            grouped.forEach { (month, games) ->
                                item(key = "header_$month") {
                                    MonthHeader(month)
                                }
                                items(games, key = { it.rawgId }) { game ->
                                    TimelineRow(game) {
                                        navController.navigate(Routes.detail(game.rawgId))
                                    }
                                }
                            }
                            item { Spacer(Modifier.height(16.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthHeader(monthKey: String) {
    val label = try {
        val ym = YearMonth.parse(monthKey)
        ym.format(DateTimeFormatter.ofPattern("MMMM yyyy")).uppercase()
    } catch (e: Exception) {
        monthKey.uppercase()
    }

    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(PurplePrimary.copy(alpha = 0.25f), DarkSurfaceVariant)
                )
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.titleMedium,
            color = PurplePrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TimelineRow(game: Game, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Timeline dot + line
        Column(
            Modifier.width(32.dp).padding(top = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(PurplePrimary)
            )
            Box(
                Modifier
                    .width(2.dp)
                    .height(90.dp)
                    .background(PurplePrimary.copy(alpha = 0.25f))
            )
        }

        // Card
        Card(
            onClick = onClick,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, CardBorder),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = game.backgroundImage,
                    contentDescription = game.name,
                    modifier = Modifier
                        .size(width = 70.dp, height = 90.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        game.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        game.releaseDate ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        color = PurplePrimary
                    )
                    if (game.platforms.isNotEmpty()) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            game.platforms.take(3).joinToString(" · "),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}