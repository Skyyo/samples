package com.skyyo.samples.features.googlePayAndCardRecognition

import android.app.Activity
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import androidx.lifecycle.*
import com.google.android.gms.pay.Pay
import com.google.android.gms.pay.PayApiAvailabilityStatus
import com.google.android.gms.pay.PayClient
import com.google.android.gms.wallet.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.*
import javax.inject.Inject

private const val IS_GOOGLE_WALLET_SUPPORTED_KEY = "isGoogleWalletSupported"
private const val PASS_DATA1_KEY = "walletData1"
private const val PASS_DATA2_KEY = "walletData2"
private const val CARD_RECOGNITION_INTENT_KEY = "cardRecognitionIntent"

@HiltViewModel
class GooglePayViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    application: Application
) : ViewModel() {
    // We can handle "save to wallet" result only from activity (https://issuetracker.google.com/issues/239603300)
    private val saveToWalletRequestCode = 1

    private val paymentsClient: PaymentsClient = createPaymentsClient(application)
    private val walletClient: PayClient = Pay.getClient(application)
    val isWalletSupported = handle.getStateFlow(IS_GOOGLE_WALLET_SUPPORTED_KEY, false)
    val passData1 = handle.getStateFlow(PASS_DATA1_KEY, "0")
    val passData2 = handle.getStateFlow(PASS_DATA2_KEY, "0")
    val cardRecognitionPendingIntent = handle.getStateFlow<PendingIntent?>(CARD_RECOGNITION_INTENT_KEY, null)
    val isCardRecognitionSupported = cardRecognitionPendingIntent.map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), false)

    init {
        getCardRecognitionIntent()
        checkIfWalletIsSupported()
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

    private fun checkIfWalletIsSupported() {
        // Use SAVE_PASSES_JWT here in production (if walletClient.savePassesJwt will be used)
        walletClient.getPayApiAvailabilityStatus(PayClient.RequestType.SAVE_PASSES)
            .addOnSuccessListener { status ->
                handle[IS_GOOGLE_WALLET_SUPPORTED_KEY] =
                    status == PayApiAvailabilityStatus.AVAILABLE
            }
            .addOnFailureListener {
                handle[IS_GOOGLE_WALLET_SUPPORTED_KEY] = false
            }
    }

    fun setPassData1(passData1: String) {
        handle[PASS_DATA1_KEY] = passData1
    }

    fun setPassData2(passData2: String) {
        handle[PASS_DATA2_KEY] = passData2
    }

    /**
     * To test add to wallet you need to generate issuerEmail, issuerId, passClass, used inside
     * by following next link - https://wallet-lab-tools.web.app/issuers.
     * Make sure to use email, that will be used for testing "add to wallet" functionality.
     */
    fun saveToWallet(activity: Activity) {
        val passId = UUID.randomUUID().toString()
        val issuerEmail = "agamula90@gmail.com"
        val issuerId = "3388000000022121403"
        val passClass = "3388000000022121403.92e8518d-dd67-4b56-b935-a7a3d722f041"

        // We will send all wallet data to backend, it should process it and return jwt encoded string
        // Then we'll use this jwt string with "walletClient.savePassesJwt" instead of 'walletClient.savePasses' below
        val passToSave = """
            {
            "iss": "$issuerEmail",
            "aud": "google",
            "typ": "savetowallet",
            "iat": ${Date().time / 1000L},
            "origins": [],
            "payload": {
                "genericObjects": [
                    {
                        "id": "$issuerId.$passId",
                        "classId": "$passClass",
                        "genericType": "GENERIC_TYPE_UNSPECIFIED",
                        "hexBackgroundColor": "#4285f4",
                        "logo": {
                            "sourceUri": {
                                "uri": "https://storage.googleapis.com/wallet-lab-tools-codelab-artifacts-public/pass_google_logo.jpg"
                            }
                        },
                        "cardTitle": {
                            "defaultValue": {
                                "language": "en",
                                "value": "Google I/O '22  [DEMO ONLY]"
                            }
                        },
                        "subheader": {
                            "defaultValue": {
                                "language": "en",
                                "value": "Attendee"
                            }
                        },
                        "header": {
                            "defaultValue": {
                                "language": "en",
                                "value": "Alex McJacobs"
                            }
                        },
                        "barcode": {
                            "type": "QR_CODE",
                            "value": "$passId"
                        },
                        "heroImage": {
                            "sourceUri": {
                                "uri": "https://storage.googleapis.com/wallet-lab-tools-codelab-artifacts-public/google-io-hero-demo-only.jpg"
                            }
                        },
                        "textModulesData": [
                            {
                                "header": "POINTS",
                                "body": "${passData1.value}",
                                "id": "points"
                            },
                            {
                                "header": "CONTACTS",
                                "body": "${passData2.value}",
                                "id": "contacts"
                            }
                        ]
                    }
                ]
            }
        }
        """
        walletClient.savePasses(passToSave, activity, saveToWalletRequestCode)
    }
}
