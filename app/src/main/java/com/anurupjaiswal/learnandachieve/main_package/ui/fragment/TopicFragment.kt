package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.net.Uri
import android.util.Log

import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
import com.anurupjaiswal.learnandachieve.basic.retrofit.Const
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Constants
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.databinding.FragmentTopicBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.TopicAdapter
import com.anurupjaiswal.learnandachieve.model.Topic
import com.anurupjaiswal.learnandachieve.model.TopicResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TopicFragment : Fragment() {

    private var _binding: FragmentTopicBinding? = null
    private val binding get() = _binding!!
    var apiService: ApiService? = null
    private lateinit var topicAdapter: TopicAdapter
    private val topicList = mutableListOf<Topic>()
    var moduleId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopicBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }


    private fun setupRecyclerView() {
        topicAdapter = TopicAdapter(requireContext(), topicList) { topic ->
            // Handle topic item click
            onTopicClick(topic)
        }
        binding.recyclerViewTopics.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = topicAdapter
        }
    }

    private fun onTopicClick(topic: Topic) {
        if (topic.youtube_links.isNotEmpty()) {
            val youtubeLink = topic.youtube_links[0]
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink))
            startActivity(intent)
        }


    }

    fun init() {
        apiService = RetrofitClient.client
        moduleId = arguments?.getString(Constants.moduleId)
        setupRecyclerView()

        val token = "Bearer ${Utils.GetSession().token}"

        Log.d("Token", token)
        Log.d("ModuleId", moduleId ?: "ModuleId is null")

        getTopics(moduleId!!, token)
    }

    fun getTopics(moduleId: String, token: String) {


        apiService?.getAllTopics(token, moduleId)?.enqueue(object : Callback<TopicResponse> {
            override fun onResponse(call: Call<TopicResponse>, response: Response<TopicResponse>) {
                when (response.code()) {
                    StatusCodeConstant.OK -> {
                        response.body()?.let { topicResponse ->
                            topicList.clear()
                            topicList.addAll(topicResponse.topicList)
                            if (topicList.isNotEmpty()) {
                                binding.tvTopicName.text = topicList[0].topic_name
                            }
                            topicAdapter.notifyDataSetChanged()
                        }
                    }
                    StatusCodeConstant.UNAUTHORIZED -> {
                        Utils.E("getAllTopics UNAUTHORIZED: ${response.message()}")
                        Utils.UnAuthorizationToken(requireContext())
                    }
                    StatusCodeConstant.BAD_REQUEST -> {
                        response.errorBody()?.let { errorBody ->
                            val apiError = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                            val displayMessage = apiError.error ?: "Bad Request Error"
                            Utils.E("getAllTopics BAD_REQUEST: $displayMessage")
                            Utils.T(requireContext(), displayMessage)
                        }
                    }
                    else -> {
                        Utils.E("getAllTopics Error: ${response.code()} - ${response.message()}")
                    }
                }
            }

            override fun onFailure(call: Call<TopicResponse>, t: Throwable) {
                Utils.E("getAllTopics onFailure: ${t.message}")
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
