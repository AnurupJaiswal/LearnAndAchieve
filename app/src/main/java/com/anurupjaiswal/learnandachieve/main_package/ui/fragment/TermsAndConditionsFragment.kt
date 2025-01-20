package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.databinding.FragmentTermsAndConditionsBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.TermsAdapter
import com.anurupjaiswal.learnandachieve.model.TermCondition

class TermsAndConditionsFragment : Fragment() {

    private var _binding: FragmentTermsAndConditionsBinding? = null
    private val binding get() = _binding!! // Backing property to ensure non-null reference

    private lateinit var termsAdapter: TermsAdapter
    private lateinit var termsList: List<TermCondition>

    // init block for early initialization
    init {
        termsList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTermsAndConditionsBinding.inflate(inflater, container, false)

        recyclerViewSetup() // Initialize RecyclerView

        return binding.root // Return the root view from the binding
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }

    /**
     * Initialize the terms list
     */
    private fun termsList() {
        termsList = listOf(
            TermCondition("1.", "Coordinators must be at least 18 years old."),
            TermCondition("2.", "Coordinators must have completed at least the 12th standard."),
            TermCondition(
                "3.",
                "PRADNYA LEARN AND ACHIEVE EDUTECH will provide free online training, which all coordinators must attend as scheduled."
            ),
            TermCondition(
                "4.",
                "Coordinators are representatives on an incentives basis only and are not employees of PRADNYA LEARN AND ACHIEVE EDUTECH. No benefits will be provided beyond the prescribed incentives."
            ),
            TermCondition(
                "5.",
                "Coordinators will earn 12.5% incentives on their total sales, with the potential for additional special incentives as announced by PRADNYA LEARN AND ACHIEVE EDUTECH."
            ),
            TermCondition(
                "6.",
                "Coordinators have three products to sell: Bharat Sat examination registration, Mock test packages, and the Pradnya Study n Learn Mobile App."
            ),
            TermCondition(
                "7.",
                "Coordinators must link their bank accounts to the PRADNYA LEARN AND ACHIEVE EDUTECH payment system to receive payments."
            ),
            TermCondition(
                "8.",
                "Coordinators can withdraw their incentives once a week, with a minimum withdrawal amount of Rs. 1000 (one thousand rupees only)."
            ),
            TermCondition(
                "9.",
                " Withdrawals will be processed via NEFT to the coordinator's bank account after verification by PRADNYA LEARN AND ACHIEVE EDUTECH’s accounting staff."
            ),
            TermCondition(
                "10.",
                "Coordinators must provide accurate and truthful information when filling out the form to join as a coordinator."
            ),
            TermCondition(
                "11.",
"Coordinators must adhere to the standard ethics and practices of the sales team, as provided in the training sessions."
            ),
        )
    }

    /**
     * Initialize RecyclerView
     */
    private fun recyclerViewSetup() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        termsAdapter = TermsAdapter(termsList)
        binding.recyclerView.adapter = termsAdapter
    }
}
