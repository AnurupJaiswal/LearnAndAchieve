package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.databinding.FragmentModuleBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.ModulesAdapter
import com.anurupjaiswal.learnandachieve.model.Module

class ModuleFragment : Fragment() {

    private var _binding: FragmentModuleBinding? = null
    private val binding get() = _binding!!

    private var modules: List<Module> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModuleBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }


    private fun init() {
        val subjectName = arguments?.getString("subject_name") ?: "Unknown Subject"
        binding.tvSubjectName.text = subjectName

        // Prepare modules from arguments
        val moduleNames = arguments?.getStringArrayList("module_names") ?: arrayListOf()
        val moduleDescriptions = arguments?.getStringArrayList("module_descriptions") ?: arrayListOf()

        modules = moduleNames.zip(moduleDescriptions) { name, description ->
            Module(name, description)
        }

        // Setup RecyclerView
        setupRecyclerView()
    }


    private fun setupRecyclerView() {
        val modulesAdapter = ModulesAdapter(modules) { module ->
            handleModuleClick(module) // Your click handler
        }
        binding.recyclerViewModules.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = modulesAdapter
        }
    }

    private fun handleModuleClick(module: Module) {
        // Prepare the bundle to pass data to the TopicFragment
        val bundle = Bundle().apply {
            putString("tpoic_name", module.topic) // Pass the module name
        }

        // Use NavigationManager to navigate to the TopicFragment
        NavigationManager.navigateToFragment(
            findNavController(), // Get NavController instance
            R.id.topicFragment, // Destination ID
            bundle // Pass data bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
