package com.example.alarmapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.net.URL
import java.util.Calendar

class MainActivity : ComponentActivity() {

    private lateinit var timePicker: TimePicker
    private lateinit var setAlarmButton: Button

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Layout programmgesteuert erstellen
        val layout = RelativeLayout(this).apply {
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
        }

        // TimePicker hinzufügen
        timePicker = TimePicker(this).apply {
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.CENTER_IN_PARENT)
            }
        }

        // Button hinzufügen
        setAlarmButton = Button(this).apply {
            text = "Set Alarm"
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.BELOW, timePicker.id)
                addRule(RelativeLayout.CENTER_HORIZONTAL)
                topMargin = 20
            }
        }

        layout.addView(timePicker)
        layout.addView(setAlarmButton)

        setContentView(layout)

        setAlarmButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            calendar.set(Calendar.MINUTE, timePicker.minute)
            calendar.set(Calendar.SECOND, 0)
            Log.println(Log.VERBOSE, "Format", timePicker.hour.toString() + ":" + timePicker.minute.toString())

            setAlarm(calendar.timeInMillis)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun setAlarm(timeInMillis: Long) {
        Log.v("Alarm","Setting an alarm.")
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            Toast.makeText(this, "Alarm set for: ${timePicker.hour}:${timePicker.minute}", Toast.LENGTH_SHORT).show()
        }

        CoroutineScope(Dispatchers.IO).launch {
            sendGet()
        }
    }

    fun sendGet() {
            val url = URL("https://www.google.com/")

            val okHttpClient = OkHttpClient()
            try {
                val parsedResponse = parseResponse(okHttpClient.newCall(createRequest()).execute())
                Log.v("GET", parsedResponse)
            } catch (e: Exception) {
                Log.e("GET-Stacktrace", e.stackTrace.toString())
                e.message?.let { Log.e("GET", it) }
            }
    }


    fun createRequest(): Request {
        return Request.Builder()
            .url("https://www.google.com/")
            .build()
    }

    fun parseResponse(response: Response): String {
        val body = response.body?.string() ?: ""
        return body
    }
}
