package com.anurupjaiswal.learnandachieve.basic.wheelpicker

import android.content.Context
import com.anurupjaiswal.learnandachieve.basic.wheelpicker.core.WheelPickerActionSheet

class DatePicker(context: Context) : WheelPickerActionSheet<DatePickerView>(context) {
    init {
        setPickerView(DatePickerView(context))
    }
}