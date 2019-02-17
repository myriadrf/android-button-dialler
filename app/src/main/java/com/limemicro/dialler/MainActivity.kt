package com.limemicro.dialler

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telecom.TelecomManager
import android.telecom.TelecomManager.ACTION_CHANGE_DEFAULT_DIALER
import android.telecom.TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.checkSelfPermission
import android.telecom.Call
import android.view.WindowManager
import io.reactivex.disposables.Disposable
import java.net.URLEncoder


class MainActivity : AppCompatActivity() {

    private lateinit var stateObserver: Disposable
    private lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Keep the screen on, so long as we're in the foreground
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Take up the whole screen
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE

        // Handle clicks on the UI
        view = findViewById<Button>(R.id.view)
        view.setOnClickListener {
            if (view.isSelected || view.isActivated) {
                log("Clicked: Hanging up…")
                CallState.hangup()
            } else {
                log("Clicked: placing call…")
                makeCall()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // Attempt to become the default phone app
        offerToReplaceDefaultPhoneApp()

        // Listen for call state updates
        stateObserver = CallState.state
            .doOnEach { log("Call state: ${it.value?.asString()}") }
            .subscribe(::updateUi)
    }

    override fun onStop() {
        super.onStop()
        stateObserver.dispose()
    }

    private fun updateUi(state: Int) {
        view.isActivated = state in listOf(Call.STATE_DIALING, Call.STATE_RINGING)
        view.isSelected = state == Call.STATE_ACTIVE
    }

    private fun makeCall() {
        // Request permission to make calls, if necessary
        if (checkSelfPermission(this, CALL_PHONE) != PERMISSION_GRANTED) {
            requestPermissions(this, arrayOf(CALL_PHONE), REQUEST_PERMISSION)
            return
        }

        // Place the phone call
        val number = Settings.getPhoneNumber(this)
        val uri = Uri.parse("tel:" + URLEncoder.encode(number, Charsets.UTF_8.name()))
        log("Dialling: $uri")
        startActivity(Intent(Intent.ACTION_CALL, uri))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        // Place the call if we were granted permission
        if (requestCode == REQUEST_PERMISSION && PERMISSION_GRANTED in grantResults) {
            makeCall()
        }
    }

    private fun offerToReplaceDefaultPhoneApp() {
        if (getSystemService(TelecomManager::class.java).defaultDialerPackage != packageName) {
            Intent(ACTION_CHANGE_DEFAULT_DIALER)
                .putExtra(EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
                .let(::startActivity)
        }
    }

    companion object {
        const val REQUEST_PERMISSION = 0
    }

}
