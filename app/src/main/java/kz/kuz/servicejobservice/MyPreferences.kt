package kz.kuz.servicejobservice

import android.content.Context
import android.preference.PreferenceManager

object MyPreferences {
    @JvmStatic
    fun isAlarmOn(context: Context?): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("isAlarmOn",
                false)
    }

    fun setAlarmOn(context: Context?, isOn: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean("isAlarmOn", isOn)
                .apply()
    }
}