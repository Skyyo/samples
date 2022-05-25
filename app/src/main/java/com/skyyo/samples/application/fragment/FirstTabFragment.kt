package com.skyyo.samples.application.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.google.accompanist.insets.statusBarsPadding
import com.skyyo.samples.application.activity.BOTTOM_NAVIGATION_HEIGHT

class FirstTabFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Blue)
                    .statusBarsPadding()
                    .padding(bottom = BOTTOM_NAVIGATION_HEIGHT.dp)
            )
        }
    }
}