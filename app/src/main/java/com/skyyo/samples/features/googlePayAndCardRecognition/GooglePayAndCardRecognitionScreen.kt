package com.skyyo.samples.features.googlePayAndCardRecognition

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import com.google.android.gms.wallet.PaymentCardRecognitionResult
import com.skyyo.samples.R
import com.skyyo.samples.extensions.getEnclosingActivity

@Composable
fun GooglePayAndCardRecognitionScreen(viewModel: GooglePayViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val cardRecognitionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) {
        viewModel.getCardRecognitionIntent()
        when (it.resultCode) {
            Activity.RESULT_OK -> {
                handlePaymentCardRecognitionSuccess(context, PaymentCardRecognitionResult.getFromIntent(it.data!!)!!)
            }
            Activity.RESULT_CANCELED -> {
                Toast.makeText(context, "card recognition canceled", Toast.LENGTH_LONG).show()
            }
        }
    }
    val activity = context.getEnclosingActivity()
    val cardRecognitionIntent by viewModel.cardRecognitionPendingIntent.collectAsState()
    val isCardRecognitionSupported by viewModel.isCardRecognitionSupported.collectAsState()
    val isWalletSupported by viewModel.isWalletSupported.collectAsState()
    val passData1 by viewModel.passData1.collectAsState()
    val passData2 by viewModel.passData2.collectAsState()

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp)
    ) {
        if (isCardRecognitionSupported) {
            Button(
                modifier = Modifier.padding(bottom = 20.dp),
                onClick = { cardRecognitionLauncher.launch(IntentSenderRequest.Builder(cardRecognitionIntent!!.intentSender).build()) }
            ) {
                Text(text = "scan card")
            }
        }

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
        }
    }
}

private fun handlePaymentCardRecognitionSuccess(
    context: Context,
    cardRecognitionResult: PaymentCardRecognitionResult
) {
    val creditCardExpirationDate = cardRecognitionResult.creditCardExpirationDate
    val expirationDate = creditCardExpirationDate?.let { "%02d/%d".format(it.month, it.year) }
    val cardResultText = "PAN: ${cardRecognitionResult.pan}\nExpiration date: $expirationDate"
    Log.e("Recognition", cardResultText)
    Toast.makeText(context, cardResultText, Toast.LENGTH_LONG).show()
}
