package `is`.hth.wakatimeclient

import android.annotation.TargetApi
import android.content.SharedPreferences
import android.os.Build

/**
 *
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
fun SharedPreferences.modify(func: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    editor.func()
    editor.apply()
}