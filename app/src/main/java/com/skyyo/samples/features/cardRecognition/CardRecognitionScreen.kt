package com.skyyo.samples.features.cardRecognition

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.wallet.PaymentCardRecognitionResult

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun CardRecognitionScreen(viewModel: CardRecognitionViewModel = hiltViewModel()) {
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
    val cardRecognitionIntent by viewModel.cardRecognitionPendingIntent.collectAsStateWithLifecycle()
    val isCardRecognitionSupported by viewModel.isCardRecognitionSupported.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp)
    ) {
        if (isCardRecognitionSupported) {
            Button(
                modifier = Modifier.padding(bottom = 10.dp),
                onClick = { cardRecognitionLauncher.launch(IntentSenderRequest.Builder(cardRecognitionIntent!!.intentSender).build()) }
            ) {
                Text(text = "scan card")
            }
            val hintTextStyle = remember { TextStyle(fontSize = 10.sp, fontWeight = FontWeight.W300) }
            Text(
                text = "(i) Card recognition return mocked data on test environment, use ENVIRONMENT_PRODUCTION for testing with real cards (CardRecognitionViewModel.createPaymentsClient method)",
                style = hintTextStyle
            )
        } else {
            Text(text = "Sorry, but card recognition not supported for your device yet. Show text inputs for card details")
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
    Toast.makeText(context, cardResultText, Toast.LENGTH_LONG).show()
}
