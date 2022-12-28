package com.example.foregroundserviceapp

import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import java.io.IOException

class MyService : Service() {

    private val CHANNEL_ID = "ForegroundServiceChannel"
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Toast.makeText(this, "service is on", Toast.LENGTH_SHORT).show()
        //val input = intent.getStringExtra("AudioON")
        val songResource = intent.getIntExtra("SongResource", 0)
        val songName = intent.getStringExtra("SongName")

        createNotificationChannel()

        // show a notification in the notification bar
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music is Playing")
            .setContentText("$songName is playing")
            .setSmallIcon(R.drawable.ic_music)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)

        // do heavy work on a background thread
        val thread = Thread(
            Runnable {
                mediaPlayer = MediaPlayer.create(this, songResource)
                mediaPlayer.isLooping = true
                mediaPlayer.start()
                Thread.sleep(1000)
            }
        )
        thread.start()

        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        Toast.makeText(this, "service is off", Toast.LENGTH_SHORT).show()
        mediaPlayer.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
