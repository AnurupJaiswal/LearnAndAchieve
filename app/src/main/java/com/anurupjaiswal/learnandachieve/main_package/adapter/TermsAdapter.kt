package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.model.TermCondition

class TermsAdapter(private val termsList: List<TermCondition>) :
    RecyclerView.Adapter<TermsAdapter.TermsViewHolder>() {

    inner class TermsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val numberTextView: TextView = view.findViewById(R.id.numberTextView)
        val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TermsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_term_condition, parent, false)
        return TermsViewHolder(view)
    }

    override fun onBindViewHolder(holder: TermsViewHolder, position: Int) {
        val term = termsList[position]
        holder.numberTextView.text = term.number
        holder.descriptionTextView.text = term.description
    }

    override fun getItemCount(): Int = termsList.size
}
