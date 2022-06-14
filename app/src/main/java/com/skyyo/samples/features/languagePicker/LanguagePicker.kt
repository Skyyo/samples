package com.skyyo.samples.features.languagePicker

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skyyo.samples.R
import com.skyyo.samples.utils.supportedLanguages
import com.skyyo.samples.worker.ExpeditedWorker
import com.skyyo.samples.worker.ForegroundWorker
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
fun LanguagePicker(viewModel: LanguageViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val text by viewModel.text.collectAsState()

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val events = remember(viewModel.events) {
        viewModel.events.receiveAsFlow().flowWithLifecycle(lifecycle)
    }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when(event) {
                is LanguageEvent.ShowConfirmationDialog -> {
                    context.showConfirmationDialog(event.newLanguage, viewModel)
                }
                is LanguageEvent.UpdateUIWithNewLanguage -> {
                    AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags(event.newLanguageCode)
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .statusBarsPadding()
            .padding(20.dp)
    ) {
        TextField(value = text, onValueChange = viewModel::setText)
        Button(modifier = Modifier.padding(vertical = 10.dp), onClick = {
            val request = OneTimeWorkRequestBuilder<ExpeditedWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
            WorkManager.getInstance(context).enqueue(request)
        }) {
            Text(text = "start expedited work")
        }

        Button(modifier = Modifier.padding(vertical = 10.dp), onClick = {
            val request = OneTimeWorkRequestBuilder<ForegroundWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
            WorkManager.getInstance(context).enqueue(request)
        }) {
            Text(text = "start foreground work")
        }
        Text(
            text = "Current language: " + currentLanguage.name,
            modifier = Modifier.padding(top = 10.dp)
        )
        for (language in supportedLanguages) {
            key(language.code) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = language == currentLanguage,
                        onClick = { viewModel.changeLanguage(language) }
                    )
                    Text(text = language.name, modifier = Modifier.padding(start = 10.dp))
                }
            }
        }
    }
}

private fun Context.showConfirmationDialog(newLanguage: Language, languageViewModel: LanguageViewModel) {
    MaterialAlertDialogBuilder(this)
        .setMessage("Are you sure you want to apply new language? App will be restarted")
        .setPositiveButton(R.string.ok) { _, _ -> languageViewModel.applyNewLanguage(newLanguage) }
        .setNegativeButton(R.string.cancel, null)
        .show()
}