package com.hazel.mediaservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.widget.SeekBar
import com.hazel.mediaservice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var mediaPlayerService: MediaPlayerService? = null
    private var isBound = false
    private lateinit var binding:ActivityMainBinding
    private val handler = Handler()
    private var isTracking = false
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MediaPlayerService.LocalBinder
            mediaPlayerService = binder.getService()
            isBound = true
            updateSeekBar()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mediaPlayerService = null
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = Intent(this, MediaPlayerService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
        controlSound()
        checkSeekBar()
    }

    private fun controlSound(){
        binding.ivPlay.setOnClickListener{
            mediaPlayerService?.play()
        }
        binding.ivPause.setOnClickListener{
            mediaPlayerService?.pause()
        }
        binding.ivStop.setOnClickListener{
            mediaPlayerService?.stop()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(connection)
        }
    }
    private fun updateSeekBar() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (mediaPlayerService != null) {
                    val currentPosition = mediaPlayerService!!.getCurrentPosition()
                    val totalDuration = mediaPlayerService!!.getTotalDuration()
                    val progress = (currentPosition.toFloat() / totalDuration * 100).toInt()
                    binding.seekBar.progress = progress
                }
                handler.postDelayed(this, 10) // Update every second
            }
        }, 0)
    }
    private fun checkSeekBar(){
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    // Update the media player position when SeekBar progress changes
                    val newPosition = progress * mediaPlayerService!!.getTotalDuration() / 100
                    mediaPlayerService?.seekTo(newPosition)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isTracking = true
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isTracking = false
            }
        })
    }

}