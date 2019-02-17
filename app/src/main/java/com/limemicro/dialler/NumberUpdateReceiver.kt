package com.limemicro.dialler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NumberUpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val phoneNumber = intent.extras?.getString("number")
        if (phoneNumber != null) {
            log("Updating phone number to: $phoneNumber")
            Settings.setPhoneNumber(context, phoneNumber)
        }
    }

}