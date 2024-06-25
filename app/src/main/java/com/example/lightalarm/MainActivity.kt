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
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

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

            setAlarm(calendar.timeInMillis)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun setAlarm(timeInMillis: Long) {
        Log.println(Log.VERBOSE, "Alarm","Setting an alarm.")
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        Log.println(Log.VERBOSE, "Alarm", alarmManager.canScheduleExactAlarms().toString())
        if (alarmManager.canScheduleExactAlarms()) {
            Log.println(Log.VERBOSE, "Alarm","I got permission!")
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            Toast.makeText(this, "Alarm set for: ${timePicker.hour}:${timePicker.minute}", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendGet() {
        val url = URL("http://www.google.com/")

        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"  // optional default is GET

            println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                inputStream.bufferedReader().use {
                    it.lines().forEach { line ->
                        println(line)
                    }
                }
            } else {
                val reader: BufferedReader = inputStream.bufferedReader()
                var line: String? = reader.readLine()
                while (line != null) {
                    System.out.println(line)
                    line = reader.readLine()
                }
                reader.close()
            }
        }
    }

}
