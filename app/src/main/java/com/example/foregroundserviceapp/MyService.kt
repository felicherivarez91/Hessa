package com.example.foregroundserviceapp

import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Intent
import android.media.MediaPlayer
import android.media.session.PlaybackState.ACTION_STOP
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.foregroundserviceapp.databinding.ActivityMainBinding

class MyService : Service() {

    private val CHANNEL_ID = "ForegroundServiceChannel"
    private lateinit var mediaPlayer: MediaPlayer


    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val stopIntent = Intent(this, MyService::class.java).apply {
            action = ACTION_STOP.toString()
        }
        val stopPendingIntent: PendingIntent = PendingIntent.getService(this, 0, stopIntent, FLAG_IMMUTABLE)

        if (intent != null){
            when(intent.action){
                ACTION_STOP.toString() ->{stopSound()}
            }
        }

        // show a notification in the notification bar
        val notificationIntent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, FLAG_IMMUTABLE
        )
                // terminate music and stop service



       val notificationLayout = RemoteViews(packageName, R.layout.notification_view)
        Toast.makeText(this, "service is on", Toast.LENGTH_SHORT).show()
        //val input = intent.getStringExtra("AudioON")
        val songResource = intent.getIntExtra("SongResource", 0)
        val songName = intent.getStringExtra("SongName")
        notificationLayout.setTextViewText(R.id.tvContent,"Audio Name: $songName")
        notificationLayout.setOnClickPendingIntent(R.id.btnStop,stopPendingIntent)
        mediaPlayer = MediaPlayer.create(this, songResource)
        mediaPlayer.isLooping = true
//
        createNotificationChannel()
//


        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
           .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
           // .setContentTitle("Music is Playing")
           // .setContentText("$songName is playing")
            .setSmallIcon(R.drawable.ic_music)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            //.addAction(0, "Stop Music",stopMusicIntent )
            .build()
//
        startForeground(1, notification)

        // creating a mediaplayer

        playSound()

        // do heavy work on a background thread
        //  val thread = Thread(
        // Runnable {
        //   mediaPlayer = MediaPlayer.create(this, songResource)
        //   mediaPlayer.isLooping = false
        //   mediaPlayer.start()
        //      Thread.sleep(1000)
        //   }
        //  )
        //  thread.start()

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
//
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "service is off", Toast.LENGTH_SHORT).show()
        stopSound()
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    // media functions
    private fun playSound() {
        // if (mediaPlayer == null) {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }

        //  }
    }

    private fun stopSound() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer!!.release()
        }
    }
}

