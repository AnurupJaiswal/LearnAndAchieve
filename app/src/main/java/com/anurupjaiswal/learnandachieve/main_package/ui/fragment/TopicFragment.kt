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


        apiService?.getAllTopics(token, moduleId)?.enqueue(object :
            Callback<TopicResponse> {
            override fun onResponse(call: Call<TopicResponse>, response: Response<TopicResponse>) {
                if (response.isSuccessful) {
                    val topicResponse = response.body()
                    topicResponse?.let {


                        topicList.clear()
                        topicList.addAll(it.topicList)

                        if (topicList.isNotEmpty()) {
                            val firstTopicName = topicList[0].topic_name
                            binding.tvTopicName.text = firstTopicName
                        }
                        topicAdapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e("API Error", "Code: ${response.code()}, Message: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<TopicResponse>, t: Throwable) {
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
