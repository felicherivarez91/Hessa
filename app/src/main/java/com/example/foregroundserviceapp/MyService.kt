package com.example.foregroundserviceapp

import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Intent
import android.media.MediaPlayer
import android.media.session.PlaybackState.ACTION_STOP
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat


class MyService : Service() {

    private val CHANNEL_ID = "ForegroundServiceChannel"
    private lateinit var mediaPlayer: MediaPlayer
     private var songResource :Int = 0
     private var songName :String = ""


    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val stopIntent = Intent(this, MyService::class.java).apply {
            action = ACTION_STOP.toString()
        }

        val stopPendingIntent: PendingIntent = PendingIntent.getService(this, 0, stopIntent, FLAG_IMMUTABLE)



        // show a notification in the notification bar
        val notificationIntent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, FLAG_IMMUTABLE
        )
                // terminate music and stop service



       val notificationLayout = RemoteViews(packageName, R.layout.notification_view)
        //val input = intent.getStringExtra("AudioON")
         songResource = intent.getIntExtra("SongResource", 0)
         songName = intent.getStringExtra("SongName").toString()
        notificationLayout.setTextViewText(R.id.tvContent,"Audio Name: $songName")
        notificationLayout.setOnClickPendingIntent(R.id.btnStop,stopPendingIntent)
     //   mediaPlayer = MediaPlayer.create(this, songResource)
       // mediaPlayer.isLooping = true
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

        if (intent != null){
            when(intent.action){
                ACTION_STOP.toString() ->{stopSound()
                    stopService(Intent(this, MyService::class.java))
                }
            }
        }

        return START_REDELIVER_INTENT
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
        stopSound()
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    // media functions
    private fun playSound() {
//         if (mediaPlayer == null) {
//             mediaPlayer= MediaPlayer()
//         }

        if (/*mediaPlayer != null && */ !mediaPlayer.isPlaying) {
            val resourceUri: Uri = Uri.parse("android.resource://$packageName/$songResource")
            mediaPlayer.reset()
            mediaPlayer.setDataSource(this,resourceUri)
            mediaPlayer.prepare()
            mediaPlayer.isLooping = true
            mediaPlayer.start()
        }

        //  }
    }

    private fun stopSound() {

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()
        mediaPlayer = MediaPlayer()

    //  stopService(Intent(this, MyService::class.java))
    //stop the service only when we click on the stop button and dont stop the service
    }




}

