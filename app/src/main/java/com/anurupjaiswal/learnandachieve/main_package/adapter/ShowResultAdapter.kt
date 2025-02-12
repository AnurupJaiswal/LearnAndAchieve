package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.databinding.ItemResultBinding
import com.anurupjaiswal.learnandachieve.model.ShowResult

import com.anurupjaiswal.learnandachieve.model.ShowResultData
class ShowResultAdapter(
    private val showResults: List<ShowResultData>,
    private val onResultClick: (String, String) -> Unit // Pass both IDs
) : RecyclerView.Adapter<ShowResultAdapter.ResultViewHolder>() {

    inner class ResultViewHolder(private val binding: ItemResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(showResult: ShowResultData) {
            binding.tvDate.text = "N/A"
            binding.tvExamTitle.text = showResult.mockTestName
            binding.tvMarks.text = "${showResult.score}/${showResult.totalNumberOfMarks}"

            binding.tvViewResult.setOnClickListener {
                onResultClick(showResult.mockTestSubmissions_id, showResult.mockTest_id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(showResults[position])
    }

    override fun getItemCount(): Int = showResults.size
}
