package com.skyyo.samples.application.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.findNavController
import com.skyyo.samples.features.languagePicker.LanguagePicker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides findNavController().currentBackStackEntry!!,
            ) {
                LanguagePicker()
            }
        }
    }
}