package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.databinding.FragmentBlogDetailsBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.RelatedBlogPagerAdapter
import com.anurupjaiswal.learnandachieve.model.BlogResponse
import com.anurupjaiswal.learnandachieve.model.DeailsBlogCategoryData
import com.anurupjaiswal.learnandachieve.model.RelatedBlog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BlogDetailsFragment : Fragment() {

    private var _binding: FragmentBlogDetailsBinding? = null
    private val binding get() = _binding!!
    private var blogId: String? = null
    private var categoryName: String? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            blogId = it.getString("blog_id")
            categoryName = it.getString("categoryName")
        }
        binding.viewPagerRelatedBlogs.isUserInputEnabled = false


        blogId?.let {
            getBlogDetails(it)
        }
        // Use the 'blogId' variable wherever necessary in this fragment
        Log.d("BlogDetailsFragment", "Received Blog ID: $blogId")

        binding.tvBlogType.text = categoryName
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBlogDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }


    private fun getBlogDetails(blogId: String) {

        binding.progressBar.visibility = View.VISIBLE
        binding.nestedScrollView.visibility = View.GONE

        RetrofitClient.client.getBlogDetails(blogId).enqueue(object : Callback<BlogResponse> {
            override fun onResponse(call: Call<BlogResponse>, response: Response<BlogResponse>) {
                if (response.isSuccessful) {
                    val blogData = response.body()?.data?.blogCategoryData
                    blogData?.let {
                        // Set Title
                        binding.tvTitle.text = it.title

                        // Set Author
                        binding.tvAuthor.text = it.name

                        // Format the date using the Utils.formatDate function
                        val formattedDate = Utils.formatDate(it.date)
                        binding.tvDate.text = formattedDate
                        loadBlogDetails(blogData)
                        loadRelatedBlogs(response.body()!!.data.relatedBlogs) //

                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to load blog details",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<BlogResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun loadBlogDetails(blogData: DeailsBlogCategoryData) {
        // Extract the HTML content (from the details field)
        val blogDetailsHtml = blogData.details

        // Apply custom CSS for styling if needed
        val customCSS = """
        <style>
            @import url('https://fonts.googleapis.com/css2?family=Gilroy:wght@400;700&display=swap');

            body {
        font-family: 'Gilroy', sans-serif;
                font-size: 14px;
                line-height: 1.5;
              
            }

            h2 {
                color: #2C3E50;
            }

            p {
                font-size: 14px;
                line-height: 1.5;
                color: #555;
            }

          a {
  color: orange;
}
               img {
                width: 100% !important;
                height: 300px !important;
                object-fit: cover;
                border-radius: 10px;
             }

            /* Video styling if included */
            video {
                width: 50%;
                height: auto;
                display: block;
            }
            
             * {
            -webkit-user-select: none; /* Safari */
            -moz-user-select: none; /* Firefox */
            -ms-user-select: none; /* Internet Explorer/Edge */
            user-select: none; /* Standard */
        }
            
        </style>
    """

        // Combine the CSS and HTML content
        val fullHtmlContent = customCSS + blogDetailsHtml

        // Load the HTML content into WebView
        binding.webView.apply {

            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            settings.javaScriptEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false // Allow autoplay for videos
            loadDataWithBaseURL(null, fullHtmlContent, "text/html", "UTF-8", null)
        }

        binding.progressBar.visibility = View.GONE
        binding.nestedScrollView.visibility = View.VISIBLE
    }

    private fun loadRelatedBlogs(relatedBlogs: List<RelatedBlog>) {
        val pagerAdapter = RelatedBlogPagerAdapter(relatedBlogs) { selectedBlog ->

            blogId =  selectedBlog.blog_id

            Utils.E(blogId!!)
            getBlogDetails(selectedBlog.blog_id)
        }

        binding.viewPagerRelatedBlogs.apply {
            adapter = pagerAdapter
            visibility = View.VISIBLE
            offscreenPageLimit = 3
            post {
                setupDotsIndicator(relatedBlogs.size, 0)
                updateButtonState(0, relatedBlogs.size)

            }
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    setupDotsIndicator(relatedBlogs.size, position)
                    updateButtonState(position, relatedBlogs.size)

                }
            })

            binding.mcvPrevious.setOnClickListener {
                val currentPos = binding.viewPagerRelatedBlogs.currentItem
                if (currentPos > 0) binding.viewPagerRelatedBlogs.currentItem = currentPos - 1
            }

            // Next Button Click
            binding.mcvNext.setOnClickListener {
                val currentPos = binding.viewPagerRelatedBlogs.currentItem
                if (currentPos < relatedBlogs.size - 1) binding.viewPagerRelatedBlogs.currentItem = currentPos + 1
            }
        }


    }
    private fun setupDotsIndicator(size: Int, currentPosition: Int) {
        binding.dotsContainer.removeAllViews() // Clear previous dots

        for (i in 0 until size) {
            val dot = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(25, 25).apply {
                    marginEnd = 20 // You can change this value to control the space between the dots
                    if (i != size - 1) {
                        marginEnd = 20 // Set the margin for all but the last dot
                    }
                }
                setBackgroundResource(
                    if (i == currentPosition) R.drawable.circle_background else R.drawable.homeinactive_dot
                )
            }
            binding.dotsContainer.addView(dot)
        }

    }

    private fun updateButtonState(currentPos: Int, totalItems: Int) {
        binding.mcvPrevious.isEnabled = currentPos > 0
        binding.mcvNext.isEnabled = currentPos < totalItems - 1

        val disabledColor = ContextCompat.getColor(requireContext(), R.color.gray)
        val enabledColor = ContextCompat.getColor(requireContext(), R.color.primaryColor)

        binding.mcvPrevious.setCardBackgroundColor(if (currentPos > 0) enabledColor else disabledColor)
        binding.mcvNext.setCardBackgroundColor(if (currentPos < totalItems - 1) enabledColor else disabledColor)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


