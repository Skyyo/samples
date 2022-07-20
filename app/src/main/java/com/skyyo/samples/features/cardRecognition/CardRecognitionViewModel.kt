package com.skyyo.samples.features.cardRecognition

import android.app.Application
import android.app.PendingIntent
import android.content.Context
import androidx.lifecycle.*
import com.google.android.gms.wallet.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val CARD_RECOGNITION_INTENT_KEY = "cardRecognitionIntent"

@HiltViewModel
class CardRecognitionViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    application: Application
) : ViewModel() {
    private val paymentsClient: PaymentsClient = createPaymentsClient(application)
    val cardRecognitionPendingIntent = handle.getStateFlow<PendingIntent?>(CARD_RECOGNITION_INTENT_KEY, null)
    val isCardRecognitionSupported = cardRecognitionPendingIntent.map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), false)

    init {
        getCardRecognitionIntent()
    }

    private fun createPaymentsClient(context: Context): PaymentsClient {
        // card recognition return mocked data on test environment, use ENVIRONMENT_PRODUCTION for testing with real cards
        val walletOptions = Wallet.WalletOptions.Builder()
            .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
            .build()

        return Wallet.getPaymentsClient(context, walletOptions)
    }

    fun getCardRecognitionIntent() {
        val request = PaymentCardRecognitionIntentRequest.getDefaultInstance()
        paymentsClient
            .getPaymentCardRecognitionIntent(request)
            .addOnSuccessListener { intentResponse ->
                handle[CARD_RECOGNITION_INTENT_KEY] = intentResponse.paymentCardRecognitionPendingIntent
            }
            .addOnFailureListener { _ -> handle[CARD_RECOGNITION_INTENT_KEY] = null }
    }
}
