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
import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Constants
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.databinding.FragmentModuleBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.ModulesAdapter
import com.anurupjaiswal.learnandachieve.model.Module
import com.anurupjaiswal.learnandachieve.model.ModuleResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModuleFragment : Fragment() {

    private var _binding: FragmentModuleBinding? = null
    private val binding get() = _binding!!

    private var medium: String? = null
    private var subjectId: String? = null
    private var subjectName: String? = null
private  var apiService :ApiService? = null
    private var modules: List<Module> = emptyList()
 var token : String?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentModuleBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }


    private fun init() {
        apiService = RetrofitClient.client
        arguments?.let {
            subjectName = it.getString(Constants.subject_name)
            medium = it.getString(Constants.medium)
            subjectId = it.getString(Constants.subjectId)
            binding.tvSubjectName.text = subjectName
            token = "Bearer ${Utils.GetSession().token}"
            if (token != null && subjectId != null && medium != null) {
                fetchModules(token!!, subjectId!!, medium!!)
            } else {
                Toast.makeText(context, "Missing required data (token, subject ID, or medium)", Toast.LENGTH_SHORT).show()
            }
        }

        setupRecyclerView()
    }


    private fun setupRecyclerView() {


        binding.recyclerViewModules.apply {
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun handleModuleClick(module: Module) {
        val bundle = Bundle().apply {

            putString(Constants.moduleId,module.module_id)
        }

        // Use NavigationManager to navigate to the TopicFragment
        NavigationManager.navigateToFragment(
            findNavController(), // Get NavController instance
            R.id.topicFragment, // Destination ID
            bundle // Pass data bundle
        )
    }

    private fun fetchModules(token:String,subjectId: String, medium: String) {

        apiService?.getAllModulesBySubject(token,subjectId, medium)?.enqueue(object :
            Callback<ModuleResponse> {
            override fun onResponse(call: Call<ModuleResponse>, response: Response<ModuleResponse>) {
                if (response.isSuccessful) {
                    val moduleResponse = response.body()
                    modules = moduleResponse?.moduleList ?: emptyList()
                    if (response.isSuccessful) {
                        val moduleResponse = response.body()
                        modules = moduleResponse?.moduleList ?: emptyList()
                        val adapter =    ModulesAdapter(requireContext(),modules) { module ->
                            handleModuleClick(module)
                        }
 binding.recyclerViewModules.adapter = adapter
                    }
                } else {
                    Toast.makeText(context, "Failed to fetch modules", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ModuleResponse>, t: Throwable) {
                Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
