package com.skyyo.samples.application.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.skyyo.samples.R
import com.skyyo.samples.application.activity.BOTTOM_NAVIGATION_HEIGHT

class SecondTabFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Green)
                    .statusBarsPadding()
                    .padding(bottom = BOTTOM_NAVIGATION_HEIGHT.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "change language", modifier = Modifier.clickable {
                    findNavController().navigate(R.id.goLanguage)
                })
            }
        }
    }
}