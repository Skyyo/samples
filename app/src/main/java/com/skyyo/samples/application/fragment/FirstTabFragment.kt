package com.skyyo.samples.application.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.findNavController
import com.google.accompanist.insets.statusBarsPadding
import com.skyyo.samples.extensions.FixInAppLanguageSwitchLayoutDirection

class FirstTabFragment: Fragment() {

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
                FirstTabContent()
            }
        }
    }

    @Composable
    fun FirstTabContent(viewModel: FirstTabViewModel = hiltViewModel()) = FixInAppLanguageSwitchLayoutDirection {
        val text by viewModel.input.collectAsState()
        Text(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = 56.dp, top = 50.dp, start = 20.dp),
            text = "input: $text"
        )
    }
}