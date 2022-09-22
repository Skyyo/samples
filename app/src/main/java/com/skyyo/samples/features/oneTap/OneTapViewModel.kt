package com.skyyo.samples.features.oneTap

import android.content.Intent
import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.skyyo.samples.application.CODE_200
import com.skyyo.samples.application.Destination
import com.skyyo.samples.application.models.OneTapAuthoriseUserRequest
import com.skyyo.samples.application.network.calls.OneTapCalls
import com.skyyo.samples.extensions.navigateWithObject
import com.skyyo.samples.extensions.tryOrNull
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

// sample web app with one tap support can be found here - https://easy-foul-ketchup.glitch.me
private const val WEB_CLIENT_ID = "991166295182-b0s899mj3ev0rbqmuugs77390jgidm5g.apps.googleusercontent.com"
private const val IS_ONE_TAP_UI_REJECTED_KEY = "isOneTapUiRejected"

@HiltViewModel
class OneTapViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    private val navigationDispatcher: NavigationDispatcher,
    private val oneTapCalls: OneTapCalls
) : ViewModel() {
    val events = Channel<OneTapEvent>(Channel.UNLIMITED)
    val isOneTapUiRejected = handle.getStateFlow(IS_ONE_TAP_UI_REJECTED_KEY, false)

    val oneTapRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(WEB_CLIENT_ID)
                .setFilterByAuthorizedAccounts(true) // Only show accounts previously used to sign in.
                .build()
        )
        .setAutoSelectEnabled(true) // Automatically sign in when exactly one credential is retrieved.
        .build()

    fun oneTapAccepted(client: SignInClient, data: Intent?) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val credential = client.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken!!
            val response = tryOrNull { oneTapCalls.authorise(OneTapAuthoriseUserRequest(idToken)) }
            when {
                response?.code() == CODE_200 -> {
                    val user = response.body()!!
                    if (user.isCompleted) {
                        navigationDispatcher.emit {
                            it.popBackStack()
                            it.navigateWithObject(
                                route = Destination.OneTapAuthorised.route,
                                arguments = bundleOf(USER_KEY to user)
                            )
                        }
                    } else {
                        navigationDispatcher.emit {
                            it.popBackStack()
                            it.navigateWithObject(
                                route = Destination.OneTapSignUpFinish.route,
                                arguments = bundleOf(CREATE_USER_KEY to user)
                            )
                        }
                    }
                }
                else -> {
                    val message = when (response) {
                        null -> "No internet"
                        else -> "Update failed with code: ${response.code()}"
                    }
                    events.send(OneTapEvent.ShowToast(message))
                }
            }
        } catch (e: ApiException) {
            when (e.statusCode) {
                CommonStatusCodes.CANCELED -> {
                    handle[IS_ONE_TAP_UI_REJECTED_KEY] = true
                }
                CommonStatusCodes.NETWORK_ERROR -> {
                    // One-tap encountered a network error, try again or just ignore.
                }
                else -> {
                    // Couldn't get credential from result.
                }
            }
        }
    }

    fun oneTapRejected() {
        events.trySend(OneTapEvent.ShowToast("one tap sign in rejected"))
    }
}
