package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.main_package.adapter.SubjectsAdapter

import com.anurupjaiswal.learnandachieve.model.Module
import com.anurupjaiswal.learnandachieve.model.Subject

class StudyMaterialFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_study_material, container, false)

        // RecyclerView setup
        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerViewSubjects)
        recyclerView.layoutManager = GridLayoutManager(activity, 2)

        // List of subjects
        val subjects = listOf(
            Subject(
                "Science 1",
                R.drawable.subjecttest,
                listOf(
                    Module("Chapter 1", "Gravitation"),
                    Module("Chapter 2", "Periodic classification of elements")
                )
            ),
            Subject(
                "Mathematics",
                R.drawable.icon_homes,
                listOf(
                    Module("Chapter 1", "Mathematics - Module 1 Description"),
                    Module("Chapter 2", "Mathematics - Module 2 Description")
                )
            )
        )

        // Set adapter
        val adapter = SubjectsAdapter(requireContext(), subjects) { subject ->
            // Extract module names and descriptions as separate lists
            val moduleNames = subject.modules?.map { it.chapterName }
            val moduleDescriptions = subject.modules?.map { it.topic }

            // Create a bundle for passing data to ModuleFragment
            val bundle = Bundle().apply {
                putString("subject_name", subject.name)
                subject.iconResId?.let { putInt("subject_icon", it) }
                putStringArrayList("module_names", moduleNames?.let { ArrayList(it) })
                putStringArrayList("module_descriptions", moduleDescriptions?.let { ArrayList(it) })
            }

            // Use NavigationManager to navigate to ModuleFragment
            NavigationManager.navigateToFragment(
                navController = findNavController(),
                destinationId = R.id.moduleFragment,
                bundle = bundle
            )
        }
        recyclerView.adapter = adapter

        return rootView
    }
}
