package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.databinding.FragmentDeleteAccountBinding


class DeleteAccountFragment : Fragment() {

     private lateinit var binding : FragmentDeleteAccountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

         binding = FragmentDeleteAccountBinding.inflate(inflater,container,false)


         binding.lbDeleteAccount.setOnClickListener {

             findNavController().navigate(R.id.DeleteAccountOtpVerificationFragment)

         }

         return binding.root
    }


}