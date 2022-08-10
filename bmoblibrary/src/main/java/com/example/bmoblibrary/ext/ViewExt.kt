package com.example.bmoblibrary.ext

import android.app.Activity
import android.text.Editable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView

/**
 * 隐藏软键盘
 */
fun hideSoftKeyboard(activity: Activity?) {
    activity?.let { act ->
        val view = act.currentFocus
        view?.let {
            val inputMethodManager =
                act.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}

inline val TextView.textString: String
    get() = this.text.toString()

inline val EditText.textString: String
    get() = this.text.toString()

inline val Editable.textString
    get() = this.toString()

inline val View.visible:Int
    get() = View.VISIBLE

inline val View.gone:Int
    get() = View.GONE