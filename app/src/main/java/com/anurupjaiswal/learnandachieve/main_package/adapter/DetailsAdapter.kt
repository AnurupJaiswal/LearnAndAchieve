package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import org.json.JSONObject

class DetailsAdapter(private val jsonObject: JSONObject) :
    RecyclerView.Adapter<DetailsAdapter.DetailsViewHolder>() {

    private val data: List<Pair<String, String>> = parseJson(jsonObject)
    private val TAG = "DetailsAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_detail, parent, false)
        return DetailsViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {
        try {
            val (label, value) = data[position]
            holder.labelTextView.text = label
            holder.valueTextView.text = value
            Log.d(TAG, "Binding data: $label -> $value")
        } catch (e: Exception) {
            Log.e(TAG, "Error in onBindViewHolder: ${e.message}", e)
        }
    }

    override fun getItemCount(): Int = data.size

    class DetailsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val labelTextView: TextView = view.findViewById(R.id.labelTextView)
        val valueTextView: TextView = view.findViewById(R.id.valueTextView)
    }

    private fun parseJson(jsonObject: JSONObject): List<Pair<String, String>> {
        val result = mutableListOf<Pair<String, String>>()
        try {
            jsonObject.keys().forEach { key ->
                val value = jsonObject.optString(key, "N/A")
                result.add(Pair(key, value))
                Log.d(TAG, "Parsed key-value: $key = $value")
            }
        } catch (e: Exception) {

            Log.e(TAG, "Error parsing JSON: ${e.message}", e)
        }
        return result
    }
}

