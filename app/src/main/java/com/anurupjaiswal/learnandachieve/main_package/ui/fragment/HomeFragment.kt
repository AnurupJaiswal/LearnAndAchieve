package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.database.User
import com.anurupjaiswal.learnandachieve.basic.database.UserDataHelper
import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.AppController
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Constants
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
import com.anurupjaiswal.learnandachieve.databinding.FragmentHomeBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.HomeViewPagerAdapter
import com.anurupjaiswal.learnandachieve.main_package.adapter.PopularPackagesAdapter
import com.anurupjaiswal.learnandachieve.main_package.adapter.PurchasePackageAdapter
import com.anurupjaiswal.learnandachieve.main_package.adapter.StudyMaterialAdapter
import com.anurupjaiswal.learnandachieve.model.GetAllBlogAppResponse
import com.anurupjaiswal.learnandachieve.model.GetAllStudyMaterial
import com.anurupjaiswal.learnandachieve.model.GetUserResponse
import com.anurupjaiswal.learnandachieve.model.HomeViewpagerIteam
import com.anurupjaiswal.learnandachieve.model.PackageResponse
import com.anurupjaiswal.learnandachieve.model.PopularPackageItem
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val handler = Handler(Looper.getMainLooper())
    private var currentCardIndex = 0
    private lateinit var gestureDetector: GestureDetector
    private var isAutoScrollingPaused = false // Flag to check if auto-scroll is paused
    private var apiservice: ApiService? = null
    var token: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setUpShopNowViewpager()

        binding.tvViewAllBlogs.setOnClickListener {
            NavigationManager.navigateToFragment(findNavController(), R.id.BlogsFragment)
        }


        binding.tvViewAllPackages.setOnClickListener {
            NavigationManager.navigateToFragment(findNavController(), R.id.PurchasePackage)
        }


        val cards = listOf(
            binding.card1, binding.card2, binding.card3, binding.card4
        )

        setupGestureDetector(cards)
        binding.horizontalScrollView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    pauseAutoScroll()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Resume the auto-scroll when the user releases the screen
                    resumeAutoScroll(cards)
                }
            }
            gestureDetector.onTouchEvent(event)
            true
        }

        observeLayoutChanges(cards)

    }


    fun init() {

        token = "Bearer ${Utils.GetSession().token} "
        binding.rcvStudyMaterial.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        apiservice = RetrofitClient.client
        binding.mostPopularPackageRecyclerView.layoutManager = LinearLayoutManager(
            context, // Context
            LinearLayoutManager.HORIZONTAL,
            false
        )
        if ((requireActivity().application as AppController).isOnline) {
            showLoading(true)
            binding.llBlogContainer.visibility = View.VISIBLE

            fetchStudyMaterials(token!!)
            getPackages(token!!)

            getAllBlogData(token!!)
            getUserDetails(token!!)
             binding.llHallTicket.setOnClickListener {
                 NavigationManager.navigateToFragment(
                     findNavController(), R.id.BharatSatExamTicketFragment)
             }
        } else {
            
            Utils.T(requireContext(), "Please Check Internet Connection!")
            binding.llBlogContainer.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
        }


    }

    private fun setUpShopNowViewpager() {
        // Prepare the data for ViewPager
        val homeViewpagerItems = arrayListOf<HomeViewpagerIteam>().apply {

            add(HomeViewpagerIteam(R.drawable.banner))
            add(HomeViewpagerIteam(R.drawable.banner_two))
            add(HomeViewpagerIteam(R.drawable.banner_three))

        }

        // Set adapter for ViewPager
        binding.viewPagerImageSlider.adapter =
            HomeViewPagerAdapter(requireContext(), homeViewpagerItems, binding.viewPagerImageSlider)

        // ViewPager configurations
        binding.viewPagerImageSlider.apply {
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
        }

        // Set page transformer for animations
        val compositePageTransformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(40))
            addTransformer { page, position ->
                val scale = 1 - abs(position)
//                page.scaleY = 0.85f + scale * 0.15f
            }
        }

        binding.viewPagerImageSlider.setPageTransformer(compositePageTransformer)

        // Setup dots for the pager
        setupDots(homeViewpagerItems.size)
        updateDotIndicator(0)

        // Handle page change callback
        binding.viewPagerImageSlider.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

                updateDotIndicator(position)
            }
        })
    }


    private fun setupDots(numOfDots: Int) {
        binding.dotsContainer.removeAllViews()
        repeat(numOfDots) {
            val dot = ImageView(context).apply {
                // Set the inactive dot background (circle shape) initially
                setBackgroundResource(R.drawable.inactive_dot)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 14
                }
            }
            binding.dotsContainer.addView(dot)
        }
    }

    private fun updateDotIndicator(position: Int) {
        // Check if dotsContainer is properly initialized
        if (binding.dotsContainer.childCount > 0) {
            // Loop through all dots and set them to inactive
            for (i in 0 until binding.dotsContainer.childCount) {
                val dot = binding.dotsContainer.getChildAt(i) as ImageView
                // Set all dots to inactive by default (circle)
                dot.setBackgroundResource(R.drawable.homeinactive_dot)

                // Reset the size for inactive dots (if needed)
                dot.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 14
                }
            }

            // Set the active dot (rectangle) for the current position
            val activeDot = binding.dotsContainer.getChildAt(position) as ImageView
            activeDot.setBackgroundResource(R.drawable.active_dot)

            // Convert 8 dp to pixels
            val heightInPx = (8 * resources.displayMetrics.density).toInt()

            // Set the new height for the active dot
            val params = activeDot.layoutParams as LinearLayout.LayoutParams
            params.height = heightInPx
            activeDot.layoutParams = params

        }
    }


    private fun getPackages(authToken: String) {

        val userId = Utils.GetSession()._id
        E("token $token")
        E("userId $userId")


        apiservice?.getPackages(authToken, limit = 10, offset = 0)
            ?.enqueue(object : retrofit2.Callback<PackageResponse> {
                override fun onResponse(
                    call: Call<PackageResponse>,
                    response: Response<PackageResponse>
                ) {
                    try {
                        when (response.code()) {
                            StatusCodeConstant.OK -> {
                                val packageResponse = response.body()
                                if (packageResponse != null) {
                                    val packageList = packageResponse.packages
                                    val adapter = PopularPackagesAdapter(
                                        requireContext(),
                                        packageList,
                                        authToken,
                                        onPackageDetailsClick = { packageId, token ->
                                            navigateToPackageDetails(packageId, token)
                                        }
                                    )
                                    binding.mostPopularPackageRecyclerView.adapter = adapter
                                    adapter.notifyDataSetChanged()  // Notify that the data has been updated
                                }
                            }
                            StatusCodeConstant.BAD_REQUEST -> {
                                val errorBody = response.errorBody()?.string()
                                var errorMessage = "Unknown error"
                                if (!errorBody.isNullOrEmpty()) {
                                    try {
                                        val gson = Gson()
                                        val apiError = gson.fromJson(errorBody, APIError::class.java)
                                        errorMessage = apiError.message ?: apiError.error ?: "Unknown error"
                                    } catch (e: Exception) {
                                        errorMessage = errorBody
                                    }
                                }
                             //   Utils.T(requireContext(), "Response: $errorMessage")
                                E("GetPackages: Bad Request Error: $errorMessage")
                            }
                            StatusCodeConstant.UNAUTHORIZED -> {
                                // Call the unauthorized token handling function
                                Utils.UnAuthorizationToken(requireContext())
                            }
                            else -> {
                                val errorBody = response.errorBody()?.string()
                                var errorMessage = "Unknown error"
                                if (!errorBody.isNullOrEmpty()) {
                                    try {
                                        val gson = Gson()
                                        val apiError = gson.fromJson(errorBody, APIError::class.java)
                                        errorMessage = apiError.message ?: apiError.error ?: "Unknown error"
                                    } catch (e: Exception) {
                                        errorMessage = errorBody
                                    }
                                }
                    //            Utils.T(requireContext(), "Response: $errorMessage")
                                E("GetPackages: Error: $errorMessage")
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<PackageResponse>, t: Throwable) {
                    call.cancel()
                    t.printStackTrace()
                //    Utils.T(requireContext(), "Response: ${t.message}")
                    E("GetPackages: Error: ${t.message}")
                }
            })


    }


    private fun observeLayoutChanges(cards: List<CardView>) {
        val cardHeights = mutableListOf<Float>()

        binding.horizontalScrollView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.horizontalScrollView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                cards.forEach { card ->
                    cardHeights.add(card.height.toFloat())
                }

                if (cardHeights.size == cards.size) {
                    val calculatedHeights = cardHeights.map { cardHeights.last() - it }

                    animateImageTranslationY(binding.boyimage, calculatedHeights[0])
                    startAutoScroll(cards, calculatedHeights)
                }
            }
        })
    }

    private fun setupGestureDetector(cards: List<CardView>) {
        gestureDetector =
            GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
                override fun onFling(
                    e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float
                ): Boolean {
                    if (e1 == null || e2 == null) return false

                    val deltaX = e2.x - e1.x
                    if (deltaX > 100) {
                        // Swipe right
                        if (currentCardIndex > 0) {
                            currentCardIndex = (currentCardIndex - 1) % cards.size
                            // Perform animations for the current card
                            performManualScroll(cards, currentCardIndex)
                        }
                    } else if (deltaX < -100) {
                        // Swipe left
                        if (currentCardIndex < cards.size - 1) {
                            currentCardIndex = (currentCardIndex + 1) % cards.size
                            // Perform animations for the current card
                            performManualScroll(cards, currentCardIndex)
                        }
                    }
                    return true
                }
            })
    }

    private fun scrollToCard(cards: List<CardView>, cardIndex: Int) {


        binding.horizontalScrollView.smoothScrollTo(cards[cardIndex].left, 0)

    }

    private fun performManualScroll(cards: List<CardView>, cardIndex: Int) {
        val translations = calculateCardTranslations(cards)
        val targetTranslationY = translations[cardIndex]

        // Animate the image translation
        animateImageTranslationY(binding.boyimage, targetTranslationY)

        // Scroll to the target card
        scrollToCard(cards, cardIndex)

        // Trigger Cup Animation based on the card index
        if (cardIndex == 3) {
            showCupImage()
        } else {
            hideCupImage()
        }

        // Restart auto-scroll after a manual scroll
        handler.removeCallbacksAndMessages(null)
        startAutoScroll(cards, translations)
    }

    private fun calculateCardTranslations(cards: List<CardView>): List<Float> {
        val cardHeights = cards.map { it.height.toFloat() }
        return cardHeights.map { cardHeights.last() - it }
    }

    private fun startAutoScroll(cards: List<CardView>, translations: List<Float>) {
        if (isAutoScrollingPaused) return // Don't start auto-scroll if it's paused

        handler.postDelayed(object : Runnable {
            override fun run() {
                if (currentCardIndex >= cards.size) currentCardIndex = 0

                val nextCardIndex = (currentCardIndex + 1) % cards.size
                animateImageTranslationY(binding.boyimage, translations[nextCardIndex])

                if (nextCardIndex == 3) {
                    showCupImage()
                } else {
                    hideCupImage()
                }

                handler.postDelayed({
                    scrollToCard(cards, nextCardIndex)
                }, 200)

                currentCardIndex++
                handler.postDelayed(this, 3000)
            }
        }, 3000)
    }

    private fun animateImageTranslationY(image: ImageView, targetTranslationY: Float) {
        val animator =
            ObjectAnimator.ofFloat(image, "translationY", image.translationY, targetTranslationY)
        animator.duration = 300
        animator.start()
    }

    //show Cup Image
    private fun showCupImage() {
        binding.cupimage.alpha = 1f
        binding.cupimage.translationY = 200f

        val animator = ObjectAnimator.ofFloat(binding.cupimage, "translationY", 200f, 0f)
        animator.duration = 500
        animator.start()
    }

    //hide Cup Image
    private fun hideCupImage() {
        binding.cupimage.alpha = 0f
        binding.cupimage.translationY = 200f
    }

    // Pause auto-scroll
    private fun pauseAutoScroll() {
        isAutoScrollingPaused = true
        handler.removeCallbacksAndMessages(null) // Stop current auto-scroll
    }

    // Resume auto-scroll
    private fun resumeAutoScroll(cards: List<CardView>) {
        isAutoScrollingPaused = false
        startAutoScroll(
            cards, calculateCardTranslations(cards)
        ) // Restart auto-scroll from current position
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)

        _binding = null // Nullify binding to prevent memory leaks
    }

    fun navigateToPackageDetails(packageId: String, token: String) {
        val bundle = Bundle().apply {
            putString("packageId", packageId)
            putString("token", token)
        }

        NavigationManager.navigateToFragment(
            findNavController(),
            R.id.packageDetailsFragment,
            bundle
        )
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


    private fun fetchStudyMaterials(authToken: String) {

        apiservice?.getStudyMaterials(
            limit = 10, offset = 0, authToken
        )?.enqueue(object : Callback<GetAllStudyMaterial> {
            override fun onResponse(
                call: Call<GetAllStudyMaterial>,
                response: Response<GetAllStudyMaterial>
            ) {
                try {
                    if (response.code() == StatusCodeConstant.OK) { // Explicitly check for OK status
                        val studyMaterialsResponse = response.body()

                        studyMaterialsResponse?.data?.let { studyMaterials ->
                            val adapter =
                                StudyMaterialAdapter(studyMaterials) { subjectId, medium, subjectName ->

                                    navigateTOModule(subjectId, medium, subjectName)
                                }

                            binding.rcvStudyMaterial.adapter = adapter
                        }
                    } else {
                        handleStudyMaterialsApiError(response)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showLoading(true)

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
        showLoading(false)

        when (response.code()) {

            StatusCodeConstant.UNAUTHORIZED -> {
                response.errorBody()?.let { errorBody ->
                    val message = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                    message.error?.let { E(it) }
                    Utils.UnAuthorizationToken(requireContext())
                }
            }

            else -> {
                Utils.T(context, "Unknown error occurred.")
            }
        }
    }

    fun getAllBlogData(authToken: String) {


        apiservice?.getAllBlogApp(authToken)?.enqueue(object : Callback<GetAllBlogAppResponse> {
            override fun onResponse(
                call: Call<GetAllBlogAppResponse>,
                response: Response<GetAllBlogAppResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val blogDataList = response.body()?.data?.BlogData
                    val blogCategoryDataList =
                        response.body()?.data?.blogCategoryData // This is the list of categories

                    binding.llBlogContainer.visibility = View.VISIBLE
                    if (!blogDataList.isNullOrEmpty()) {

                        // Function to get category name by matching blog_category_id
                        fun getCategoryNameById(blogCategoryId: String): String? {
                            return blogCategoryDataList?.find { it.blog_Category_id == blogCategoryId }?.categoryName
                        }

                        // Handling blogDataList
                        if (blogDataList.size > 0) {
                            binding.tvBlogTitle1.text = blogDataList[0].title

                            // Get category name for blog 1
                            val categoryName1 =
                                getCategoryNameById(blogDataList[0].blog_category_id)

                            binding.cvBlogCard1.setOnClickListener {
                                // Pass both blog_id and categoryName to navigateToBlogDetails
                                navigateToBlogDetails(blogDataList[0].blog_id, categoryName1)
                            }
                        }
                        if (blogDataList.size > 1) {
                            binding.tvBlogTitle2.text = blogDataList[1].title

                            // Get category name for blog 2
                            val categoryName2 =
                                getCategoryNameById(blogDataList[1].blog_category_id)

                            binding.cvBlogCard2.setOnClickListener {
                                // Pass both blog_id and categoryName to navigateToBlogDetails
                                navigateToBlogDetails(blogDataList[1].blog_id, categoryName2)
                            }
                        }
                        if (blogDataList.size > 2) {
                            binding.tvBlogTitle3.text = blogDataList[2].title

                            // Get category name for blog 3
                            val categoryName3 =
                                getCategoryNameById(blogDataList[2].blog_category_id)

                            binding.cvBlogCard3.setOnClickListener {
                                // Pass both blog_id and categoryName to navigateToBlogDetails
                                navigateToBlogDetails(blogDataList[2].blog_id, categoryName3)
                            }
                        }
                        if (blogDataList.size > 3) {
                            binding.tvBlogTitle4.text = blogDataList[3].title

                            val categoryName4 =
                                getCategoryNameById(blogDataList[3].blog_category_id)
                            binding.cvBlogCard4.setOnClickListener {
                                // Pass both blog_id and categoryName to navigateToBlogDetails
                                navigateToBlogDetails(blogDataList[3].blog_id, categoryName4)
                            }
                            showLoading(false)

                        }
                    } else {
                        showLoading(false)

                        Log.e("HomeFragment", "Blog data list is empty or null")
                    }
                } else {
                    showLoading(false)

                    E("Response failed or body is null")
                }
            }

            override fun onFailure(call: Call<GetAllBlogAppResponse>, t: Throwable) {
                showLoading(false)
                t.message?.let { E(it) }
            }
        })
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

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.llHomeContainer.visibility = View.INVISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.llHomeContainer.visibility = View.VISIBLE
        }
    }

    private fun getUserDetails(authToken: String) {

        apiservice?.getUserDetails(authToken)?.enqueue(object : Callback<GetUserResponse> {
            override fun onResponse(
                call: Call<GetUserResponse>,
                response: Response<GetUserResponse>
            ) {
                try {
                    if (response.code() == StatusCodeConstant.OK) {
                        val userModel = response.body()

                        if (userModel?.user != null) {
                            val getUser = userModel.user

                            E("User ID: ${getUser.user_id}")
                            E("Full API Response: ${response.body()}")

                            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()
                            )

                            val examDate = getUser.bharatSatExamDate?.substring(0, 10)
                            if (examDate != null) {
                                    binding.llHallTicket.visibility = View.VISIBLE
                                } else {
                                    binding.llHallTicket.visibility = View.GONE
                                }

                        } else {

                            E("Error: 'getUser' is null in response.")
                        }

                    } else {
                        handleGetUserResponseApiError(response)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Utils.T(requireContext(), "Error processing the request.")
                }
            }

            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                call.cancel()
                // Hide loading state
                t.printStackTrace()
                Utils.T(requireContext(), t.message ?: "Request failed. Please try again later.")
            }
        })


    }

    private fun handleGetUserResponseApiError(response: Response<GetUserResponse>) {
        when (response.code()) {
            StatusCodeConstant.BAD_REQUEST -> {
                response.errorBody()?.let { errorBody ->
                    val message = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                    val displayMessage = message.error ?: "Invalid Request"
                    Utils.T(requireContext(), displayMessage)
                }
            }

            StatusCodeConstant.UNAUTHORIZED -> {
                response.errorBody()?.let { errorBody ->
                    val message = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                    val displayMessage = message.message ?: "Unauthorized Access"
                    Utils.T(requireContext(), displayMessage)
                    Utils.UnAuthorizationToken(requireContext())
                }
            }

            StatusCodeConstant.NOT_FOUND -> {
                response.errorBody()?.let { errorBody ->
                    val message = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                    val displayMessage = message.message ?: "Package not found. Please try again."
                    Utils.T(requireContext(), displayMessage)
                }
            }
            else -> {
                Utils.T(requireContext(), "Unknown error occurred.")

            }
        }
    }
}
