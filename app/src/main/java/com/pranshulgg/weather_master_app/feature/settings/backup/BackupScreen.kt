package com.pranshulgg.weather_master_app.feature.settings.backup

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.components.NavigateUpBtn
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.SettingsTileIcon
import com.pranshulgg.weather_master_app.core.ui.components.TextAlertDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BackupScreen(navController: NavController) {
    val viewModel: BackupScreenViewModel = hiltViewModel()

    var isImportConfirmDialogOpen by remember { mutableStateOf(false) }
    var pendingImportUri by remember { mutableStateOf<Uri?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { viewModel.exportTo(it) }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            pendingImportUri = uri
            isImportConfirmDialogOpen = true
        }
    }

    LargeTopBarScaffold(
        title = stringResource(R.string.setting_backup_restore),
        navigationIcon = { NavigateUpBtn(navController) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SettingSection(
                title = stringResource(R.string.backup_export_action),
                tiles = listOf(
                    SettingTile.SwitchTile(
                        leading = { SettingsTileIcon(R.drawable.key_24px) },
                        title = stringResource(R.string.backup_include_api_keys),
                        description = stringResource(R.string.backup_include_api_keys_secondary),
                        checked = viewModel.includeApiKeys,
                        enabled = !viewModel.loading,
                        onCheckedChange = { viewModel.onIncludeApiKeysChanged(it) }
                    ),
                    SettingTile.ActionTile(
                        leading = { SettingsTileIcon(R.drawable.cloud_upload_24px) },
                        title = stringResource(R.string.backup_export_action),
                        description = stringResource(R.string.backup_export_action_secondary),
                        onClick = {
                            exportLauncher.launch(defaultBackupFileName())
                        }
                    ),
                )
            )
            SettingSection(
                title = stringResource(R.string.backup_import_action),
                tiles = listOf(
                    SettingTile.ActionTile(
                        leading = { SettingsTileIcon(R.drawable.cloud_download_24px) },
                        title = stringResource(R.string.backup_import_action),
                        description = stringResource(R.string.backup_import_action_secondary),
                        onClick = {
                            importLauncher.launch(arrayOf("application/json"))
                        }
                    ),
                )
            )
        }
    }

    TextAlertDialog(
        show = isImportConfirmDialogOpen,
        title = stringResource(R.string.backup_import_confirm_title),
        message = stringResource(R.string.backup_import_confirm_body),
        confirmText = stringResource(R.string.action_confirm),
        dismissText = stringResource(R.string.action_cancel),
        onConfirm = {
            pendingImportUri?.let { viewModel.importFrom(it) }
        },
        onDismiss = {
            isImportConfirmDialogOpen = false
            pendingImportUri = null
        }
    )
}

private fun defaultBackupFileName(): String {
    val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    return "weathermaster_backup_$date.json"
}
