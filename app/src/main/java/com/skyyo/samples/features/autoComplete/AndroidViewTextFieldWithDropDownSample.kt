package com.skyyo.samples.features.autoComplete

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.material.textfield.TextInputLayout
import com.skyyo.samples.R

@Composable
fun AndroidViewTextFieldWithDropDownSample(
    items: List<String>,
    selectedValue: String?,
    modifier: Modifier = Modifier,
    onSelect: (Int) -> Unit = {}
) {
    AndroidView(
        factory = { context ->
            val textInputLayout =
                TextInputLayout.inflate(context, R.layout.text_input_field, null) as TextInputLayout

            val autoCompleteTextView = textInputLayout.editText as? AutoCompleteTextView
            val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, items)
            autoCompleteTextView?.setAdapter(adapter)
            autoCompleteTextView?.setText(selectedValue, false)
            autoCompleteTextView?.setOnItemClickListener { _, _, index, _ -> onSelect(index) }
            textInputLayout
        },
        update = { textInputLayout ->
            val autoCompleteTextView = textInputLayout.editText as? AutoCompleteTextView
            val adapter =
                ArrayAdapter(textInputLayout.context, android.R.layout.simple_list_item_1, items)
            autoCompleteTextView?.setAdapter(adapter)
            autoCompleteTextView?.setText(selectedValue, false)
        },
        modifier = modifier
    )
}
