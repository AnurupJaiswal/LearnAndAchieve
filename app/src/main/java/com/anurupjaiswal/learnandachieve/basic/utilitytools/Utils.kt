package com.anurupjaiswal.learnandachieve.basic.utilitytools

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Html
import android.text.Spannable
import android.text.format.DateFormat
import android.util.Base64
import android.util.Log
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.webkit.WebView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.database.User
import com.anurupjaiswal.learnandachieve.basic.database.UserDataHelper
import com.anurupjaiswal.learnandachieve.basic.retrofit.Const
import com.anurupjaiswal.learnandachieve.basic.utilitytools.loadingButton.LoadingButton
import com.anurupjaiswal.learnandachieve.databinding.AlertdialogBinding
import com.anurupjaiswal.learnandachieve.databinding.CustomToastBinding
import com.anurupjaiswal.learnandachieve.databinding.DialogLogoutBinding
import com.anurupjaiswal.learnandachieve.main_package.ui.activity.LoginActivity
import com.google.android.material.card.MaterialCardView

import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


object Utils {
    @JvmStatic
    fun GetSession(): User {
        return UserDataHelper.instance.list[0]
    }



        fun renderHtml(textView: TextView, htmlContent: String) {
        // This will render the HTML content in the TextView
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            textView.text = Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY)
        } else {
            textView.text = Html.fromHtml(htmlContent)
        }
    }


    // Function to convert HSL to RGB
    fun hslToRgb(h: Int, s: Int, l: Int): Int {
        val c = (1 - Math.abs(2 * l / 100.0 - 1)) * s / 100.0
        val x = c * (1 - Math.abs(((h / 60) % 2) - 1))
        val m = l / 100.0 - c / 2

        var r = 0.0
        var g = 0.0
        var b = 0.0

        when (h) {
            in 0..59 -> {
                r = c; g = x; b = 0.0
            }

            in 60..119 -> {
                r = x; g = c; b = 0.0
            }

            in 120..179 -> {
                r = 0.0; g = c; b = x
            }

            in 180..239 -> {
                r = 0.0; g = x; b = c
            }

            in 240..299 -> {
                r = x; g = 0.0; b = c
            }

            in 300..359 -> {
                r = c; g = 0.0; b = x
            }
        }

        r += m
        g += m
        b += m

        val rInt = (r * 255).toInt()
        val gInt = (g * 255).toInt()
        val bInt = (b * 255).toInt()

        return Color.rgb(rInt, gInt, bInt)
    }

    @JvmStatic
    fun IS_LOGIN(): Boolean {
        return UserDataHelper.instance.list.size > 0
    }

    fun I(cx: Context, startActivity: Class<*>?, data: Bundle?) {
        val i = Intent(cx, startActivity)
        if (data != null) i.putExtras(data)
        cx.startActivity(i)
    }

    @JvmStatic
    val deviceName: String
        get() {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.startsWith(manufacturer)) {
                capitalize(model)
            } else {
                capitalize(manufacturer) + " " + model
            }
        }

    @SuppressLint("Range")
    fun getFileName(uri: Uri, c: Context): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = c.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    fun getWindowWidth(): Int {
        // Calculate window height for fullscreen use

        return Resources.getSystem().displayMetrics.widthPixels
    }

    private fun capitalize(s: String?): String {
        if (s == null || s.length == 0) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            first.uppercaseChar().toString() + s.substring(1)
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    fun setWebView(webView: WebView, data: String?) {
        webView.settings.javaScriptEnabled = true
        webView.settings.builtInZoomControls = true
        webView.loadData(data!!, "text/html; charset=utf-8", "UTF-8")
    }

    @JvmStatic
    fun UnAuthorizationToken(cx: Context) {
        UserDataHelper.instance.deleteAll()

        T(cx, "Session expired. Please log in again.")
        I_clear(cx, LoginActivity::class.java, null)
    }

    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun byteArrayToBitmap(context: Context, data: ByteArray?): Bitmap {
        val bitmap: Bitmap
        if (data != null) {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
        } else {
            bitmap = BitmapFactory.decodeResource(
                context.resources,
                R.drawable.dummy
            )
        }
        return bitmap
    }

    fun InternetDialog(context: Context?) {
        val dialog = Dialog(context!!, android.R.style.Theme_DeviceDefault_Dialog_Alert)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.alertdialog)
        dialog.findViewById<View>(R.id.tvPermittManually).setOnClickListener { view: View? ->
            if (AppController.instance.isOnline) {
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    fun SetHtmlContent(textView: TextView, data: String?) {
        val imageGetter = PicassoImageGetter(textView)
        val html: Spannable
        html = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(data, Html.FROM_HTML_MODE_LEGACY, imageGetter, null) as Spannable
        } else {
            Html.fromHtml(data, imageGetter, null) as Spannable
        }
        textView.text = html
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    fun CustomAlertDialog(context: Context?, message: String?) {
        val dialog = Dialog(context!!, android.R.style.Theme_DeviceDefault_Dialog_Alert)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val alertdialogBinding = AlertdialogBinding.inflate(LayoutInflater.from(context))
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(alertdialogBinding.root)
        alertdialogBinding.tvDesc.text = message
        dialog.findViewById<View>(R.id.tvPermittManually).setOnClickListener { view: View? ->
            if (AppController.instance.isOnline) {
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    fun logoutAlertDialog(c: Context): Dialog {
        val dialog = Dialog(c, android.R.style.Theme_DeviceDefault_Dialog_Alert)
        dialog.setCanceledOnTouchOutside(false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.logout)
        dialog.findViewById<View>(R.id.tvOK).setOnClickListener { view: View? ->
            UnAuthorizationToken(c)
            dialog.dismiss()
        }
        dialog.findViewById<View>(R.id.tvCancel)
            .setOnClickListener { view: View? -> dialog.cancel() }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()
        return dialog
    }

    fun deleteAccountAlertDialog(c: Context): Dialog {
        val dialog = Dialog(c, android.R.style.Theme_DeviceDefault_Dialog_Alert)
        dialog.setCanceledOnTouchOutside(false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.logout)
        val textView = dialog.findViewById<TextView>(R.id.tvDesc)
        textView.setText(R.string.are_you_sure_want_to_delete_your_account)
        dialog.findViewById<View>(R.id.tvOK).setOnClickListener { view: View? ->
            //deleteUserAccount(c)
            dialog.dismiss()
        }
        dialog.findViewById<View>(R.id.tvCancel)
            .setOnClickListener { view: View? -> dialog.cancel() }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()
        return dialog
    }

    fun Picasso(Url: String, imageView: ImageView?, dummy: Int) {
        Picasso.get().load(Const.baseUrlForImage + Url)
            .fetch(object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    if (dummy == 0) Picasso.get()
                        .load(Const.baseUrlForImage + Url)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(imageView) else Picasso.get()
                        .load(Const.baseUrlForImage + Url)
                        .error(dummy)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(imageView)
                }

                override fun onError(e: Exception) {
                    if (dummy == 0) Picasso.get()
                        .load(Const.baseUrlForImage + Url)
                        .into(imageView) else Picasso.get()
                        .load(Const.baseUrlForImage + Url)
                        .error(dummy)
                        .into(imageView)
                }
            })
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    val currentDate: String
        get() {
            val c = Calendar.getInstance().time
            E("Current time => $c")
            val df =
                SimpleDateFormat("dd MMM ,yyyy", Locale.getDefault())
            return df.format(c)
        }

    fun prettyCount(number: Number): String {
        val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
        val numValue = number.toLong()
        val value = Math.floor(Math.log10(numValue.toDouble())).toInt()
        val base = value / 3
        return if (value >= 3 && base < suffix.size) {
            E(
                "::prettyCount::" + numValue / Math.pow(
                    10.0,
                    (base * 3).toDouble()
                )
            )
            val i = "" + numValue / Math.pow(10.0, (base * 3).toDouble())
            val values = i.split("\\.").toTypedArray()
            if (values[1] == "0") {
                values[0] + suffix[base]
            } else {
                DecimalFormat("#0.0")
                    .format(numValue / Math.pow(10.0, (base * 3).toDouble())) + suffix[base]
            }
        } else {
            DecimalFormat("#,##0").format(numValue)
        }
    }

    fun <T> removeDuplicates(list: List<T>): List<T> {

        // Create a new ArrayList
        val newList: MutableList<T> = ArrayList()

        // Traverse through the first list
        for (element in list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {
                newList.add(element)
            }
        }

        // return the new list
        return newList
    }

    /**
     * Change the status bar Color of the Activity to the Desired Color.
     *
     * @param activity - Activity
     * @param color    - Desired Color
     */
    fun changeStatusBarColor(activity: Activity, color: Int) {
        val window = activity.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(activity, color)
    }

    fun printKeyHash(context: Activity): String? {
        val packageInfo: PackageInfo
        var key: String? = null
        try {
//getting application package name, as defined in manifest
            val packageName = context.applicationContext.packageName

//Retriving package info
            packageInfo = context.packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            )
            E("Package Name=" + context.packageName)
            for (signature in packageInfo.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                key = String(Base64.encode(md.digest(), 0))
                E("Key Hash=$key")
            }
        } catch (e1: PackageManager.NameNotFoundException) {
            E("Name not found$e1")
        } catch (e: NoSuchAlgorithmException) {
            E("No such an algorithm$e")
        } catch (e: Exception) {
            E("Exception$e")
        }
        return key
    }

    fun I_finish(cx: Context, startActivity: Class<*>?, data: Bundle?) {
        val i = Intent(cx, startActivity)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        if (data != null) i.putExtras(data)
        cx.startActivity(i)
    }

    fun I_clear(cx: Context, startActivity: Class<*>?, data: Bundle?) {
        val i = Intent(cx, startActivity)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        if (data != null) i.putExtras(data)
        cx.startActivity(i)
    }

    @JvmStatic
    fun E(msg: String) {
        if (Const.Development == Constants.Debug) Log.e("Log.E", msg)
    }

    fun DetectUIMode(activity: Activity): Int {
        return activity.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
    }

    fun getFormattedDate(smsTimeInMilis: Long, context: Context): String {
        val smsTime = Calendar.getInstance()
        smsTime.timeInMillis = smsTimeInMilis
        val now = Calendar.getInstance()
        val timeFormatString = "h:mm aa"
        val dateTimeFormatString = "EEE, MMM d | h:mm aa"
        val HOURS = (60 * 60 * 60).toLong()
        return if (now[Calendar.DATE] == smsTime[Calendar.DATE]) {
            context.getString(R.string.today) + " " + DateFormat.format(
                timeFormatString,
                smsTime
            )
        } else if (now[Calendar.DATE] - smsTime[Calendar.DATE] == 1) {
            context.getString(R.string.yesterday) + " " + DateFormat.format(
                timeFormatString,
                smsTime
            )
        } else if (now[Calendar.YEAR] == smsTime[Calendar.YEAR]) {
            DateFormat.format(dateTimeFormatString, smsTime).toString()
        } else {
            DateFormat.format("MMM dd yyyy | h:mm aa", smsTime).toString()
        }
    }


    fun formatDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        return outputFormat.format(date ?: Date())
    }

    val dateAfterYear: String
        get() {
            val cal = Calendar.getInstance()
            cal.time = Date()
            cal.add(Calendar.YEAR, 1)
            val smsTime = Calendar.getInstance()
            smsTime.timeInMillis = cal.time.time
            return DateFormat.format(
                "dd'" + getOrdinal(
                    smsTime[Calendar.DAY_OF_MONTH]
                ) + "' MMM yyyy", smsTime
            ).toString()
        }

    fun getOrdinal(day: Int): String {
        val ordinal: String
        ordinal = when (day % 20) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> if (day > 30) "st" else "th"
        }
        return ordinal
    }

    fun initProgressDialog(c: Context?): Dialog {
        if (c == null) throw IllegalArgumentException("Context cannot be null")

        return Dialog(c).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.progress_dialog)
            window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }
    }

    /* fun videoProgressDialog(c: Context?): Dialog {
         val dialog = Dialog(c!!)
         dialog.setCanceledOnTouchOutside(false)
         dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
         dialog.setContentView(R.layout.progress_dialog)
         dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
         dialog.setCanceledOnTouchOutside(false)
         dialog.setCancelable(true)
         dialog.show()
         return dialog
     }*/
    /*
        fun pdfProgressDialog(c: Context?): Dialog {
            val dialog = Dialog(c!!)
            dialog.setCanceledOnTouchOutside(false)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.progress_dialog)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCanceledOnTouchOutside(false)
            dialog.setOnCancelListener { T(c, "Unable to load Pdf") }
            dialog.show()
            return dialog
        }*/

    @SuppressLint("InflateParams")
    fun T(c: Context?, msg: String?) {
        // Inflate the custom layout for the toast using ViewBinding
        val binding = CustomToastBinding.inflate(LayoutInflater.from(c))

        // Set the message to the TextView in the custom layout
        binding.toastMessage.text = msg

        // Set the logo if needed (optional)
        // binding.toastLogo.setImageResource(R.drawable.ic_logo)

        // Create the custom toast
        val toast = Toast(c)
        toast.duration = Toast.LENGTH_SHORT
        toast.setView(binding.root)

        // Set the position of the toast (above the default position)
      //  toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 110)

        // Show the custom toast
        toast.show()
    }


    fun share(c: Context, subject: String?, shareBody: String?) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        c.startActivity(Intent.createChooser(sharingIntent, "Share via"))
    }

    fun getMimeType(context: Context, uri: Uri): String {
        val extension: String?
        //Check uri format to avoid null
        extension = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //If scheme is a content
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
        }
        return extension ?: ""
    }

    fun T_Long(c: Context?, msg: String?) {
        Toast.makeText(c, msg, Toast.LENGTH_LONG).show()
    }

    fun disableButton(loader: LoadingButton) {
        loader.alpha = 0.4f
        loader.isEnabled = false
    }

    fun enableButton(loader: LoadingButton) {
        loader.alpha = 1.0f
        loader.isEnabled = true
    }


    /*public static void setLanguage(String language, @NonNull Context context) {
        SavedData.saveLanguage(language);
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());
        LocaleHelper.setLocale(context, language);
    }*/
    fun alert(activity: Context, message: String?) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(activity.getString(R.string.app_name))
        builder.setMessage(message)
            .setPositiveButton(R.string.ok) { dialogInterface: DialogInterface, i: Int -> dialogInterface.cancel() }
        val alertdialog = builder.create()
        alertdialog.show()
    }

    /*
        fun expandOrCollapseView(v: View, expand: Boolean) {
            if (expand) {
                E("expand dikha")
                v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                val targetHeight = v.measuredHeight
                v.layoutParams.height = 0
                v.visibility = View.VISIBLE
                val valueAnimator = ValueAnimator.ofInt(targetHeight)
                valueAnimator.addUpdateListener { animation ->
                    v.layoutParams.height = animation.animatedValue
                    v.requestLayout()
                }
                valueAnimator.interpolator = DecelerateInterpolator()
                valueAnimator.duration = 200
                valueAnimator.start()
            } else {
                E("expand nhi dikha")
                val initialHeight = v.measuredHeight
                val valueAnimator = ValueAnimator.ofInt(initialHeight, 0)
                valueAnimator.interpolator = DecelerateInterpolator()
                valueAnimator.addUpdateListener { animation ->
                    v.layoutParams.height = animation.animatedValue
                    v.requestLayout()
                    if (animation.animatedValue as Int == 0) v.visibility = View.GONE
                }
                valueAnimator.interpolator = DecelerateInterpolator()
                valueAnimator.duration = 200
                valueAnimator.start()
            }
        }
    */

    fun hashMapTORequestBody(hm: HashMap<*, *>): RequestBody {
        val json = (hm as Map<*, *>?)?.let { JSONObject(it).toString() }
        return json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
    }


    fun toggleProgressBarAndText(
        showProgressBar: Boolean,
        progressBar: ProgressBar,
        textView: TextView,
        rootView: View
    ) {
        if (showProgressBar) {
            progressBar.visibility = View.VISIBLE
            textView.visibility = View.GONE
            rootView.isEnabled = false // Disable interaction
        } else {
            progressBar.visibility = View.GONE
            textView.visibility = View.VISIBLE
            rootView.isEnabled = true // Enable interaction
        }
    }

    fun startCountdownTimer(durationInMinutes: Int, timerTextView: TextView) {
        val totalMillis = durationInMinutes * 60 * 1000L // Convert minutes to milliseconds

        object : CountDownTimer(totalMillis, 1000) { // Update every second
            override fun onTick(millisUntilFinished: Long) {
                val hours = millisUntilFinished / (1000 * 60 * 60)
                val minutes = (millisUntilFinished / (1000 * 60)) % 60
                val seconds = (millisUntilFinished / 1000) % 60

                // Apply italic style to "Left"
                val formattedTime = String.format("%02d hr %02d mins %02d secs <i>Left</i>", hours, minutes, seconds)

                // Set formatted text
                timerTextView.text = Html.fromHtml(formattedTime, Html.FROM_HTML_MODE_LEGACY)
            }

            override fun onFinish() {
                timerTextView.text = "Time's Up!"
            }
        }.start()

    }
    fun showLogoutDialog(
        context: Context,
        onLogoutConfirmed: (ProgressBar, MaterialCardView) -> Unit, // Pass UI elements for visibility handling
        onCancel: () -> Unit = {} // Optional cancel callback
    ) {
        val binding = DialogLogoutBinding.inflate(LayoutInflater.from(context))

        val dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setCancelable(false)
            .create()

        dialog.setCanceledOnTouchOutside(false)

        // Handle Logout button click
        binding.mcvLogout.setOnClickListener {
            binding.loading.visibility = View.VISIBLE // Show loading indicator
            binding.mcvLogout.isEnabled = false // Disable logout button

            onLogoutConfirmed(binding.loading, binding.mcvLogout) // Pass views to handle UI updates
        }

        // Handle Cancel button click
        binding.mcvCancel.setOnClickListener {
            onCancel()
            dialog.dismiss()
        }

        dialog.show()
    }



}