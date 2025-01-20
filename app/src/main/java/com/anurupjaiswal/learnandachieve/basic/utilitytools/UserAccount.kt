package com.anurupjaiswal.learnandachieve.basic.utilitytools

import android.annotation.SuppressLint
import android.content.Context
import android.widget.EditText
import android.text.TextUtils
import android.util.Patterns
import com.anurupjaiswal.learnandachieve.R
import java.util.regex.Pattern

/**
 * Created by Anil on 9/4/2021.
 */
@SuppressLint("StaticFieldLeak")
object UserAccount {
    @SuppressLint("StaticFieldLeak")
    var EditTextPointer: EditText? = null
    var errorMessage: String? = null

    /**
     * Email All Type Validation
     *
     * @param context  Page Reference
     * @param editText Edit Text To Check
     * @return true/false
     */
    @SuppressLint("StaticFieldLeak")
    fun isEmailValid(context: Context, editText: EditText): Boolean {
        //add your own logic
        return if (TextUtils.isEmpty(editText.text.toString().trim { it <= ' ' })) {
            EditTextPointer = editText
            errorMessage = context.getString(R.string.empty_error)
            false
        } else {
            if (Patterns.EMAIL_ADDRESS.matcher(
                    editText.text.toString().trim { it <= ' ' }).matches()
            ) {
                true
            } else {
                EditTextPointer = editText
                errorMessage = context.getString(R.string.invalid_email)
                false
            }
        }
    }

    /**
     * Mobile All Type Validation
     *
     * @param context  Page Reference
     * @param editText Edit Text To Check
     * @return true/false
     */
    fun isValidPhoneNumber(context: Context, editText: EditText): Boolean {
        return if (editText.text == null || TextUtils.isEmpty(editText.text)) {
            errorMessage = context.getString(R.string.empty_error)
            EditTextPointer = editText
            false
        } else {
            if (editText.text.toString().length != 15) {
                EditTextPointer = editText
                errorMessage = context.getString(R.string.enter_ten_digits_number)
                false
            } else {
                if (Patterns.PHONE.matcher(editText.text).matches()) {
                    true
                } else {
                    EditTextPointer = editText
                    errorMessage = context.getString(R.string.valid_number)
                    false
                }
            }
        }
    }

    /**
     * Check Is Empty
     *
     * @param context Page Reference
     * @param arg     Multiple Edit Text To Check
     * @return true/false
     */
    fun isEmpty(context: Context, vararg arg: EditText): Boolean {
        for (editText in arg) {
            if (editText.text.toString().trim { it <= ' ' }.length <= 0) {
                EditTextPointer = editText
                EditTextPointer!!.requestFocus()
                errorMessage = context.getString(R.string.empty_error)
                return false
            }
        }
        return true
    }

    /**
     * Check Is PasswordMatch
     *
     * @param context      Page Reference
     * @param pass         Edit Text To Check
     * @param confirm_pass Edit Text To Check
     * @return true/false
     */
    fun isPasswordMatch(context: Context, pass: EditText, confirm_pass: EditText): Boolean {
        return if (pass.text == null || TextUtils.isEmpty(pass.text)) {
            errorMessage = context.getString(R.string.empty_error)
            EditTextPointer = pass
            false
        } else if (confirm_pass.text == null || TextUtils.isEmpty(confirm_pass.text)) {
            errorMessage = context.getString(R.string.empty_error)
            EditTextPointer = confirm_pass
            false
        } else {
            if (pass.text.toString() != confirm_pass.text.toString()) {
                EditTextPointer = confirm_pass
                errorMessage = context.getString(R.string.password_not_match)
                return false
            }
            true
        }
    }

    fun isValidData(context: Context, editText: EditText, msg: Int): Boolean {
        val p = Pattern.compile("^(\\d*\\.)?\\d+$")
        val s = editText.text.toString().trim { it <= ' ' }
        val m = p.matcher(s.trim { it <= ' ' })
        return if (m.matches()) {
            true
        } else {
            EditTextPointer = editText
            errorMessage = context.getString(msg)
            false
        }
    }

    fun isValidUrl(url: String): Boolean {
        val p =
            Pattern.compile("(ftp|http|https):\\/\\/(\\w+:{0,1}\\w*@)?(\\S+)(:[0-9]+)?(\\/|\\/([\\w#!:.?+=&%@!\\-\\/]))?")
        val s = url.trim { it <= ' ' }
        val m = p.matcher(s.trim { it <= ' ' })
        return m.matches()
    }

    /**
     * is Valid Aadhaar Number
     *
     * @param context  Page Reference
     * @param editText Edit Text To Check
     * @return true/false
     */
    fun isValidAmount(context: Context, editText: EditText): Boolean {
        return if (editText.text == null || TextUtils.isEmpty(editText.text)) {
            errorMessage = context.getString(R.string.empty_error)
            EditTextPointer = editText
            false
        } else {
            val p = Pattern.compile("^(\\d*\\.)?\\d+$")
            val s = editText.text.toString().trim { it <= ' ' }
            val m = p.matcher(s.trim { it <= ' ' })
            if (m.matches()) {
                true
            } else {
                EditTextPointer = editText
                errorMessage = context.getString(R.string.amount_valid)
                false
            }
        }
    }

    /**
     * is Valid Aadhaar Number
     *
     * @param context  Page Reference
     * @param editText Edit Text To Check
     * @return true/false
     */
    fun isValidAadhaarNumber(context: Context, editText: EditText): Boolean {
        return if (editText.text == null || TextUtils.isEmpty(editText.text)) {
            errorMessage = context.getString(R.string.empty_error)
            EditTextPointer = editText
            false
        } else {
            val p = Pattern.compile("^[2-9]{1}[0-9]{3}\\s[0-9]{4}\\s[0-9]{4}$")
            val s = editText.text.toString().replace("....".toRegex(), "$0 ")
            val m = p.matcher(s.trim { it <= ' ' })
            if (m.matches()) {
                true
            } else {
                EditTextPointer = editText
                errorMessage = context.getString(R.string.aadhaar_valid)
                false
            }
        }
    }

    /**
     * is Valid PAN Number
     *
     * @param context  Page Reference
     * @param editText Edit Text To Check
     * @return true/false
     */
    fun isValidPAN(context: Context, editText: EditText): Boolean {
        return if (editText.text == null || TextUtils.isEmpty(editText.text)) {
            errorMessage = context.getString(R.string.empty_error)
            EditTextPointer = editText
            false
        } else {
            val p = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}")
            val m = p.matcher(editText.text.toString())
            if (m.matches()) {
                true
            } else {
                EditTextPointer = editText
                errorMessage = context.getString(R.string.pan_card_valid)
                false
            }
        }
    }

    /**
     * is Valid IFSC Code
     *
     * @param context  Page Reference
     * @param editText Edit Text To Check
     * @return true/false
     */
    fun isValidIFSC(context: Context, editText: EditText): Boolean {
        return if (editText.text == null || TextUtils.isEmpty(editText.text)) {
            errorMessage = context.getString(R.string.empty_error)
            EditTextPointer = editText
            false
        } else {
            val p = Pattern.compile("^[A-Z]{4}0[A-Z0-9]{6}$")
            val m = p.matcher(editText.text.toString())
            if (m.matches()) {
                true
            } else {
                EditTextPointer = editText
                errorMessage = context.getString(R.string.ifsc_valid)
                false
            }
        }
    }
}