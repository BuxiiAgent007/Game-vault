package com.gamevault.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.gamevault.app.data.model.CollectionStatus
import com.gamevault.app.data.model.Game
import com.gamevault.app.ui.components.ErrorMessage
import com.gamevault.app.ui.components.InfoChip
import com.gamevault.app.ui.components.LoadingBox
import com.gamevault.app.ui.theme.CardBorder
import com.gamevault.app.ui.theme.DarkSurface
import com.gamevault.app.ui.theme.DarkSurfaceVariant
import com.gamevault.app.ui.theme.PurplePrimary
import com.gamevault.app.ui.theme.RatingGold
import com.gamevault.app.ui.theme.ScrimBottom
import com.gamevault.app.ui.theme.ScrimMid
import com.gamevault.app.ui.theme.TextPrimary
import com.gamevault.app.ui.theme.TextSecondary
import com.gamevault.app.ui.viewmodel.DetailViewModel
import com.gamevault.app.util.UiState

@Composable
fun GameDetailScreen(navController: NavHostController) {
    val viewModel: DetailViewModel = hiltViewModel()
    val gameState by viewModel.game.collectAsState()
    val collectionItem by viewModel.collectionItem.collectAsState()

    when (val s = gameState) {
        is UiState.Loading -> LoadingBox()
        is UiState.Error -> ErrorMessage(
            message = friendlyError(s.message),
            onRetry = viewModel::load
        )
        is UiState.Success -> DetailContent(
            game = s.data,
            inCollection = collectionItem != null,
            onBack = { navController.popBackStack() },
            viewModel = viewModel
        )
    }
}

@Composable
private fun DetailContent(
    game: Game,
    inCollection: Boolean,
    onBack: () -> Unit,
    viewModel: DetailViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ── HERO with overlay top bar ──
        Box(
            Modifier
                .fillMaxWidth()
                .height(320.dp)
        ) {
            AsyncImage(
                model = game.backgroundImage,
                contentDescription = game.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Bottom gradient scrim (for title legibility)
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0f to ScrimMid,
                            0.4f to Color.Transparent,
                            1f to ScrimBottom
                        )
                    )
            )

            // Top bar overlay with back button
            Row(
                Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xCC000000),
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            }

            // Title overlay bottom
            Column(
                Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = game.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rating pill
                    val rating = game.rating
                    if (rating != null && rating > 0f) {
                        Surface(
                            color = Color(0xCC000000),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = RatingGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    String.format("%.1f", rating),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = RatingGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    // Metacritic pill
                    game.metacritic?.let { mc ->
                        Surface(
                            color = metacriticColor(mc),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                mc.toString(),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    // Release date
                    game.releaseDate?.let {
                        Text(
                            text = it.take(4),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        // ── CONTENT BELOW HERO ──
        Column(Modifier.padding(16.dp)) {

            // Genres row
            if (game.genres.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    game.genres.take(4).forEach { InfoChip(it) }
                }
                Spacer(Modifier.height(16.dp))
            }

            // Action buttons
            if (!inCollection) {
                Button(
                    onClick = { viewModel.addToCollection(CollectionStatus.PLAYING) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurplePrimary,
                        contentColor = Color(0xFF1A1030)
                    )
                ) {
                    Text(
                        stringResource(R.string.mark_playing),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { viewModel.addToCollection(CollectionStatus.WISHLIST) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Text(stringResource(R.string.add_wishlist), color = TextPrimary)
                    }
                    OutlinedButton(
                        onClick = { viewModel.addToCollection(CollectionStatus.FINISHED) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Text(stringResource(R.string.mark_finished), color = TextPrimary)
                    }
                }
            } else {
                Surface(
                    color = PurplePrimary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "✓ In your Vault",
                        color = PurplePrimary,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            // Platform section
            if (game.platforms.isNotEmpty()) {
                Spacer(Modifier.height(24.dp))
                SectionLabel(stringResource(R.string.platforms_label))
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    game.platforms.take(6).forEach { InfoChip(it) }
                }
            }

            // About
            game.description?.let {
                Spacer(Modifier.height(24.dp))
                SectionLabel(stringResource(R.string.about))
                Spacer(Modifier.height(8.dp))
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.3
                )
            }

            // Screenshots
            if (game.screenshots.isNotEmpty()) {
                Spacer(Modifier.height(24.dp))
                SectionLabel(stringResource(R.string.screenshots))
                Spacer(Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(game.screenshots) { url ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, CardBorder),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            modifier = Modifier
                                .width(260.dp)
                                .aspectRatio(16f / 9f)
                        ) {
                            AsyncImage(
                                model = url,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(width = 4.dp, height = 18.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(PurplePrimary)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun metacriticColor(score: Int): Color = when {
    score >= 75 -> Color(0xFF4CAF50)  // green
    score >= 50 -> Color(0xFFFFC107)  // amber
    else -> Color(0xFFE53935)         // red
}