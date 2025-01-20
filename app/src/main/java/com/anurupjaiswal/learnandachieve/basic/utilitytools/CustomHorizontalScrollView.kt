package com.anurupjaiswal.learnandachieve.basic.utilitytools

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.HorizontalScrollView

class CustomHorizontalScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // Trigger performClick for accessibility when the view is touched
        if (event?.action == MotionEvent.ACTION_DOWN) {
            performClick()
        }
        return super.onTouchEvent(event)
    }


}
