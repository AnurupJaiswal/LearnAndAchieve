package com.anurupjaiswal.learnandachieve.basic.utilitytools

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.network.ConnectionStatus
import com.anurupjaiswal.learnandachieve.basic.network.ConnectivityLiveData
import com.anurupjaiswal.learnandachieve.basic.retrofit.Const
import com.anurupjaiswal.learnandachieve.databinding.BottomSheetInternetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView



open class BaseActivity : AppCompatActivity() {

    // Connectivity observer and BottomSheetDialog for network changes
    private lateinit var connectivityLiveData: ConnectivityLiveData
    private var bottomSheetDialog: BottomSheetDialog? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        if (Const.Development == Constants.Live) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Initialize ConnectivityLiveData to monitor network changes
        connectivityLiveData = ConnectivityLiveData(applicationContext)
        connectivityLiveData.observe(this, Observer { status ->
            when (status) {
                ConnectionStatus.AVAILABLE -> bottomSheetDialog?.dismiss()
                ConnectionStatus.LOST -> showNoInternetBottomSheet()
            }
        })
    }

    // Existing code to dismiss keyboard on touch outside an EditText
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    hideKeyboard(v)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    fun showSoftKeyboard(editText: EditText) {
        val imm = editText.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    // Display a non-cancelable BottomSheet when there is no internet connection
    private fun showNoInternetBottomSheet() {
        if (bottomSheetDialog?.isShowing == true) return

        val binding = BottomSheetInternetBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(this).apply {
            setContentView(binding.root)
            setCancelable(false)
        }

        binding.mcvRetry.setOnClickListener {
            if (connectivityLiveData.value == ConnectionStatus.AVAILABLE) {
                bottomSheetDialog?.dismiss()
            } else {
                Utils.T(this, "Still No Internet Connection")
            }
        }

        bottomSheetDialog?.show()
    }
}
