package com.example.iot2
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val ipAddress = "192.168.18.34"
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnUp = findViewById<Button>(R.id.btn_up)
        val btnDown = findViewById<Button>(R.id.btn_down)
        val btnLeft = findViewById<Button>(R.id.btn_left)
        val btnRight = findViewById<Button>(R.id.btn_right)
        val btnStop = findViewById<Button>(R.id.btn_stop)
        val speedSlider = findViewById<SeekBar>(R.id.speed_slider)
        val lockSwitch = findViewById<Switch>(R.id.lock_switch)

        // Kirim perintah ke ESP8266
        fun sendCommand(command: String) {
            val url = "http://$ipAddress/$command"
            val request = Request.Builder().url(url).build()

            client.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    response.body?.close()
                }
            })
        }

        // Tombol kontrol
        btnUp.setOnClickListener { sendCommand("forward") }
        btnDown.setOnClickListener { sendCommand("backward") }
        btnLeft.setOnClickListener { sendCommand("left") }
        btnRight.setOnClickListener { sendCommand("right") }
        btnStop.setOnClickListener { sendCommand("stop") }

        // Slider kecepatan
        speedSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                sendCommand("speed=$progress")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Switch lock
        lockSwitch.setOnCheckedChangeListener { _, isChecked ->
            sendCommand(if (isChecked) "lock" else "unlock")
        }
    }
}
