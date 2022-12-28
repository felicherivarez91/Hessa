package com.example.foregroundserviceapp

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.foregroundserviceapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private val CHANNEL_ID = "ForegroundServiceChannel"
    private lateinit var binding: ActivityMainBinding
    private var serviceIsOn = false
    private var playList: ArrayList<SongModel>? = null
    private var currentSongPosition = 0
    var serviceIntent : Intent? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServices = activityManager.getRunningServices(Int.MAX_VALUE)

        val isServiceRunning = runningServices.any { service ->
            service.service.className == MyService::class.java.name
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playList = Playlist.defaultPlayList()
        displaySongView(currentSongPosition)

        binding.ivPlayPause.setOnClickListener {
            if (serviceIsOn) {
                stopService()
            } else {
                startService()
            }
        }

        binding.ivNext.setOnClickListener {
            if (currentSongPosition == playList!!.size - 1) {
                currentSongPosition = 0
                displaySongView(currentSongPosition)
            } else {
                currentSongPosition++
                displaySongView(currentSongPosition)
            }
        }

        binding.ivPrevious.setOnClickListener {
            if (currentSongPosition == 0) {
                currentSongPosition = playList!!.size - 1
                displaySongView(currentSongPosition)
            } else {
                currentSongPosition--
                displaySongView(currentSongPosition)
            }
        }
    }



    private fun startService() {

        serviceIsOn = true
        binding.ivPlayPause.setImageResource(R.drawable.ic_pause)
         serviceIntent = Intent(this, MyService::class.java)
       // serviceIntent.putExtra("AudioON", "Audio is playing")
        serviceIntent!!.putExtra("SongResource", playList?.get(currentSongPosition)?.getResourceId())
        serviceIntent!!.putExtra("SongName", playList?.get(currentSongPosition)?.getName())

          ContextCompat.startForegroundService(this, serviceIntent!!)

    }

    private fun stopService() {
        serviceIsOn = false
        binding.ivPlayPause.setImageResource(R.drawable.ic_play)
         serviceIntent = Intent(this, MyService::class.java)
        stopService(serviceIntent)
    }

    private fun displaySongView(currentSongPosition: Int) {
        binding.tvSongName.text = playList?.get(currentSongPosition)?.getName()
        binding.ivSongImage.setImageResource(playList?.get(currentSongPosition)!!.getImage())
    }





    override fun onDestroy() {
        super.onDestroy()

    }
}
