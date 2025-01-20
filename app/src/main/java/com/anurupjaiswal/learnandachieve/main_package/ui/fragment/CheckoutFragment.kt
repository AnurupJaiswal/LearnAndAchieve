package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.databinding.FragmentCheckoutBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.CartAdapter
import com.anurupjaiswal.learnandachieve.model.PackageModel

class CheckoutFragment : Fragment() {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val text = "By completing your purchase you agree to these Terms of Service."
        val spannableString = SpannableString(text)

        // Custom ClickableSpan to remove underline
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(requireContext(), "Terms of Service Clicked", Toast.LENGTH_SHORT).show()
                // Navigate to Terms of Service page or perform action here
            }

            override fun updateDrawState(ds: android.text.TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false // Remove underline
                ds.color = ContextCompat.getColor(requireContext(), R.color.blue) // Set blue color
            }
        }

        val startIndex = text.indexOf("Terms of Service")
        val endIndex = startIndex + "Terms of Service".length
        val boldSpan = StyleSpan(Typeface.BOLD)

        // Apply ClickableSpan
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(boldSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.textTerms.text = spannableString
        binding.textTerms.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }





}
