package com.gamevault.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.gamevault.app.R
import com.gamevault.app.ui.components.AppScaffold
import com.gamevault.app.ui.components.ErrorMessage
import com.gamevault.app.ui.components.GameCard
import com.gamevault.app.ui.components.LoadingBox
import com.gamevault.app.ui.navigation.Routes
import com.gamevault.app.ui.viewmodel.SearchViewModel
import com.gamevault.app.util.UiState

private val genres = listOf("action", "role-playing-games-rpg", "shooter", "adventure", "sports", "strategy")
private val years = listOf(2026, 2025, 2024)

@Composable
fun SearchScreen(navController: NavHostController) {
    val viewModel: SearchViewModel = hiltViewModel()
    val results by viewModel.results.collectAsState()
    val query by viewModel.query.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()

    AppScaffold(currentRoute = Routes.SEARCH, navController = navController) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.query.value = it },
                placeholder = { Text(stringResource(R.string.search_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )

            // Genre filter chips (RAWG genre slugs)
            Row(
                Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = selectedGenre == null,
                    onClick = { viewModel.setGenre(null) },
                    label = { Text(stringResource(R.string.all)) }
                )
                Spacer(Modifier.padding(4.dp))
                genres.forEach { genre ->
                    FilterChip(
                        selected = selectedGenre == genre,
                        onClick = { viewModel.setGenre(if (selectedGenre == genre) null else genre) },
                        label = { Text(genre.substringBefore("-").replaceFirstChar { it.uppercase() }) }
                    )
                    Spacer(Modifier.padding(4.dp))
                }
            }

            // Year filter chips
            Row(
                Modifier.horizontalScroll(rememberScrollState()).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = selectedYear == null,
                    onClick = { viewModel.setYear(null) },
                    label = { Text(stringResource(R.string.all)) }
                )
                Spacer(Modifier.padding(4.dp))
                years.forEach { year ->
                    FilterChip(
                        selected = selectedYear == year,
                        onClick = { viewModel.setYear(if (selectedYear == year) null else year) },
                        label = { Text(year.toString()) }
                    )
                    Spacer(Modifier.padding(4.dp))
                }
            }

            when (val s = results) {
                is UiState.Loading -> LoadingBox()
                is UiState.Error -> ErrorMessage(
                    message = friendlyError(s.message),
                    onRetry = viewModel::retry
                )
                is UiState.Success -> {
                    if (s.data.isEmpty()) {
                        Text(
                            stringResource(R.string.no_results),
                            modifier = Modifier.padding(24.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
                        ) {
                            items(s.data, key = { it.rawgId }) { game ->
                                GameCard(
                                    game = game,
                                    onClick = { navController.navigate(Routes.detail(game.rawgId)) }
                                )
                                Spacer(Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
