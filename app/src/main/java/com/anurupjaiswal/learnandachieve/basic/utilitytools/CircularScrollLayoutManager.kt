package com.anurupjaiswal.learnandachieve.basic.utilitytools

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class CircularScrollLayoutManager(context: Context) : LinearLayoutManager(context, VERTICAL, false) {

    private val radius = 400f // Adjust radius for circular effect
    private val maxScale = 1.2f // Scale for centered item
    private val minScale = 0.7f // Scale for outer items

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        if (recycler == null || state == null) return

        try {
            detachAndScrapAttachedViews(recycler)
        } catch (e: NullPointerException) {
            e.printStackTrace()
            return
        }

        super.onLayoutChildren(recycler, state)
        applyCircularLayout()
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        val scrolled = super.scrollVerticallyBy(dy, recycler, state)
        applyCircularLayout()
        return scrolled
    }

    private fun applyCircularLayout() {
        val midpoint = height / 2f
        val itemCount = childCount

        for (i in 0 until itemCount) {
            val child = getChildAt(i) ?: continue

            // Angle calculation for circular layout
            val angle = (360f / itemCount) * i - 90 // Offset to start from top

            // X and Y positions based on angle and radius
            val x = radius * cos(Math.toRadians(angle.toDouble())).toFloat() + (width / 2)
            val y = radius * sin(Math.toRadians(angle.toDouble())).toFloat() + midpoint

            // Apply translation
            child.translationX = x
            child.translationY = y

            // Scaling based on distance from center
            val distanceFromCenter = abs(midpoint - y)
            val scale = maxScale - (distanceFromCenter / radius) * (maxScale - minScale)
            child.scaleX = scale
            child.scaleY = scale

            // Keep items upright and apply slight rotation for 3D effect
            child.rotationX = (angle - 90) * 0.3f // Slight tilt
        }
    }
}