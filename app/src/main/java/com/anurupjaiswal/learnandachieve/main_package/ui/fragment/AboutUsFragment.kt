package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.databinding.FragmentAboutUsBinding


class AboutUsFragment : Fragment() {

     private  lateinit var  binding: FragmentAboutUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAboutUsBinding.inflate(inflater, container, false)
        return  binding.root
    }

}