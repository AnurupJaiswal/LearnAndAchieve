package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.model.Subject


class SubjectsAdapter(
    private val context: Context,
    private val subjectList: List<Subject>,
    private val onSubjectClick: (Subject) -> Unit
) : RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_study_subject, parent, false)
        return SubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = subjectList[position]
        holder.subjectName.text = subject.name
        subject.iconResId?.let { holder.subjectIcon.setImageResource(it) }
        holder.itemView.setOnClickListener { onSubjectClick(subject) }
    }

    override fun getItemCount(): Int = subjectList.size

    class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subjectName: TextView = itemView.findViewById(R.id.subjectName)
        val subjectIcon: ImageView = itemView.findViewById(R.id.subjectIcon)
    }
}

