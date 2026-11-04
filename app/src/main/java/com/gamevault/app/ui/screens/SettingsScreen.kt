package com.gamevault.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.gamevault.app.R
import com.gamevault.app.data.model.AppLanguage
import com.gamevault.app.data.model.ThemeMode
import com.gamevault.app.ui.navigation.Routes
import com.gamevault.app.ui.viewmodel.AuthViewModel
import com.gamevault.app.ui.viewmodel.SettingsViewModel
import com.gamevault.app.util.UiState

@Composable
fun SettingsScreen(navController: NavHostController) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(authState) {
        // After logout the auth state flips to error (signed out) -> go to login
        if (authState is UiState.Error && authViewModel.isLoggedOut()) {
            navController.navigate(Routes.LOGIN) { popUpTo(0) }
        }
        // After account deletion -> go to login
        if (authState is UiState.Error &&
            (authState as UiState.Error).message == "ACCOUNT_DELETED"
        ) {
            navController.navigate(Routes.LOGIN) { popUpTo(0) }
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(24.dp))

        // ── THEME ──
        Text(stringResource(R.string.theme), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = settings.theme == ThemeMode.SYSTEM,
                onClick = { viewModel.setTheme(ThemeMode.SYSTEM) },
                label = { Text(stringResource(R.string.theme_system)) }
            )
            FilterChip(
                selected = settings.theme == ThemeMode.LIGHT,
                onClick = { viewModel.setTheme(ThemeMode.LIGHT) },
                label = { Text(stringResource(R.string.theme_light)) }
            )
            FilterChip(
                selected = settings.theme == ThemeMode.DARK,
                onClick = { viewModel.setTheme(ThemeMode.DARK) },
                label = { Text(stringResource(R.string.theme_dark)) }
            )
        }

        // ── LANGUAGE ──
        Spacer(Modifier.height(24.dp))
        Text(stringResource(R.string.language), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = settings.language == AppLanguage.EN,
                onClick = { viewModel.setLanguage(AppLanguage.EN) },
                label = { Text(stringResource(R.string.lang_en)) }
            )
            FilterChip(
                selected = settings.language == AppLanguage.ZU,
                onClick = { viewModel.setLanguage(AppLanguage.ZU) },
                label = { Text(stringResource(R.string.lang_zu)) }
            )
            FilterChip(
                selected = settings.language == AppLanguage.TN,
                onClick = { viewModel.setLanguage(AppLanguage.TN) },
                label = { Text(stringResource(R.string.lang_tn)) }
            )
        }

        // ── NOTIFICATIONS ──
        Spacer(Modifier.height(24.dp))
        Text(stringResource(R.string.notifications), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.notif_release), Modifier.weight(1f))
            Switch(
                checked = settings.notifRelease,
                onCheckedChange = { viewModel.setNotifRelease(it) }
            )
        }
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.notif_badges), Modifier.weight(1f))
            Switch(
                checked = settings.notifBadges,
                onCheckedChange = { viewModel.setNotifBadges(it) }
            )
        }

        // ── LOGOUT ──
        Spacer(Modifier.height(32.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { authViewModel.logout() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.logout))
        }

        // ── DELETE ACCOUNT ── (design doc §3.3)
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = { showDeleteConfirm = true },
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            ),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Delete Account")
        }
    }

    // ── DELETE CONFIRMATION DIALOG ──
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Account?") },
            text = {
                Text(
                    "This will permanently delete your account and all " +
                            "collection data. This action cannot be undone."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    authViewModel.deleteAccount()
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}