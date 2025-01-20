package com.anurupjaiswal.learnandachieve.main_package.ui.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.utilitytools.BaseActivity
import com.anurupjaiswal.learnandachieve.basic.wheelpicker.DatePicker


import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class DatePickerExampleActivity : BaseActivity() {



    private lateinit var selectedDateText: TextView
    private val dateFormatter = SimpleDateFormat("d MMM yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_date_picker_example)

        selectedDateText = findViewById(R.id.selected_date_text)

        // Setting up button to show the bottom sheet picker
        val actionSheetButton: MaterialCardView = findViewById(R.id.action_sheet_button)
        actionSheetButton.setOnClickListener {
            showDatePickerBottomSheet()
        }
    }

    private fun showDatePickerBottomSheet() {
        val picker = DatePicker(this)
        picker.show(window)

        picker.pickerView?.apply {
            post {
                // Set the range of years from 1990 to 2024


                // Set default date to January 1, 2024
            //    setDate(2005, 0, 29)
            }
        }

        picker.setOnClickOkButtonListener {
            val pickerView = picker.pickerView ?: return@setOnClickOkButtonListener
            val year = pickerView.year
            val month = pickerView.month
            val day = pickerView.day

            // Update selected date text
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, day)
            }.time
            selectedDateText.text = dateFormatter.format(selectedDate) // Display selected date in TextView

            picker.hide()
        }
    }
}


