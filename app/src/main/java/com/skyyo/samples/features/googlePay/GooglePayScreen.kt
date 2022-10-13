package com.skyyo.samples.features.googlePay

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.skyyo.samples.R
import com.skyyo.samples.extensions.getEnclosingActivity

@Composable
fun GooglePayScreen(viewModel: GooglePayViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val activity = context.getEnclosingActivity()
    val isWalletSupported by viewModel.isWalletSupported.collectAsState()
    val passData1 by viewModel.passData1.collectAsState()
    val passData2 by viewModel.passData2.collectAsState()

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp)
    ) {
        if (isWalletSupported) {
            val titleTextStyle = remember { TextStyle(fontSize = 18.sp, fontWeight = FontWeight.W700) }
            val inputsTextStyle = remember { TextStyle(fontSize = 15.sp, fontWeight = FontWeight.W500) }
            Text(text = "Add to wallet", style = titleTextStyle, modifier = Modifier.padding(bottom = 10.dp))
            Text(text = "Pass data", style = inputsTextStyle, modifier = Modifier.padding(bottom = 5.dp))
            Row(
                modifier = Modifier.padding(bottom = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Points:", modifier = Modifier.padding(end = 5.dp))
                TextField(value = passData1, onValueChange = viewModel::setPassData1)
            }
            Row(
                modifier = Modifier.padding(bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Contacts:", modifier = Modifier.padding(end = 5.dp))
                TextField(value = passData2, onValueChange = viewModel::setPassData2)
            }

            val borderStroke = remember { BorderStroke(1.dp, Color(0xFF747775)) }
            val addToWalletShape = remember { RoundedCornerShape(50) }
            val backgroundColor = remember { Color(0xFF1F1F1F) }
            Button(
                modifier = Modifier
                    .height(48.dp)
                    .widthIn(min = 267.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(addToWalletShape)
                    .border(border = borderStroke, shape = addToWalletShape),
                onClick = { viewModel.saveToWallet(activity) },
                colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor)
            ) {
                Image(painter = painterResource(id = R.drawable.add_to_wallet), contentDescription = "")
            }
            val hintTextStyle = remember { TextStyle(fontSize = 10.sp, fontWeight = FontWeight.W300) }
            Text(
                text = "(i) To test \"add to wallet\" update GooglePayViewModel.saveToWallet implementation according to instructions from javadocs",
                style = hintTextStyle,
                modifier = Modifier.padding(top = 10.dp)
            )
        } else {
            Text(text = "Your device doesn't support google wallet. Show alternatives methods to save passes")
        }
    }
}
