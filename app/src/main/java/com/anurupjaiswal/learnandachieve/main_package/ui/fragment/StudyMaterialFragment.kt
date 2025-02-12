package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Constants
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
import com.anurupjaiswal.learnandachieve.databinding.FragmentHomeBinding
import com.anurupjaiswal.learnandachieve.databinding.FragmentStudyMaterialBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.StudyMaterialAdapter
import com.anurupjaiswal.learnandachieve.main_package.adapter.SubjectsAdapter
import com.anurupjaiswal.learnandachieve.model.GetAllStudyMaterial

import com.anurupjaiswal.learnandachieve.model.Module
import com.anurupjaiswal.learnandachieve.model.Subject
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudyMaterialFragment : Fragment() {
    private var apiservice: ApiService? = null
    private var _binding: FragmentStudyMaterialBinding? = null
    private val binding get() = _binding!!

    var token: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStudyMaterialBinding.inflate(inflater, container, false)

        init()

        return binding.root
    }

    fun init() {

        token = Utils.GetSession().token


        apiservice = RetrofitClient.client
        binding.recyclerViewSubjects.layoutManager = GridLayoutManager(requireContext(), 2)


        fetchStudyMaterials()

    }


    private fun fetchStudyMaterials() {

        apiservice?.getStudyMaterials(
            limit = 10,
            offset = 0,
            authorization = "Bearer $token"
        )?.enqueue(object : Callback<GetAllStudyMaterial> {
            override fun onResponse(
                call: Call<GetAllStudyMaterial>,
                response: Response<GetAllStudyMaterial>
            ) {
                try {
                    if (response.code() == StatusCodeConstant.OK) {
                        val studyMaterialsResponse = response.body()

                        studyMaterialsResponse?.data?.let { studyMaterials ->
                            val adapter =
                                SubjectsAdapter(studyMaterials) { subjectId, medium, subjectName ->

                                    navigateTOModule(subjectId, medium, subjectName)
                                }

                            binding.recyclerViewSubjects.adapter = adapter
                        }
                    } else {
                        // Handle error for non-200 responses
                        handleStudyMaterialsApiError(response)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Utils.T(context, "Error processing the request.")
                }
            }

            override fun onFailure(call: Call<GetAllStudyMaterial>, t: Throwable) {
                call.cancel()
                t.printStackTrace()
                Utils.T(context, t.message ?: "Request failed. Please try again later.")
            }
        })
    }
    private fun handleStudyMaterialsApiError(response: Response<GetAllStudyMaterial>) {
        when (response.code()) {
            StatusCodeConstant.UNAUTHORIZED -> {
                Utils.UnAuthorizationToken(requireContext())
            }

            StatusCodeConstant.BAD_REQUEST -> {
                response.errorBody()?.let { errorBody ->
                    val apiError = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                    val errorMessage = apiError.error ?: "Bad Request Error"
                    E("handleStudyMaterialsApiError BAD_REQUEST: $errorMessage")
                }
            }

            else -> {
                E("handleStudyMaterialsApiError Error: ${response.code()} - ${response.errorBody()?.string()}")
            }
        }
    }
    fun navigateTOModule(subjectId: String, medium: String, subjectName: String) {
        val bundle = Bundle().apply {
            putString(Constants.subjectId, subjectId)
            putString(Constants.subject_name, subjectName)
            putString(Constants.medium, medium)
        }

        NavigationManager.navigateToFragment(
            findNavController(),
            R.id.moduleFragment,
            bundle
        )

    }

}
