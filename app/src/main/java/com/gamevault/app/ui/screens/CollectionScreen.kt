package com.gamevault.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.gamevault.app.R
import com.gamevault.app.data.model.CollectionItem
import com.gamevault.app.data.model.CollectionStatus
import com.gamevault.app.ui.components.AppScaffold
import com.gamevault.app.ui.components.EmptyState
import com.gamevault.app.ui.navigation.Routes
import com.gamevault.app.ui.theme.CardBorder
import com.gamevault.app.ui.theme.DarkSurface
import com.gamevault.app.ui.theme.DarkSurfaceVariant
import com.gamevault.app.ui.theme.PurplePrimary
import com.gamevault.app.ui.theme.RatingGold
import com.gamevault.app.ui.theme.ScrimBottom
import com.gamevault.app.ui.theme.ScrimMid
import com.gamevault.app.ui.theme.TealAccent
import com.gamevault.app.ui.theme.TextPrimary
import com.gamevault.app.ui.theme.TextSecondary
import com.gamevault.app.ui.viewmodel.CollectionViewModel

@Composable
fun CollectionScreen(navController: NavHostController) {
    val viewModel: CollectionViewModel = hiltViewModel()
    val items by viewModel.items.collectAsState()
    val status by viewModel.status.collectAsState()

    var tabIndex by remember { mutableIntStateOf(0) }

    AppScaffold(currentRoute = Routes.COLLECTION, navController = navController) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // Header
            Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = stringResource(R.string.collection_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${items.size} game${if (items.size == 1) "" else "s"} in this tab",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            // Pill tabs
            PillTabs(
                tabs = listOf(
                    stringResource(R.string.tab_playing),
                    stringResource(R.string.tab_finished),
                    stringResource(R.string.tab_wishlist)
                ),
                selectedIndex = tabIndex,
                onSelect = { idx ->
                    tabIndex = idx
                    viewModel.setStatus(
                        when (idx) {
                            0 -> CollectionStatus.PLAYING
                            1 -> CollectionStatus.FINISHED
                            else -> CollectionStatus.WISHLIST
                        }
                    )
                }
            )

            // Offline banner
            if (items.any { !it.synced }) {
                Surface(
                    color = PurplePrimary.copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        stringResource(R.string.offline_changes_pending),
                        style = MaterialTheme.typography.labelSmall,
                        color = PurplePrimary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            // Content or empty state
            if (items.isEmpty()) {
                EmptyState(
                    emoji = "🎮",
                    title = "Your ${tabLabel(tabIndex)} is empty",
                    message = "Start building your collection by adding games from Discover or Search.",
                    ctaText = "Discover Games",
                    onCta = { navController.navigate(Routes.HOME) }
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
                ) {
                    items(items, key = { it.itemId }) { item ->
                        CollectionCard(item) {
                            navController.navigate(Routes.detail(item.rawgId))
                        }
                    }
                }
            }
        }
    }
}

private fun tabLabel(idx: Int) = when (idx) {
    0 -> "Playing list"
    1 -> "Finished shelf"
    else -> "Wishlist"
}

@Composable
private fun PillTabs(
    tabs: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEachIndexed { idx, label ->
            val selected = idx == selectedIndex
            val container by animateColorAsState(
                targetValue = if (selected) PurplePrimary else DarkSurfaceVariant,
                label = "pillBg"
            )
            val textColor = if (selected) Color(0xFF1A1030) else TextSecondary

            Surface(
                color = container,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onSelect(idx) }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        label,
                        style = MaterialTheme.typography.labelMedium,
                        color = textColor,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun CollectionCard(item: CollectionItem, onClick: () -> Unit) {
    androidx.compose.material3.Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, CardBorder),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = DarkSurface),
        modifier = Modifier.aspectRatio(0.68f)
    ) {
        Box(Modifier.fillMaxSize()) {
            AsyncImage(
                model = item.coverImageUrl,
                contentDescription = item.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Scrim
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Transparent,
                            0.5f to ScrimMid,
                            1f to ScrimBottom
                        )
                    )
            )

            // DONE badge
            if (item.status == CollectionStatus.FINISHED) {
                Surface(
                    color = TealAccent,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                ) {
                    Text(
                        stringResource(R.string.done_badge),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF062B26),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Bottom: title + progress
            Column(
                Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    item.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold
                )
                if (item.status == CollectionStatus.PLAYING) {
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { item.progressPercent / 100f },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                        color = PurplePrimary,
                        trackColor = Color(0x33FFFFFF)
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "${item.progressPercent}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = RatingGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}