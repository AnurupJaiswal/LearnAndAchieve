package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.databinding.ItemMockTestBinding
import com.anurupjaiswal.learnandachieve.model.MockTestItem


import java.text.SimpleDateFormat
import java.util.*
class MockTestAdapter(private val mockTestList: List<MockTestItem>) :
    RecyclerView.Adapter<MockTestAdapter.MockTestViewHolder>() {

    inner class MockTestViewHolder(private val binding: ItemMockTestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(mockTest: MockTestItem) {
            binding.title.text = mockTest.mockTestName
            binding.subjects.text = mockTest.subjectData.joinToString { it.subjectName }
            binding.totalQuestions.text = mockTest.totalQuestions.toString()
            binding.time.text = "${mockTest.durationInMinutes} mins"
            binding.noOfAttempts.text = mockTest.numberOfAttempts.toString()

            // Set Expiry Date

            // Hide unlock button if expired
            val dateFormat = SimpleDateFormat("dd MMMM, yyyy hh:mm a", Locale.getDefault())
            val expiryDate = dateFormat.parse(mockTest.expireDate)
            val currentDate = Calendar.getInstance().time

            if (expiryDate != null && currentDate.after(expiryDate)) {
                binding.note.text = "This mock test has expired."
                binding.unlockButton.visibility = View.GONE
            } else {
                binding.note.text = "You can access this test until ${mockTest.expireDate}."
                binding.unlockButton.visibility = View.VISIBLE
            }

            binding.unlockButton.setOnClickListener {
                // Handle navigation to test
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MockTestViewHolder {
        val binding = ItemMockTestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MockTestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MockTestViewHolder, position: Int) {
        holder.bind(mockTestList[position])
    }

    override fun getItemCount(): Int = mockTestList.size
}
