package com.limemicro.dialler

import android.content.Context

object Settings {

    private const val DEFAULT_PHONE_NUMBER = "88888"

    private const val SETTINGS_NAME = "settings"
    private const val PHONE_NUMBER_KEY = "phone_number"

    fun getPhoneNumber(context: Context): String {
        return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE)
                .getString(PHONE_NUMBER_KEY, DEFAULT_PHONE_NUMBER)!!
    }

    fun setPhoneNumber(context: Context, phoneNumber: String) {
        context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit()
            .putString(PHONE_NUMBER_KEY, phoneNumber)
            .apply()
    }

}