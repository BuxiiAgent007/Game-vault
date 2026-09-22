package com.gamevault.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.gamevault.app.R
import com.gamevault.app.ui.components.AppScaffold
import com.gamevault.app.ui.components.SectionTitle
import com.gamevault.app.ui.navigation.Routes
import com.gamevault.app.ui.theme.CardBorder
import com.gamevault.app.ui.theme.DarkBackground
import com.gamevault.app.ui.theme.DarkSurface
import com.gamevault.app.ui.theme.DarkSurfaceVariant
import com.gamevault.app.ui.theme.PurpleDark
import com.gamevault.app.ui.theme.PurplePrimary
import com.gamevault.app.ui.theme.RatingGold
import com.gamevault.app.ui.theme.TealAccent
import com.gamevault.app.ui.theme.TextPrimary
import com.gamevault.app.ui.theme.TextSecondary
import com.gamevault.app.ui.viewmodel.ProfileViewModel
import kotlin.math.roundToInt

@Composable
fun ProfileScreen(navController: NavHostController) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val ui by viewModel.ui.collectAsState()
    val stats by viewModel.stats.collectAsState()

    AppScaffold(currentRoute = Routes.PROFILE, navController = navController) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // ══════════════════════════════════════════════
            //  HERO HEADER
            // ══════════════════════════════════════════════
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(PurpleDark.copy(alpha = 0.45f), DarkBackground)
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = DarkSurface,
                        shape = CircleShape,
                        border = BorderStroke(2.dp, PurplePrimary),
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null,
                                tint = PurplePrimary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = ui.displayName.ifBlank { "Player" },
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = ui.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Spacer(Modifier.height(6.dp))
                        stats?.let {
                            Surface(
                                color = TealAccent.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = stringResource(it.rankRes),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TealAccent,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // ══════════════════════════════════════════════
            //  STATS DASHBOARD
            // ══════════════════════════════════════════════
            SectionTitle(text = "Your Stats")

            stats?.let { s ->
                Column(Modifier.padding(horizontal = 16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        BigStatCard(
                            label = stringResource(R.string.stats_finished_year),
                            value = s.finishedThisYear.toString(),
                            accentColor = PurplePrimary,
                            modifier = Modifier.weight(1f)
                        )
                        BigStatCard(
                            label = stringResource(R.string.stats_total_collection),
                            value = s.totalInCollection.toString(),
                            accentColor = TealAccent,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        BigStatCard(
                            label = stringResource(R.string.stats_hours),
                            value = s.hoursLogged.roundToInt().toString(),
                            accentColor = RatingGold,
                            modifier = Modifier.weight(1f)
                        )
                        BigStatCard(
                            label = stringResource(R.string.stats_top_genre),
                            value = s.topGenre ?: "—",
                            accentColor = PurplePrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // ══════════════════════════════════════════════
            //  BADGES
            // ══════════════════════════════════════════════
            Spacer(Modifier.height(16.dp))
            SectionTitle(text = stringResource(R.string.badges))

            if (ui.badges.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    border = BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🏆", style = MaterialTheme.typography.displaySmall)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.no_badges_yet),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Column(
                    Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ui.badges.forEach { badge ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                            border = BorderStroke(1.dp, CardBorder),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = PurplePrimary.copy(alpha = 0.15f),
                                    shape = CircleShape,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("🏆", style = MaterialTheme.typography.titleLarge)
                                    }
                                }
                                Spacer(Modifier.width(14.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(badge.nameRes),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = stringResource(badge.descRes),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ══════════════════════════════════════════════
            //  SETTINGS BUTTON
            // ══════════════════════════════════════════════
            Spacer(Modifier.height(24.dp))
            OutlinedButton(
                onClick = { navController.navigate(Routes.SETTINGS) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.settings_title),
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  BIG STAT CARD — larger numbers, accent colored
// ═══════════════════════════════════════════════════════════════════
@Composable
private fun BigStatCard(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        border = BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = value,
                style = MaterialTheme.typography.displaySmall,
                color = accentColor,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
        }
    }
}