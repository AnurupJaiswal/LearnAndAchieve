package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.databinding.FragmentBlogsBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.BlogAdapter
import com.anurupjaiswal.learnandachieve.main_package.adapter.SelectionAdapter
import com.anurupjaiswal.learnandachieve.model.BlogData
import com.anurupjaiswal.learnandachieve.model.GetAllBlogAppResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BlogsFragment : Fragment() {
    private var _binding: FragmentBlogsBinding? = null
    private val binding get() = _binding!!
    private var popupWindow: PopupWindow? = null
    private var categoryList =
        mutableListOf<Pair<String, String>>()
    private lateinit var blogAdapter: BlogAdapter
    private var currentPage = 1
    private val pageSize = 4  // Set page size to 4

    private val blogList = mutableListOf<BlogData>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBlogsBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        binding.btnPrev.setOnClickListener {
            if (currentPage > 1) {
                currentPage--
                updateRecyclerView()
            }
        }

        binding.btnNext.setOnClickListener {
            val totalPages = (blogList.size + pageSize - 1) / pageSize
            if (currentPage < totalPages) {
                currentPage++
                updateRecyclerView()
            }
        }
    }
    fun init() {
        setupRecyclerView()

        showLoading(true)
        setupCategorySelection()
        fetchBlogCategories()




    }




    private fun setupCategorySelection() {
        binding.llSelectBlogType.setOnClickListener {
            showPopup(
                binding.tvBlogType, binding.viewCategory,
                categoryList.map { it.first } // Extract only category names for display
            ) { selectedCategory ->
                binding.tvBlogType.text = selectedCategory
                val selectedCategoryId = categoryList.find { it.first == selectedCategory }?.second

                // Fetch blogs for the selected category (or all blogs if "All Blogs" is selected)
                fetchBlogsByCategory(selectedCategoryId ?: "")
            }
        }
    }

    private fun setupRecyclerView() {
        blogAdapter = BlogAdapter(emptyList()) { blogData ->
            val categoryName = categoryList.find { it.second == blogData.blog_category_id }?.first
                ?: "Unknown Category"

            navigateToBlogDetails(blogData.blog_id, categoryName) // Handle click event
        }
        binding.rcvBlog.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvBlog.adapter = blogAdapter
    }

    private fun navigateToBlogDetails(blogId: String, categoryName: String?) {
        val bundle = Bundle().apply {
            putString("blog_id", blogId)
            putString("categoryName", categoryName) // Add categoryName to the bundle

        }
        NavigationManager.navigateToFragment(
            findNavController(),
            R.id.BlogDetailsFragment,
            bundle
        )
    }

    private fun fetchBlogCategories() {
        showLoading(true)

        val apiService = RetrofitClient.client
        val token = "Bearer ${Utils.GetSession().token}"

        apiService.getAllBlogApp(token).enqueue(object : Callback<GetAllBlogAppResponse> {
            override fun onResponse(
                call: Call<GetAllBlogAppResponse>,
                response: Response<GetAllBlogAppResponse>
            ) {
                if (response.isSuccessful) {
                    val blogCategories = response.body()?.data?.blogCategoryData
                    val data = response.body()?.data

                    if (blogCategories != null) {
                        categoryList.clear()
                        categoryList.add(Pair("All Blogs", "")) // Default for fetching all blogs

                        // Store category names and IDs
                        categoryList.addAll(blogCategories.map {
                            Pair(
                                it.categoryName,
                                it.blog_Category_id
                            )
                        })


                        blogList.clear()
                        data?.BlogData?.let { blogs ->
                            blogList.addAll(blogs)
                        }
                        showLoading(false)

                        updateRecyclerView() // ✅ Update RecyclerView with all blogs


                        //     fetchBlogsByCategory("") // Load all blogs initially
                    }
                }
                showLoading(false)

            }

            override fun onFailure(call: Call<GetAllBlogAppResponse>, t: Throwable) {
                t.printStackTrace()
                showLoading(false)
            }
        })
    }

    private fun fetchBlogsByCategory(categoryId: String) {


        if (categoryId.trim().isEmpty()) {
            fetchBlogCategories()  // Call the function to fetch blog categories
            return  // Exit the function to prevent further execution
        }

        showLoading(true)

        val apiService = RetrofitClient.client
        val token = "Bearer ${Utils.GetSession().token}"

        apiService.getBlogs(token, categoryId, pageSize, 0)
            .enqueue(object : Callback<GetAllBlogAppResponse> {
                override fun onResponse(
                    call: Call<GetAllBlogAppResponse>,
                    response: Response<GetAllBlogAppResponse>
                ) {
                    if (response.isSuccessful) {
                        val blogs = response.body()?.data?.BlogData
                        blogList.clear()
                        if (blogs != null) {
                            blogList.addAll(blogs)
                        }
                        updateRecyclerView()
                    }
                    showLoading(false)
                }

                override fun onFailure(call: Call<GetAllBlogAppResponse>, t: Throwable) {
                    t.printStackTrace()
                    showLoading(false)
                }
            })
    }


    private fun showPopup( textView: TextView, view: View, items: List<String>, onItemSelected: (String) -> Unit) {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.popup_selection, null)
        val recyclerView =
            popupView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerView)
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
        popupWindow?.showAtLocation(
            view,
            Gravity.NO_GRAVITY,
            location[0],
            location[1] + view.height
        )
    }





    private fun updateRecyclerView() {
        // Ensure currentPage is greater than 0
        if (currentPage <= 0) {
            currentPage = 1  // Set it to the first page if it's invalid
        }

        val startIndex = (currentPage - 1) * pageSize
        val endIndex = minOf(startIndex + pageSize, blogList.size)

        // Ensure startIndex < endIndex and both are valid
        if (startIndex < endIndex && startIndex < blogList.size) {
            blogAdapter.updateList(blogList.subList(startIndex, endIndex))
        } else {
            // Log or handle invalid indices
            Log.e("BlogsFragment", "Invalid sublist indices: startIndex=$startIndex, endIndex=$endIndex")
        }

        // Calculate total pages
        val totalPages = (blogList.size + pageSize - 1) / pageSize

        // If there's only one page, ensure currentPage is set to 1 and handle UI
        if (totalPages == 1) {
            currentPage = 1 // Ensure it selects the first (and only) page
        }

        // Update page number UI with the total pages and current page
        updatePageNumberUI(totalPages)

        // Enable/Disable Prev and Next based on the current page
        binding.btnPrev.isEnabled = currentPage > 1
        binding.btnNext.isEnabled = currentPage < totalPages

        // Update the colors based on the current page
        if (currentPage == 1) {
            binding.btnPrev.setTextColor(ContextCompat.getColor(requireContext(), R.color.desable))
            binding.btnPrev.isClickable = false
        } else {
            binding.btnPrev.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.btnPrev.isClickable = true
        }

        if (currentPage == totalPages) {
            binding.btnNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.desable))
            binding.btnNext.isClickable = false
        } else {
            binding.btnNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.btnNext.isClickable = true
        }
    }


    private fun updatePageNumberUI(totalPages: Int) {
        binding.pageContainer.removeAllViews()

        for (i in 1..totalPages) {
            val pageButton = TextView(requireContext()).apply {
                text = i.toString()
                textSize = 14f

                setPadding(30, 20, 30, 20) // Adjust padding for rectangle shape

                if (i == currentPage) {
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    setBackgroundResource(R.drawable.bg_selected_page)
                } else {
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    setBackgroundResource(R.drawable.bg_unselected_page)
                }
                typeface = ResourcesCompat.getFont(requireContext(), R.font.gilroy_semibold)
                setOnClickListener {
                    currentPage = i
                    updateRecyclerView()
                }

                // Set margins for gap between page buttons
                val layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {

                    setMargins(10, 0, 10, 0)
                }
                layoutParams.gravity =
                    Gravity.CENTER_VERTICAL // Optional: to align buttons vertically centered
                this.layoutParams = layoutParams
            }
            binding.pageContainer.addView(pageButton)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.llContainer.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.llContainer.visibility = View.VISIBLE
        }
    }


    private fun showNoInternetMessage() {
        binding.progressBar.visibility = View.GONE
        binding.llContainer.visibility = View.GONE
        binding.noInternetText.visibility =
            View.VISIBLE // Ensure this TextView exists in your layout
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        popupWindow?.dismiss()

    }
}