package com.example.foregroundserviceapp

object Playlist {

    fun defaultPlayList(): ArrayList<SongModel>
    {
        val playlist = ArrayList<SongModel>()
        val rain = SongModel(1,"Rain Drops",R.raw.water, R.drawable.rain)
        val birds = SongModel(2,"Birds",R.raw.birds, R.drawable.bird)
        playlist.add(rain)
        playlist.add(birds)
        return playlist
    }
}
