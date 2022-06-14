package com.skyyo.samples.features.autoComplete

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.material.textfield.TextInputLayout
import com.skyyo.samples.R

@Composable
fun AndroidViewTextFieldWithDropDownSample(
    modifier: Modifier = Modifier,
    suggestions: List<String>,
    selectedValue: String = "",
    onSelect: (Int) -> Unit = {},
) {
    val context = LocalContext.current
    val adapter = remember(suggestions) {
        ArrayAdapter(context, android.R.layout.simple_list_item_1, suggestions)
    }
    val textInputLayout = remember {
        TextInputLayout.inflate(context, R.layout.text_input_field, null) as TextInputLayout
    }
    val autoCompleteTextView = remember { textInputLayout.editText as? AutoCompleteTextView }

    AndroidView(
        modifier = modifier,
        factory = {
            autoCompleteTextView?.apply {
                setAdapter(adapter)
                setText(selectedValue)
                setOnItemClickListener { _, _, index, _ -> onSelect(index) }
            }
            textInputLayout
        },
        update = {
            autoCompleteTextView?.setAdapter(adapter)
            autoCompleteTextView?.setText(selectedValue, false)
        },
    )
}
