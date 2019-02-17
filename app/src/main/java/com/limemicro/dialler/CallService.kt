package com.limemicro.dialler

import android.telecom.Call
import android.telecom.InCallService

class CallService : InCallService() {

    override fun onCallAdded(call: Call) {
        CallState.call = call
    }

    override fun onCallRemoved(call: Call) {
        CallState.call = null
    }

}