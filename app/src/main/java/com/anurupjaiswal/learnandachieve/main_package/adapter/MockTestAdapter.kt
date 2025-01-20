package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.databinding.ItemMockTestBinding
import com.anurupjaiswal.learnandachieve.model.MockTest

class MockTestAdapter(
    private val mockTestList: List<MockTest>,
    private val onItemClick: (MockTest) -> Unit
) : RecyclerView.Adapter<MockTestAdapter.MockTestViewHolder>() {

    inner class MockTestViewHolder(private val binding: ItemMockTestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(mockTest: MockTest) {
            with(binding) {
                title.text = mockTest.title
                subjects.text = mockTest.subjects
                totalQuestions.text = mockTest.totalQuestions
                time.text = mockTest.time
                noOfAttempts.text = mockTest.noOfAttempts
                note.text = mockTest.note

                unlockButton.setOnClickListener {
                    onItemClick(mockTest)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MockTestViewHolder {
        val binding = ItemMockTestBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MockTestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MockTestViewHolder, position: Int) {
        holder.bind(mockTestList[position])
    }

    override fun getItemCount(): Int = mockTestList.size
}
