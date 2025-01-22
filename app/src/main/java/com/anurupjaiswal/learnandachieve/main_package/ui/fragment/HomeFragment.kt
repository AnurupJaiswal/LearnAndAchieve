package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
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
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.databinding.FragmentHomeBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.HomeViewPagerAdapter
import com.anurupjaiswal.learnandachieve.main_package.adapter.PopularPackagesAdapter
import com.anurupjaiswal.learnandachieve.model.HomeViewpagerIteam
import com.anurupjaiswal.learnandachieve.model.PopularPackageItem
import kotlin.math.abs

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val handler = Handler(Looper.getMainLooper())
    private var currentCardIndex = 0
    private lateinit var gestureDetector: GestureDetector
    private var isAutoScrollingPaused = false // Flag to check if auto-scroll is paused

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setUpShopNowViewpager()
        setUpMostPopularPackageRecyclerView()

        binding.tvViewAllBlogs.setOnClickListener {
            NavigationManager.navigateToFragment(findNavController(), R.id.BlogsFragment)
        }


        binding.tvViewAllPackages.setOnClickListener {
            NavigationManager.navigateToFragment(findNavController(), R.id.PurchasePackage)
        }


        return binding.root
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val cards = listOf(
            binding.card1, binding.card2, binding.card3, binding.card4
        )

        setupGestureDetector(cards)
        binding.horizontalScrollView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Pause the auto-scroll when the user presses/holds the screen
                    pauseAutoScroll()
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Resume the auto-scroll when the user releases the screen
                    resumeAutoScroll(cards)
                }
            }
            // Handle the swipe gestures
            gestureDetector.onTouchEvent(event)
            true
        }

        observeLayoutChanges(cards)

    }

    private fun setUpShopNowViewpager() {
        // Prepare the data for ViewPager
        val homeViewpagerItems = arrayListOf<HomeViewpagerIteam>().apply {
            repeat(3) {
                add(HomeViewpagerIteam(R.drawable.banner))
            }
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

    private fun setUpMostPopularPackageRecyclerView() {

        val popularPackages = listOf(
            PopularPackageItem(
                title = "All The Skills You Need In One Place and need to  testing string to test only the data that was coming is rioght or not  w ",
                description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
                discountedPrice = "₹299",
                originalPrice = "₹599",
                imageUrl = "https://example.com/image1.jpg",
                showPopular = true
            ), PopularPackageItem(
                title = "Skill Building Bundle",
                description = "Enhance your skills with our premium package.",
                discountedPrice = "₹599",
                originalPrice = "₹999",
                imageUrl = "https://example.com/image2.jpg"
            ), PopularPackageItem(
                title = "Complete Learning Pack",
                description = "Get access to all our resources in one pack.",
                discountedPrice = "₹499",
                originalPrice = "₹799",
                imageUrl = "https://example.com/image3.jpg"
            )
        )







        binding.mostPopularPackageRecyclerView.apply {
            adapter = PopularPackagesAdapter(popularPackages)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
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


}
