package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.databinding.FragmentBharatSatExamInstructionsBinding

class BharatSatExamInstructionsFragment : Fragment() {

    private var _binding: FragmentBharatSatExamInstructionsBinding? = null
    private val binding get() = _binding!!

    private var isExamBharatSat: Boolean = false
    private var bharatSatExamId: String? =null
    private var eHallTicketId: String? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isExamBharatSat = it.getBoolean("isExamBharatSat", false)
        }
        arguments?.let {
            bharatSatExamId = it.getString("bharatSatExamId", "")
        }
        arguments?.let {
            eHallTicketId = it.getString("eHallTicketId", "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBharatSatExamInstructionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mcvStartTest.visibility = if (isExamBharatSat) View.VISIBLE else View.GONE
        binding.mcvStartTest.setOnClickListener {
            val bundle = Bundle().apply {
                putBoolean("isExamBharatSat", true)
                putString("bharatSatExamId",bharatSatExamId)
                putString("eHallTicketId",eHallTicketId)
            }
            NavigationManager.navigateToFragment(
                findNavController(), R.id.BharatSatExamQueFragment,bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
