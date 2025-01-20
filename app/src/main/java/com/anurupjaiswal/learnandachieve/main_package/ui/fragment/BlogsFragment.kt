package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.databinding.FragmentBlogsBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.BlogAdapter
import com.anurupjaiswal.learnandachieve.main_package.adapter.SelectionAdapter
import com.anurupjaiswal.learnandachieve.model.Blog

class BlogsFragment : Fragment() {

    private var _binding: FragmentBlogsBinding? = null
    private val binding get() = _binding!!

    private var popupWindow: PopupWindow? = null
    private lateinit var blogAdapter: BlogAdapter
    private val blogs = mutableListOf<Blog>()
    private var currentPage = 1
    private val blogsPerPage = 4

    private val allBlogs = listOf(
        Blog("Empowering Students with Free E-Learning Services", "July 1, 2024", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.", "https://path.to/your/image1.jpg"),
        Blog("Blog 2", "July 2, 2024", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.", "https://path.to/your/image2.jpg"),
        Blog("Blog 3", "July 3, 2024", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.", "https://path.to/your/image3.jpg"),
        Blog("Blog 4", "July 4, 2024", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.", "https://path.to/your/image4.jpg"),
        Blog("Blog 5", "July 5, 2024", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.", "https://path.to/your/image5.jpg"),
        Blog("Blog 6", "July 6, 2024", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.", "https://path.to/your/image6.jpg")
    )

    private val categories = mapOf(
        "All Blogs" to allBlogs,
        "General" to allBlogs.subList(0, 3),
        "Education" to allBlogs.subList(2, 5)
    )

    private var selectedCategory = "All Blogs"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBlogsBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupCategorySelection()
        loadBlogs()

        return binding.root
    }

    private fun setupRecyclerView() {
        blogAdapter = BlogAdapter(emptyList())
        binding.rcvBlog.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvBlog.adapter = blogAdapter
    }

    private fun setupCategorySelection() {
        binding.llSelectBlogType.setOnClickListener {
            showPopup(
                binding.tvBlogType, binding.viewCategory,
                categories.keys.toList()
            ) { category ->
                selectedCategory = category
                currentPage = 1
                loadBlogs()
            }
        }
    }

    private fun loadBlogs() {
        val blogList = categories[selectedCategory] ?: emptyList()
        val paginatedBlogs = paginateBlogs(blogList)

        blogs.clear()
        blogs.addAll(paginatedBlogs)

        blogAdapter.updateBlogs(blogs)
    }

    private fun paginateBlogs(blogList: List<Blog>): List<Blog> {
        val startIndex = (currentPage - 1) * blogsPerPage
        val endIndex = (startIndex + blogsPerPage).coerceAtMost(blogList.size)
        return blogList.subList(startIndex, endIndex)
    }

    private fun showPopup(
        textView: TextView,
        view: View,
        items: List<String>,
        onItemSelected: (String) -> Unit
    ) {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.popup_selection, null)

        val recyclerView = popupView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = SelectionAdapter(items) { selectedText ->
            textView.text = selectedText
            onItemSelected(selectedText)
            popupWindow?.dismiss()
        }

        popupWindow = PopupWindow(
            popupView,
            view.width,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            isFocusable = true
            isOutsideTouchable = true
            setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.popup_background_with_shadow
                )
            )
            elevation = 10f
        }

        val location = IntArray(2)
        view.getLocationOnScreen(location)
        popupWindow?.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1] + view.height)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        popupWindow?.dismiss()
    }
}
