package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.databinding.ItemStudySubjectBinding
import com.anurupjaiswal.learnandachieve.model.StudyMaterial


class SubjectsAdapter(
    private val StudyMaterial: List<StudyMaterial>,
    private val onItemClick: (subjectId:String,medium:String,subjectName:String) -> Unit // Callback to pass the subject_id

) : RecyclerView.Adapter<SubjectsAdapter.SubjectsViewHolder>() {

    class SubjectsViewHolder(private val binding: ItemStudySubjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(Subjects: StudyMaterial,onItemClick: (String,String,String) -> Unit) {
            binding.subjectName.text = Subjects.subject_name
            Utils.Picasso(Subjects.subject_image,binding.subjectIcon,R.drawable.popup_background_with_shadow)

            binding.root.setOnClickListener {
                // Invoke the callback passing the subject_id
                onItemClick(Subjects.subject_id,Subjects.medium,Subjects.subject_name)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectsViewHolder {
        val binding = ItemStudySubjectBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SubjectsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubjectsViewHolder, position: Int) {
        holder.bind(StudyMaterial[position], onItemClick)
    }

    override fun getItemCount(): Int = StudyMaterial.size
}
