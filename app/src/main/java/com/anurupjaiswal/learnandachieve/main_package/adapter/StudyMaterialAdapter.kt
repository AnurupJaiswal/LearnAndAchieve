package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.databinding.ItemStudyMaterialsBinding
import com.bumptech.glide.Glide
import com.anurupjaiswal.learnandachieve.model.StudyMaterial


class StudyMaterialAdapter(
    private val studyMaterials: List<StudyMaterial>,
    private val onItemClick: (subjectId:String,medium:String,subjectName:String) -> Unit // Callback to pass the subject_id

) : RecyclerView.Adapter<StudyMaterialAdapter.StudyMaterialViewHolder>() {

    class StudyMaterialViewHolder(private val binding: ItemStudyMaterialsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(studyMaterial: StudyMaterial,onItemClick: (String,String,String) -> Unit) {
            binding.subjectName.text = studyMaterial.subject_name
            Utils.Picasso(studyMaterial.subject_image,binding.subjectIcon,R.drawable.popup_background_with_shadow)

            binding.root.setOnClickListener {
                // Invoke the callback passing the subject_id
                onItemClick(studyMaterial.subject_id,studyMaterial.medium,studyMaterial.subject_name)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyMaterialViewHolder {
        val binding = ItemStudyMaterialsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return StudyMaterialViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudyMaterialViewHolder, position: Int) {
        holder.bind(studyMaterials[position], onItemClick)
    }

    override fun getItemCount(): Int = studyMaterials.size
}
