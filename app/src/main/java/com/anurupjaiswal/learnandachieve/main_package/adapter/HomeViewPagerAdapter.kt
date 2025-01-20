package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.model.HomeViewpagerIteam

class HomeViewPagerAdapter(private val ctx: Context,
                           private val sliderItems: MutableList<HomeViewpagerIteam>,
                           private val viewPager2: ViewPager2
) : RecyclerView.Adapter<HomeViewPagerAdapter.SliderViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(ctx).inflate(R.layout.homepageviewpager_item_container, parent, false)
        return SliderViewHolder(view)

    }


  /*  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.slide_item_container, parent, false
        )
        return SliderViewHolder(view)
    }*/


    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.bind(sliderItems[position])
        if (position == sliderItems.size - 2) {

        }
    }


    override fun getItemCount(): Int = sliderItems.size

    inner class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageView: ImageView = itemView.findViewById(R.id.imageSlide)

        fun bind(sliderItem: HomeViewpagerIteam) {

            imageView.setImageResource(sliderItem.image)
        }
    }

}
