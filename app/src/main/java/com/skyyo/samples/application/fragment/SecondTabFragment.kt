package com.skyyo.samples.application.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.findNavController
import com.skyyo.samples.R
import com.skyyo.samples.extensions.FixInAppLanguageSwitchLayoutDirection

class SecondTabFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides findNavController().currentBackStackEntry!!,
            ) {
                SecondTabContent()
            }
        }
    }

    @Composable
    fun SecondTabContent(viewModel: SecondTabViewModel = hiltViewModel()) = FixInAppLanguageSwitchLayoutDirection {
        val firstTabInput by viewModel.firstTabInput.collectAsState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = 56.dp)
        ) {
            TextField(value = firstTabInput, onValueChange = viewModel::setFirstTabInput)
            Text(
                text = stringResource(R.string.change_language),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .clickable(onClick = viewModel::goLanguage)
            )
        }
    }
}