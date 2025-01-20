package com.anurupjaiswal.learnandachieve.basic.wheelpicker

import android.content.Context
import com.anurupjaiswal.learnandachieve.basic.wheelpicker.core.WheelPickerActionSheet

class WeekdayTimePicker(context: Context) : WheelPickerActionSheet<WeekdayTimePickerView>(context) {
    init {
        setPickerView(WeekdayTimePickerView(context))
    }
}