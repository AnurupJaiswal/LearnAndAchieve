/*
 * *
 *  * Created by labhansh Gupta on 24/12/22, 1:21 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 19/12/22, 4:47 PM
 *  * All rights reserved.
 *
 */

package com.anurupjaiswal.learnandachieve.basic.loadingButton

import android.animation.Animator
import android.view.View

internal fun Animator.disposeAnimator() {
    end()
    removeAllListeners()
    cancel()
}

internal fun View.updateWidth(width: Int) {
    val layoutParams = this.layoutParams
    layoutParams.width = width
    this.layoutParams = layoutParams
}

internal fun View.updateHeight(height: Int) {
    val layoutParams = this.layoutParams
    layoutParams.height = height
    this.layoutParams = layoutParams
}
