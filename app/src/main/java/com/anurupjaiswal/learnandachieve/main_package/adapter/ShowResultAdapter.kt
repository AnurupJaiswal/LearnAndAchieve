package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.databinding.ItemResultBinding
import com.anurupjaiswal.learnandachieve.model.ShowResult

class ShowResultAdapter(
    private val showResults: List<ShowResult>,
    private val onResultClick: (ShowResult) -> Unit // Callback for item clicks
) : RecyclerView.Adapter<ShowResultAdapter.ResultViewHolder>() {

    inner class ResultViewHolder(private val binding: ItemResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(showResult: ShowResult) {
            binding.tvDate.text = showResult.date
            binding.tvExamTitle.text = showResult.examTitle
            binding.tvMarks.text = showResult.marks

            // Set up click listener for the result item
            binding.tvViewResult.setOnClickListener {
                onResultClick(showResult) // Callback to the fragment/activity
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(showResults[position]) // Bind data to the view
    }

    override fun getItemCount(): Int = showResults.size
}
