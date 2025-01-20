package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.RotateAnimation
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.databinding.FragmentBharatSatScholarShipBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.ExpandableCardAdapter
import com.anurupjaiswal.learnandachieve.main_package.adapter.RankPrizeAdapter
import com.anurupjaiswal.learnandachieve.main_package.adapter.StandardSelectionAdapter
import com.anurupjaiswal.learnandachieve.model.CardItem
import com.anurupjaiswal.learnandachieve.model.RankPrize

class BharatSatScholarShipFragment : Fragment() {

    private var _binding: FragmentBharatSatScholarShipBinding? = null
    private val binding get() = _binding!!

    private var isRecyclerViewVisible = false

    private val rankPrizeListMap = mapOf(
        "Standard 10" to Pair(
            listOf(
                RankPrize("Rank 1", "₹ 50,000"),
                RankPrize("Rank 2", "₹ 35,000"),
                RankPrize("Rank 3", "₹ 30,000"),
                RankPrize("Rank 4", "₹ 25,000")
            ), listOf(
                RankPrize("Rank 1", "₹ 10,000"),
                RankPrize("Rank 2", "₹ 8,000")
            )
        ), "Standard 9" to Pair(
            listOf(
                RankPrize("Rank 1", "₹ 40,000"),
                RankPrize("Rank 2", "₹ 25,000")
            ), listOf(
                RankPrize("Rank 3", "₹ 7,000"),
                RankPrize("Rank 4", "₹ 5,000")
            )
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBharatSatScholarShipBinding.inflate(inflater, container, false)
        val view = binding.root

        initializeUI()
        setupStandardSelection()
        setupScholarshipDistribution()

        return view
    }

    private fun initializeUI() {
        // Set default data for "Standard 10"
        val defaultStandard = "Standard 10"
        binding.tvStandardSelection.text = defaultStandard
        updateRankAndTalukaAdapters(defaultStandard)

        // Toggle the visibility of the standard selection dropdown
        binding.tvStandardSelection.setOnClickListener {
            toggleStandardSelectionVisibility()
        }
    }

    private fun setupStandardSelection() {
        val standardList = listOf("Standard 10", "Standard 9", "Standard 8", "Standard 7")

        val standardAdapter = StandardSelectionAdapter(standardList, object : StandardSelectionAdapter.OnStandardSelectedListener {
            override fun onStandardSelected(standardName: String) {
                binding.tvStandardSelection.text = standardName
                updateRankAndTalukaAdapters(standardName)
                hideStandardSelectionWithAnimation()
            }
        })

        binding.RcvSelectStandard.layoutManager = LinearLayoutManager(context)
        binding.RcvSelectStandard.adapter = standardAdapter
    }

    private fun setupScholarshipDistribution() {
        val cardItems = listOf(
            CardItem(
                "District-Level Recognition",
                "In each of the 300 districts, we celebrate the top 10 achievers across grades 5 to 10 with prestigious Scholarships.",
                Color.parseColor("#D6F6FF")
            ),
            CardItem(
                "Nationwide Commitment",
                "Exceptional scholars in every district receive significant grants. Total grant amounts to a remarkable sum!",
                Color.parseColor("#D6F6FF")
            ),
            CardItem(
                "Nurturing Excellence",
                "At Learn and Achieve Edutech, we believe in rewarding your performance and dedication.",
                Color.parseColor("#FFE8E8")
            ),
            CardItem(
                "Join Us, Embrace Opportunity",
                "We welcome candidates passionate about education and committed to student success.",
                Color.parseColor("#DCFFD3")
            )
        )

        binding.rcvScholarshipDistribution.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ExpandableCardAdapter(cardItems)
        }
    }

    private fun updateRankAndTalukaAdapters(standardName: String) {
        val (rankPrizes, talukaLevels) = rankPrizeListMap[standardName] ?: Pair(emptyList(), emptyList())

        // Update Rank RecyclerView
        binding.rcvRank.layoutManager = GridLayoutManager(context, 2)
        binding.rcvRank.adapter = RankPrizeAdapter(rankPrizes)

        // Update Taluka-Level RecyclerView
        binding.rcvTaluka.layoutManager = GridLayoutManager(context, 2)
        binding.rcvTaluka.adapter = RankPrizeAdapter(talukaLevels)
    }

    private fun toggleStandardSelectionVisibility() {
        if (isRecyclerViewVisible) {
            hideStandardSelectionWithAnimation()
        } else {
            showStandardSelectionWithAnimation()
        }
    }

    private fun showStandardSelectionWithAnimation() {
        binding.McvSelectStandard.visibility = View.VISIBLE
        binding.McvSelectStandard.alpha = 0f
        binding.McvSelectStandard.animate().alpha(1f).setDuration(300)
        rotateImageView(180f)
        isRecyclerViewVisible = true
    }

    private fun hideStandardSelectionWithAnimation() {
        binding.McvSelectStandard.animate().alpha(0f).setDuration(300).withEndAction {
            binding.McvSelectStandard.visibility = View.GONE
            rotateImageView(0f)
        }
        isRecyclerViewVisible = false
    }

    private fun rotateImageView(rotation: Float) {
        val rotate = RotateAnimation(
            binding.ivExpandCollapse.rotation,
            rotation,
            RotateAnimation.RELATIVE_TO_SELF,
            0.5f,
            RotateAnimation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 300
        rotate.fillAfter = true
        binding.ivExpandCollapse.startAnimation(rotate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
