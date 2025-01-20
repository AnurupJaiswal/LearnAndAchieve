package com.anurupjaiswal.learnandachieve.basic.wheelpicker

import android.content.Context
import android.text.SpannableString
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.wheelpicker.core.BaseWheelPickerView
import com.anurupjaiswal.learnandachieve.basic.wheelpicker.core.ItemEnableWheelAdapter
import com.anurupjaiswal.learnandachieve.basic.wheelpicker.core.TextWheelPickerView
import com.anurupjaiswal.learnandachieve.basic.wheelpicker.core.TextWheelViewHolder
import com.anurupjaiswal.learnandachieve.basic.wheelpicker.core.TripleDependentData
import com.anurupjaiswal.learnandachieve.basic.wheelpicker.core.TripleDependentPickerView
import com.anurupjaiswal.learnandachieve.databinding.TriplePickerViewBinding
import java.lang.ref.WeakReference
import java.text.DateFormatSymbols
import java.util.*


class YearWheelAdapter(
    valueEnabledProvider: WeakReference<ValueEnabledProvider>
) : ItemEnableWheelAdapter(valueEnabledProvider) {

    private val startYear = 1960
    private val endYear = 2022
    private val totalYears = endYear - startYear + 1

    override fun getItemCount(): Int {
        // Set the item count to match the number of years between start and end year
        return totalYears
    }

    override val valueCount: Int
        get() = totalYears

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextWheelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.wheel_picker_item, parent, false) as TextView
        return TextWheelViewHolder(view)
    }

    override fun onBindViewHolder(holder: TextWheelViewHolder, position: Int) {
        // Calculate the actual year based on position and startYear
        val year = startYear + position
        val text = SpannableString("$year")
        val isEnabled = valueEnabledProvider.get()?.isEnabled(this, position) ?: true
        holder.onBindData(
            TextWheelPickerView.Item(
                id = "$year",
                text = text,
                isEnabled = isEnabled
            )
        )
    }
}

class DatePickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TripleDependentPickerView(context, attrs, defStyleAttr),
    BaseWheelPickerView.WheelPickerViewListener {

    enum class Mode {
        YEAR_MONTH_DAY,
        YEAR_MONTH
    }

    var mode: Mode = Mode.YEAR_MONTH_DAY
        set(value) {
            if (field == value) {
                return
            }
            field = value
            when (value) {
                Mode.YEAR_MONTH_DAY -> dayPickerView.visibility = View.VISIBLE
                Mode.YEAR_MONTH -> {
                    dayPickerView.visibility = View.GONE
                    setThird(1, false, null)
                }
            }
        }

    private val startYear = 1960 // Define your start year here

    override val adapters: Triple<RecyclerView.Adapter<*>, RecyclerView.Adapter<*>, RecyclerView.Adapter<*>>
        get() = Triple(yearAdapter, monthAdapter, dayAdapter)

    override val currentData: TripleDependentData
        get() = TripleDependentData(year, month, day)

    override fun minData(): TripleDependentData? {
        if (minDate == null) {
            return null
        }
        return TripleDependentData(
            minDateCalendar.get(Calendar.YEAR),
            minDateCalendar.get(Calendar.MONTH),
            minDateCalendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    override fun maxData(): TripleDependentData? {
        if (maxDate == null) {
            return null
        }
        return TripleDependentData(
            maxDateCalendar.get(Calendar.YEAR),
            maxDateCalendar.get(Calendar.MONTH),
            maxDateCalendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    override fun value(adapter: RecyclerView.Adapter<*>, valueIndex: Int): Int {
        if (adapter == dayAdapter) {
            return valueIndex + 1
        }
        return valueIndex
    }

    interface Listener {
        fun didSelectData(year: Int, month: Int, day: Int)
    }

    private val highlightView: View = run {
        val view = View(context)
        view.background = ContextCompat.getDrawable(context, R.drawable.text_wheel_highlight_bg)
        view
    }
    private val yearPickerView: TextWheelPickerView
    private val monthPickerView: TextWheelPickerView
    private val dayPickerView: TextWheelPickerView

    private var listener: Listener? = null
    private val minDateCalendar: Calendar = Calendar.getInstance()
    private val maxDateCalendar: Calendar = Calendar.getInstance()

    fun setWheelListener(listener: Listener) {
        this.listener = listener
    }

    var minDate: Date? = null
        set(value) {
            val newValue = if (value != null && maxDate != null) minOf(maxDate ?: value, value) else value
            val oldData = minData()
            if (field == newValue) {
                return
            }
            field = newValue
            newValue?.let {
                minDateCalendar.time = it
            }
            val newData = minData()
            minData()?.let {
                updateCurrentDataByMinData(it, false)
            }
            reloadPickersIfNeeded(oldData, newData)
        }

    var maxDate: Date? = null
        set(value) {
            val newValue = if (value != null && minDate != null) maxOf(minDate ?: value, value) else value
            val oldData = maxData()
            if (field == newValue) {
                return
            }
            field = newValue
            newValue?.let {
                maxDateCalendar.time = it
            }
            val newData = maxData()
            maxData()?.let {
                updateCurrentDataByMaxData(it, false)
            }
            reloadPickersIfNeeded(oldData, newData)
        }

    val day: Int
        get() = dayPickerView.selectedIndex + 1

    val month: Int
        get() = monthPickerView.selectedIndex

    val year: Int
        get() = yearPickerView.selectedIndex + startYear

    fun setDate(year: Int, month: Int, day: Int) {
        setFirst(year - startYear, false) { // Offset by startYear for correct index
            setSecond(month, false) {
                setThird(day, false, null)
            }
        }
    }

    var isCircular: Boolean = false
        set(value) {
            field = value
            dayPickerView.isCircular = value
            monthPickerView.isCircular = value
            yearPickerView.isCircular = value
        }

    private val yearAdapter = YearWheelAdapter(WeakReference(this))
    private val monthAdapter = ItemEnableWheelAdapter(WeakReference(this))
    private val dayAdapter = ItemEnableWheelAdapter(WeakReference(this))

    private val binding: TriplePickerViewBinding =
        TriplePickerViewBinding.inflate(LayoutInflater.from(context), this)

    override fun setFirst(value: Int, animated: Boolean, completion: (() -> Unit)?) {
        if (this.year == value) {
            completion?.invoke()
            return
        }
        yearPickerView.setSelectedIndex(value, animated, completion)
    }

    override fun setSecond(value: Int, animated: Boolean, completion: (() -> Unit)?) {
        if (this.month == value) {
            completion?.invoke()
            return
        }
        monthPickerView.setSelectedIndex(value, animated, completion)
    }

    override fun setThird(value: Int, animated: Boolean, completion: (() -> Unit)?) {
        if (this.day == value) {
            completion?.invoke()
            return
        }
        dayPickerView.setSelectedIndex(value - 1, animated, completion)
    }

    override fun setHapticFeedbackEnabled(hapticFeedbackEnabled: Boolean) {
        super.setHapticFeedbackEnabled(hapticFeedbackEnabled)
        yearPickerView.isHapticFeedbackEnabled = hapticFeedbackEnabled
        monthPickerView.isHapticFeedbackEnabled = hapticFeedbackEnabled
        dayPickerView.isHapticFeedbackEnabled = hapticFeedbackEnabled
    }

    init {
        dayPickerView = binding.leftPicker
        dayPickerView.setAdapter(dayAdapter)
        monthPickerView = binding.midPicker
        monthPickerView.setAdapter(monthAdapter)
        monthAdapter.values = (0 until 12).map {
            TextWheelPickerView.Item(
                "$it",
                DateFormatSymbols.getInstance().shortMonths[it]
            )
        }
        yearPickerView = binding.rightPicker
        yearPickerView.setAdapter(yearAdapter)
        addView(highlightView)
        (highlightView.layoutParams as? LayoutParams)?.apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = context.resources.getDimensionPixelSize(R.dimen.text_wheel_picker_item_height)
            gravity = Gravity.CENTER_VERTICAL
        }

        dayPickerView.setWheelListener(this)
        monthPickerView.setWheelListener(this)
        yearPickerView.setWheelListener(this)

        val calendar = Calendar.getInstance()
        setDate(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    private fun updateDayPickerViewIfNeeded(): Boolean {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val dayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        if (dayAdapter.values.count() == dayCount) {
            return false
        }
        dayAdapter.values = (0 until dayCount).map {
            val day = it + 1
            TextWheelPickerView.Item(
                "$day",
                context.getString(R.string.day_time_picker_format_day, day)
            )
        }
        dayPickerView.post {
            dayPickerView.refreshCurrentPosition()
        }
        return true
    }

    private fun dateIsValid(data: TripleDependentData): Boolean {
        if (data.first == NO_POSITION && data.second == NO_POSITION && data.third == NO_POSITION) {
            return false
        }
        val format = "%04d%02d%02d"
        val selectedDateString = format.format(data.first, data.second, data.third)
        minData()?.let {
            if (selectedDateString < format.format(it.first, it.second, it.third)) {
                return false
            }
        }
        maxData()?.let {
            if (selectedDateString > format.format(it.first, it.second, it.third)) {
                return false
            }
        }
        return true
    }

    // region BaseWheelPickerView.WheelPickerViewListener
    override fun didSelectItem(picker: BaseWheelPickerView, index: Int) {
        var dayPickerUpdated = false
        if (picker == yearPickerView || picker == monthPickerView) {
            dayPickerUpdated = updateDayPickerViewIfNeeded()
        }
        if (minDate != null || maxDate != null) {
            if (picker == yearPickerView) {
                monthAdapter.notifyDataSetChanged()
                if (!dayPickerUpdated) {
                    dayAdapter.notifyDataSetChanged()
                }
            } else if (picker == monthPickerView && !dayPickerUpdated) {
                dayAdapter.notifyDataSetChanged()
            }
        }
        if (!dateIsValid(currentData)) {
            minData()?.let {
                updateCurrentDataByMinData(it, true)
            }
            maxData()?.let {
                updateCurrentDataByMaxData(it, true)
            }
        }
        listener?.didSelectData(year, month, day)
    }
}