package com.example.foregroundserviceapp

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foregroundserviceapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var playList: ArrayList<SongModel>? = null
    private var currentSongPosition = 0
    var serviceIntent : Intent? = null
    private lateinit var serviceClass: Class<MyService>
    private lateinit var services : List<ActivityManager.RunningServiceInfo>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

         serviceClass = MyService::class.java
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
         services = activityManager.getRunningServices(Int.MAX_VALUE)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playList = Playlist.defaultPlayList()
        displaySongView(currentSongPosition)

        // prepare gui
        if(isServiceAlive(serviceClass)){
            binding.ivPlayPause
        }
        binding.ivPlayPause.setOnClickListener {
            if (isServiceAlive(serviceClass)) {
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

        binding.ivPlayPause.setImageResource(R.drawable.ic_pause)
        serviceIntent = Intent(this, MyService::class.java)
       // serviceIntent.putExtra("AudioON", "Audio is playing")
        serviceIntent!!.putExtra("SongResource", playList?.get(currentSongPosition)?.getResourceId())
        serviceIntent!!.putExtra("SongName", playList?.get(currentSongPosition)?.getName())

       // ContextCompat.startForegroundService(this, serviceIntent!!)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService( serviceIntent!!)
            Toast.makeText(this,"service is ${isServiceAlive(serviceClass)}",Toast.LENGTH_SHORT).show()
        }

    }

    private fun stopService() {
        binding.ivPlayPause.setImageResource(R.drawable.ic_play)
         serviceIntent = Intent(this, MyService::class.java)
        stopService(serviceIntent)
        Toast.makeText(this,"service is ${isServiceAlive(serviceClass)}",Toast.LENGTH_SHORT).show()

    }

    private fun displaySongView(currentSongPosition: Int) {
        binding.tvSongName.text = playList?.get(currentSongPosition)?.getName()
        binding.ivSongImage.setImageResource(playList?.get(currentSongPosition)!!.getImage())
    }




    private fun isServiceAlive(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    //fix the giu
}
