package com.skyyo.samples.features.autoComplete

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.LayoutRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.material.textfield.TextInputLayout
import com.skyyo.samples.R

@Composable
fun AndroidViewDropdown(
    modifier: Modifier = Modifier,
    countries: List<String>,
    selectedValue: String = "",
    filter: (String) -> List<String>,
    onSelect: (Int) -> Unit = {},
) {
    val context = LocalContext.current
    val textInputLayout = remember(countries) {
        (View.inflate(context, R.layout.text_input_field, null) as TextInputLayout).also {
            (it.editText as AutoCompleteTextView).apply {
                val adapter = FilterableAdapter(countries, filter, context)
                setOnItemClickListener { _, _, index, _ ->
                    onSelect(index)
                }
                setAdapter(adapter)
            }
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { textInputLayout },
        update = {
            (textInputLayout.editText as AutoCompleteTextView).setText(selectedValue)
        },
    )
}

private class FilterableAdapter(
    private val items: List<String>,
    private val filterFun: (String) -> List<String>,
    context: Context,
    @LayoutRes private val layoutId: Int = android.R.layout.simple_list_item_1
) : BaseAdapter(), Filterable {

    private val inflater = LayoutInflater.from(context)
    private var allItems: List<String>? = null
    private var filteredItems: List<String> = items
    private val filterLock = Any()

    private val adapterFilter: Filter = object : Filter() {
        override fun performFiltering(prefix: CharSequence?): FilterResults {
            if (allItems == null) {
                synchronized(filterLock) { allItems = items }
            }
            val results: List<String>
            if (prefix.isNullOrEmpty()) {
                synchronized(filterLock) {
                    results = allItems!!
                }
            } else {
                synchronized(filterLock) {
                    results = filterFun(prefix.toString())
                }
            }
            return FilterResults().apply {
                values = results
                count = results.size
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            filteredItems = results.values as List<String>
            if (results.count > 0) {
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }
    }

    override fun getCount(): Int = filteredItems.size

    override fun getItem(position: Int): Any = filteredItems[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(inflater, position, convertView, parent, layoutId)
    }

    private fun createViewFromResource(
        inflater: LayoutInflater,
        position: Int,
        convertView: View?,
        parent: ViewGroup,
        layoutId: Int
    ): View {
        val view: TextView = (convertView ?: inflater.inflate(layoutId, parent, false)) as TextView
        view.text = getItem(position) as String
        return view
    }

    override fun getFilter(): Filter = adapterFilter
}
