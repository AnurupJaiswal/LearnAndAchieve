package com.anurupjaiswal.learnandachieve.basic.wheelpicker

import android.content.Context
import com.anurupjaiswal.learnandachieve.basic.wheelpicker.core.WheelPickerActionSheet

class DayTimePicker(context: Context) : WheelPickerActionSheet<DayTimePickerView>(context) {
    init {
        setPickerView(DayTimePickerView(context))
    }
}