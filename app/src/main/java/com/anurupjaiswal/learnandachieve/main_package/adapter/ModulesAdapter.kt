package com.anurupjaiswal.learnandachieve.main_package.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.databinding.ItemModuleBinding
import com.anurupjaiswal.learnandachieve.model.Module


class ModulesAdapter(
    private val context: Context,

    private val modules: List<Module>,
    private val onModuleClick: (Module) -> Unit
) : RecyclerView.Adapter<ModulesAdapter.ModuleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val binding = ItemModuleBinding.inflate(LayoutInflater.from(context), parent, false)
        return ModuleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        holder.bind(modules[position])
    }

    override fun getItemCount(): Int = modules.size

    inner class ModuleViewHolder(private val binding: ItemModuleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(module: Module) {
            // Bind module data to views
            binding.tvModuleName.text = module.module_name
            binding.tvChapter.text = "Chapter ${position + 1}: "
            binding.root.setOnClickListener {
                onModuleClick(module)
            }
        }
    }


}
