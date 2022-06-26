package com.skyyo.samples.features.scanQrNoPermissions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

@Composable
fun QrNoPermissionsScreen() {
    val context = LocalContext.current
    val scanner = remember { GmsBarcodeScanning.getClient(context) }
    var scanResult by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = scanResult)
        Button(onClick = {
            scanner.startScan()
                .addOnSuccessListener { barcode ->
                    scanResult = barcode.rawValue ?: "no result"
                }
                .addOnFailureListener { e ->
                    scanResult = e.message ?: "error undefined"
                }
        }) {

        }
    }


}
