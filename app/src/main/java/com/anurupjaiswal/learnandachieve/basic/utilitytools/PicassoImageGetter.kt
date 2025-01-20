package com.anurupjaiswal.learnandachieve.basic.utilitytools

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html.ImageGetter
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target

class PicassoImageGetter : ImageGetter {
    private var textView: TextView? = null

    constructor()
    constructor(target: TextView?) {
        textView = target
    }

    override fun getDrawable(source: String): Drawable {
        val drawable = BitmapDrawablePlaceHolder()
        Picasso.get()
            .load(source)
            .into(drawable)
        return drawable
    }

    private inner class BitmapDrawablePlaceHolder : BitmapDrawable(), Target {
        protected var drawable: Drawable? = null
        override fun draw(canvas: Canvas) {
            if (drawable != null) {
                drawable!!.draw(canvas)
            }
        }

        @JvmName("setDrawable1")
        fun setDrawable(drawable: Drawable) {
            this.drawable = drawable
            val width = drawable.intrinsicWidth / 2
            val height = drawable.intrinsicHeight / 2
            drawable.setBounds(0, 0, width, height)
            setBounds(0, 0, width, height)
            if (textView != null) {
                textView!!.text = textView!!.text
            }
        }

        override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
            setDrawable(BitmapDrawable(AppController.getContext().resources, bitmap))
        }

        override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {}
        override fun onPrepareLoad(placeHolderDrawable: Drawable) {}
    }
}