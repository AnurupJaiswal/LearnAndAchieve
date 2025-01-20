package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.net.Uri

import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.databinding.FragmentTopicBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.TopicAdapter
import com.anurupjaiswal.learnandachieve.model.Topic


class TopicFragment : Fragment() {

    private var _binding: FragmentTopicBinding? = null
    private val binding get() = _binding!!

    private lateinit var topicAdapter: TopicAdapter
    private val topicList = mutableListOf<Topic>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopicBinding.inflate(inflater, container, false)

        // Get module name and topics from the arguments
        val moduleName = arguments?.getString("tpoic_name") ?: "Unknown"
        binding.tvModuleName.text = moduleName

        topicList.addAll(
            listOf(
                Topic(

                    "https://youtu.be/tht94Y4VX4o?si=hG11fwS2vtkfbG1P", // YouTube URL
                    "https://img.youtube.com/vi/tht94Y4VX4o/maxresdefault.jpg" // Thumbnail URL
                ),
                Topic(
                    "https://youtu.be/Gj4--Cdtam4?si=vpt5ZPF--wVLhUp_", // YouTube URL
                    "https://img.youtube.com/vi/Gj4--Cdtam4/maxresdefault.jpg" // Thumbnail URL
                ),

                Topic(
                    "https://youtu.be/dx4Teh-nv3A?si=w91980WXwxQ6UtVg", // YouTube URL
                    "https://img.youtube.com/vi/dx4Teh-nv3A/maxresdefault.jpg" // Thumbnail URL
                ),

                Topic(
                    "https://www.youtube.com/watch?v=vCsb0emDvFw", // YouTube URL
                    "https://img.youtube.com/vi/vCsb0emDvFw/sddefault.jpg" // Thumbnail URL
                )
            )
        )

        setupRecyclerView()

        return binding.root
    }

    private fun setupRecyclerView() {
        topicAdapter = TopicAdapter(requireContext(), topicList) { topic ->
            // Open the YouTube video in a browser or YouTube app
            onTopicClick(topic)
        }
        binding.recyclerViewTopics.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = topicAdapter
        }
    }

    private fun onTopicClick(topic: Topic) {
        // Create an Intent to open the YouTube video URL
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(topic.youtubeUrl))
        startActivity(intent)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
