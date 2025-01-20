package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.databinding.FragmentQuestionComparisonBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.QuestionComparisonAdapter
import com.anurupjaiswal.learnandachieve.model.QuestionItem

class QuestionComparisonFragment : Fragment() {

    private var _binding: FragmentQuestionComparisonBinding? = null
    private val binding get() = _binding!!

    // Sample Data for Questions
    private val questionList = listOf(
        QuestionItem(
            "Question 1",
            "Read Paragraph Carefully. And Answer The Questions.",
            "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
            "A. The Industrial Revolution",
            "B. The New Deal",
            "C. James Madison",
            "D. Madam C.J. Walker"
        ),
        QuestionItem(
            "Question 2",
            "Understand the following passage and answer the questions below.",
            "The rise of digital technologies has transformed how people communicate and work.",
            "A. Digital Revolution",
            "B. Industrial Revolution",
            "C. Information Age",
            "D. Space Age"
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionComparisonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setting up the RecyclerView with adapter and layout manager
        binding.rcvQuestionComparison.layoutManager = LinearLayoutManager(requireContext())
        val adapter = QuestionComparisonAdapter(questionList)
        binding.rcvQuestionComparison.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clean up to avoid memory leaks
    }
}